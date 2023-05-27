package com.zjs.mj.util;

import com.zjs.mj.entity.Task;
import com.zjs.mj.enums.TaskStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 用来辅助检查任务被MJ限流率
 * 限流过高则限制提交任务
 */
@Slf4j
public class TaskLimitSet {
    private final Set<Task> taskLimitSet;
    /**
     * 用于监控任务提交量的set大小，与fastQueue和relaxQueue的大小相关
     */
    private final int setMaxSize;

    /**
     * 限流时间，在这事件之前的任务将被拒绝
     */
    private volatile long limitTime;

    /**
     * 系统负载过高，则调大该值，增大每个任务与每个任务之间向MJ的提交间隔
     */
    private volatile long sleepTime = 3;

    public TaskLimitSet(int max) {
        taskLimitSet = new HashSet<>();
        setMaxSize = max;
    }

    /**
     * 添加任务到限流集合
     * 如果最近提交的任务被mj限流，则调整提交间隔
     * @param task 任务
     */
    public void addTaskLimitSet(Task task) {
        int busyCount = 0;
        for (Task one : taskLimitSet) {
            if(one.getStatus() == TaskStatus.FAILED) {
                if (one.getResult().getCode() == 429) {
                    //例如发起imagine请求时，MJ返回429错误码，说明MJ限流了
                    busyCount++;
                }
            }
        }
        double percent = (double) busyCount / setMaxSize;
        log.info("当前系统负载:{}%", percent*100);
        if (percent >= 0.4) {
            log.info("被MidJourney限流,将拒绝30s内所有的任务提交");
            limitTime = System.currentTimeMillis() + 1000 * 30;
            sleepTime = 8; //调大任务与任务执行之间的间隔 尽量保证不会被限流
            taskLimitSet.clear();
        }

        if (taskLimitSet.size() >= setMaxSize) {
            //只根据最近的任务来判断是否被限流，满了就清空
            taskLimitSet.clear();
        }
        this.taskLimitSet.add(task);
    }

    public long getLimitTime() {
        return limitTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
}
