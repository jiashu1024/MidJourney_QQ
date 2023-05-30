package com.zjs.mj.util;

import com.zjs.mj.config.Properties;
import com.zjs.mj.entity.AuthenticationResult;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.entity.SubmitResult;
import com.zjs.mj.entity.Task;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.processors.qqProcessor.ChatProcessor;
import com.zjs.mj.service.MjService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 任务池
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TaskPool {

    private final MjService service;

    private final Properties properties;

    private final TaskMapper taskMapper;

    private TaskLimitSet set;

    /**
     * fast任务队列
     */
    private LinkedBlockingQueue<Task> fastQueue;

    /**
     * relax任务队列
     */
    private LinkedBlockingQueue<Task> relaxQueue;

    /**
     * 默认向mj发起请求的时间间隔
     * 发请求间隔太短会被限流
     */
    private static final long DEFAULT_SLEEP_TIME = 3;

    /**
     * 当前向mj发起请求的时间间隔
     * 如果被mj限流，该值会被调大
     */
    private static long currentSleepTime = DEFAULT_SLEEP_TIME;
    private int limitTaskCount = 0;
    private final Semaphore  semaphore = new Semaphore(1);

    @PostConstruct
    void init() {
        int plan = properties.getMj().getPlan();
        int MAX_FAST_TASKS;
        //用于mid journey的订阅来调整任务队列的大小
        if (plan == 4) {
            MAX_FAST_TASKS = 22;
        } else {
            MAX_FAST_TASKS = 13;
        }
        int MAX_RELAX_TASKS = 3;
        fastQueue = new LinkedBlockingQueue<>(MAX_FAST_TASKS);
        relaxQueue = new LinkedBlockingQueue<>(MAX_RELAX_TASKS);

        set = new TaskLimitSet(MAX_FAST_TASKS + MAX_RELAX_TASKS);
    }




    public void work() {
        while (true) {
            try {
                // 如果任务队列没有任务就休眠 避免空轮询
                semaphore.acquire();
            } catch (InterruptedException ignored) {;
            }
            Task task = null;
            // 优先执行fast任务
            if (!fastQueue.isEmpty()) {
                try {
                    task = fastQueue.take();
                } catch (InterruptedException e) {
                    continue;
                }
            }
            if (task == null && !relaxQueue.isEmpty()) {
                try {
                    task = relaxQueue.take();
                } catch (InterruptedException e) {
                    continue;
                }
            }

            if (task != null) {
                log.info("开始执行任务:{}", task);
                try {

                    task.run(service,set,taskMapper);

                    long time = set.getSleepTime();
                    if(time != DEFAULT_SLEEP_TIME && limitTaskCount == 0) {
                        limitTaskCount = fastQueue.size() + relaxQueue.size();
                        set.setSleepTime(DEFAULT_SLEEP_TIME);
                        currentSleepTime = time;
                    }
                    if(limitTaskCount > 0) {
                        limitTaskCount--;
                    } else {
                        currentSleepTime = DEFAULT_SLEEP_TIME;
                    }
                    log.debug("任务请求间隔：{}s", currentSleepTime);
                    TimeUnit.SECONDS.sleep(currentSleepTime);
                } catch (Exception e) {
                    log.error("任务{}执行失败: {}", task, e.getMessage());
                    task.setStatus(TaskStatus.FAILED);
                    task.setDescription(e.getMessage());
                    taskMapper.updateById(task);
                    task.notifyUser();
                }
            }
        }
    }

    /**
     * 提交任务
     * @param task
     * @return
     */
    public SubmitResult submitTask(Task task) {
        //处理翻译失败的任务
        if (task.getStatus() == TaskStatus.FAILED) {
            return SubmitResult.failure(task.getDescription());
        }

        // 任务提交前检查系统负载
        long now = System.currentTimeMillis();
        if (now <= set.getLimitTime()) {
            task.setStatus(TaskStatus.FAILED);
            task.setDescription("系统负载过大，拒绝任务提交");
            log.info("系统负载过大，拒绝任务提交");
            return SubmitResult.failure("系统负载过大,请60s后重试");
        }

        // 任务提交前鉴权
        ChatProcessor processor = task.getProcessor();
        AuthenticationResult authenticationResult = processor.checkAuth(task.getEvent(), task);
        if(!authenticationResult.isOk()) {
            task.setStatus(TaskStatus.FAILED);
            task.setDescription(authenticationResult.getMessage());
            return SubmitResult.failure(authenticationResult.getMessage());
        }

        ImagineMode mode = task.getMode();
        LinkedBlockingQueue<Task> queue;
        if (mode.equals(ImagineMode.FAST)) {
            queue = fastQueue;
        } else {
            queue = relaxQueue;
        }
        if (queue == null) {
            queue = relaxQueue;
        }
        if (queue.offer(task)) {
            String message = task.getMode() + "任务提交成功";
            task.setDescription(message);
            semaphore.release();
            return SubmitResult.success(message);
        } else {
            task.setStatus(TaskStatus.FAILED);
            task.setDescription("频率限制,请稍后重试");
            return SubmitResult.failure("频率限制,请稍后重试");
        }
    }


}
