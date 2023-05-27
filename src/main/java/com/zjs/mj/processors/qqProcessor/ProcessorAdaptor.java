package com.zjs.mj.processors.qqProcessor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjs.mj.constant.UserRole;
import com.zjs.mj.entity.AuthenticationResult;
import com.zjs.mj.entity.SubmitResult;
import com.zjs.mj.entity.Task;
import com.zjs.mj.entity.dto.DefaultRoleCount;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.DefaultRoleCountMapper;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.mapper.UserMapper;
import com.zjs.mj.util.DateTimeUtil;
import com.zjs.mj.util.TaskPool;
import com.zjs.mj.util.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProcessorAdaptor implements ChatProcessor {

    protected final UserUtil userUtil;
    protected final UserMapper userMapper;
    protected final TaskMapper taskMapper;
    protected final TaskPool taskPool;

//    public ProcessorAdaptor(UserUtil userUtil, UserMapper userMapper, TaskMapper taskMapper, TaskPool taskPool) {
//        this.userUtil = userUtil;
//        this.userMapper = userMapper;
//        this.taskMapper = taskMapper;
//        this.taskPool = taskPool;
//    }


    @Override
    public AuthenticationResult checkAuth(Event event, Task task) {
        User user = task.getUser();
        if (user.getRole().equals(UserRole.ADMIN)) {
            return AuthenticationResult.ok();
        }
        //判断plus用户是否过期，过期就降级为normal用户
        userUtil.checkPlusUserExpire(user);
        //判断用户是否还有操作次数
        ImagineMode mode = task.getMode();
        if (mode.equals(ImagineMode.FAST)) {
            if (user.getFastCount() > 0) {
                if (DateTimeUtil.expire(user.getFastExpireTime())) {
                    return AuthenticationResult.failure(user.getRole() + "  订阅过期");
                }
                return AuthenticationResult.ok();
            }
            return AuthenticationResult.failure("fast模式次数不足");
        } else if (mode.equals(ImagineMode.RELAX)) {
            if (user.getRelaxCount() > 0) {
                if (DateTimeUtil.expire(user.getRelaxExpireTime())) {
                    return AuthenticationResult.failure(user.getRole() + "  订阅过期");
                }
                return AuthenticationResult.ok();
            }
            return AuthenticationResult.failure("relax模式次数不足");
        }

        return AuthenticationResult.failure("未授权");
    }

    @Override
    public void process(Event event) {
    }

    protected boolean expire(Event event) {
        //如果这个事件的时间比当前时间早10s以上，就不处理
        //(说明这个消息是机器人启动之前发的)
        MessageEvent messageEvent = (MessageEvent) event;
        int time = messageEvent.getTime();
        long now = System.currentTimeMillis() / 1000;
        return time < now - 30;
    }

    protected void processImagineRequest(MessageEvent messageEvent, MessageChain chain, User user, Event event, MessageChainBuilder builder, String prompt) {


        Task task = new Task().build(Action.IMAGINE, prompt, user, chain, event, this);
        task.setRootTaskId(task.getTaskId());
        taskMapper.insert(task);
        SubmitResult submitResult = taskPool.submitTask(task);
        MessageChain reply = builder.append(submitResult.getMessage()).build();
        messageEvent.getSubject().sendMessage(reply);
    }

    protected void processUvRequest(MessageChain chain, MessageEvent messageEvent, MessageChainBuilder builder, QuoteReply quoteReply, User user, Event event,String command) {

        //用于处理引用回复的作图请求 例如UV操作需要引用源imagine的消息
//        MessageContent messageContent = chain.get(PlainText.Key);
//        if (messageContent == null) {
//            builder.append("请发送正确的指令").build();
//            messageEvent.getSubject().sendMessage(builder.build());
//            return;
//        }

        command = command.replace(" ", "");
        command = command.toUpperCase();
        if (command.startsWith("U") || command.startsWith("V")) {
            try {
                int index = Integer.parseInt(command.substring(1));
                if (index > 4 || index <= 0) {
                    builder.append("index 错误").build();
                    messageEvent.getSubject().sendMessage(builder.build());
                    return;
                }
                MessageSource source = quoteReply.getSource();
                String sourceKey = Task.createSourceKey(source);
                log.debug("QuoteReply message find task from sourceKey:{}", sourceKey);
                LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Task::getSourceKey, sourceKey);
                //获取引用消息对应的任务
                Task task = taskMapper.selectOne(queryWrapper);
                if (task == null) {
                    log.debug("can not find task from sourceKey:{} failed", sourceKey);
                    builder.append("回复的任务系统未存储").build();
                    messageEvent.getSubject().sendMessage(builder.build());
                    return;
                }
                //创建UV任务
                Action action;
                Task uvTask = new Task();
                uvTask.setMode(task.getMode());
                uvTask.setRelatedTaskId(task.getTaskId());
                uvTask.setRequestId(task.getRequestId());
                uvTask.setMessageHash(task.getMessageHash());
                uvTask.setDeleted(task.isDeleted());
                if (command.startsWith("U")) {
                    action = Action.UPSCALE;
                } else {
                    action = Action.VARIATION;
                }
                uvTask.setMode(task.getMode());
                uvTask.setRootTaskId(task.getRootTaskId());
                uvTask.setFinalPrompt(String.valueOf(index));
                uvTask.build(action, "", user, chain, event, this);

                //如果U任务的源任务已经成功了，那么就不需要再次提交了
                //U操作再次提交会mj会报错，不会重新生成，事件不好监听，所以直接拒绝
                //V操作允许重复提交，因为V操作是可以多次提交的
                if (uvTask.getAction().equals(Action.UPSCALE)) {
                    LambdaQueryWrapper<Task> containSameSuccessTaskWrapper = new LambdaQueryWrapper<>();
                    containSameSuccessTaskWrapper.eq(Task::getRelatedTaskId, uvTask.getRelatedTaskId())
                            .eq(Task::getAction, uvTask.getAction())
                            .eq(Task::getFinalPrompt, uvTask.getFinalPrompt())
                            .eq(Task::getStatus, TaskStatus.SUCCESS);
                    Task successSameTask = taskMapper.selectOne(containSameSuccessTaskWrapper);
                    if (successSameTask != null) {
                        builder.append("该任务已经成功了，不需要再次提交").build();
                        messageEvent.getSubject().sendMessage(builder.build());
                        return;
                    }
                } else {
                    //对于V操作，对同一个rootTaskId，只能有一个V操作在进行
                    LambdaQueryWrapper<Task> containSameVTaskWrapper = new LambdaQueryWrapper<>();
                    containSameVTaskWrapper.eq(Task::getRootTaskId, uvTask.getRootTaskId())
                            .eq(Task::getAction, uvTask.getAction())
                            .in(Task::getStatus, TaskStatus.WAITING, TaskStatus.RUNNING);
                    Task sameVTask = taskMapper.selectOne(containSameVTaskWrapper);
                    if (sameVTask != null) {
                        log.warn("同一时间只能对同一个rootTaskId进行一次V操作，当前任务id为{}", uvTask.getTaskId());
                        builder.append("等待上次V操作成功后重新申请").build();
                        messageEvent.getSubject().sendMessage(builder.build());
                        return;
                    }
                }

                //删除数据库里对UV对应失败的任务(waiting 或者running状态，没有正常结束的任务)
                //失败的原因 可能是中途退出系统，网络异常等，需要删除任务记录
                //因为UV作图成功后，是靠action,imagine task id,index来查找生成的UV任务
                //如果之前有未完成的任务，那会导致最后数据保存数据库时，有两个相同的任务，一个成功，一个失败
                LambdaQueryWrapper<Task> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(Task::getRelatedTaskId, uvTask.getRelatedTaskId())
                        .eq(Task::getAction, uvTask.getAction())
                        .eq(Task::getFinalPrompt, uvTask.getFinalPrompt())
                        .notIn(Task::getStatus, TaskStatus.SUCCESS, TaskStatus.FAILED);
                taskMapper.delete(deleteWrapper);

                taskMapper.insert(uvTask);
                SubmitResult submitResult = taskPool.submitTask(uvTask);
                MessageChain reply = builder.append(submitResult.getMessage()).build();
                messageEvent.getSubject().sendMessage(reply);

            } catch (NumberFormatException e) {
                builder.append("请发送正确的指令").build();
                messageEvent.getSubject().sendMessage(builder.build());
            }


        }
    }


}
