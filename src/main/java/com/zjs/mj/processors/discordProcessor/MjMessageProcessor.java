package com.zjs.mj.processors.discordProcessor;

import cn.hutool.core.text.CharSequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjs.mj.entity.Task;
import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.util.ConvertUtils;
import com.zjs.mj.util.MessageData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class MjMessageProcessor implements MessageProcessor {

    private final TaskMapper taskMapper;

    @Override
    public void onMessageReceived(Message message) {
        if (MessageType.SLASH_COMMAND.equals(message.getType()) || MessageType.DEFAULT.equals(message.getType())) {
            processImagineMessageReceived(message);
        } else if (MessageType.INLINE_REPLY.equals(message.getType()) && message.getReferencedMessage() != null) {
            processUVMessageReceived(message);
        }
    }

    @Override
    public void onMessageUpdate(Message message) {

        if (message.getInteraction() != null && "describe".equals(message.getInteraction().getName())) {
            //this.describeMessageHandler.onMessageUpdate(message);
        } else {
            processUVMessageUpdate(message);
        }
    }

    private void processImagineMessageReceived(Message message) {
        //System.out.println(message.getId());
        MessageData messageData = ConvertUtils.matchImagineContent(message.getContentRaw());
        if (messageData == null) {
            return;
        }
        String taskId = ConvertUtils.findTaskIdByFinalPrompt(messageData.getPrompt());
        if (taskId == null) {
            return;
        }
        Task task = taskMapper.selectById(taskId);

        task.setRequestId(message.getId());
        if ("Waiting to start".equals(messageData.getStatus())) {
            log.info("task[{}] : waiting to start ", task.getTaskId());
            task.setStatus(TaskStatus.RUNNING);
        } else {
            finishTask(task, message);
        }
        task.setPosted(true);

        task.notifyUser();
        //  notifyUser(task);
        taskMapper.updateById(task);
    }

    private void processUVMessageUpdate(Message message) {
        String content = message.getContentRaw();
        MessageData data = ConvertUtils.matchImagineContent(content);
        if (data == null) {
            data = ConvertUtils.matchUVContent(content);
        } else {
            String taskId = ConvertUtils.findTaskIdByFinalPrompt(data.getPrompt());
            if (CharSequenceUtil.isBlank(taskId)) {
                return;
            }
            Task task = taskMapper.selectById(taskId);
            if (task == null) {
                return;
            }
            if (!task.getDescription().equals("100%")) {
                log.info("task[{}] : {}", task.getTaskId(), data.getStatus());
                task.setDescription(data.getStatus());
            }

            taskMapper.updateById(task);
            return;
        }
        if (data == null) {
            return;
        }
        String relatedTaskId = ConvertUtils.findTaskIdByFinalPrompt(data.getPrompt());
        if (CharSequenceUtil.isBlank(relatedTaskId)) {
            return;
        }


    }

    private void processUVMessageReceived(Message message) {
        MessageData messageData = ConvertUtils.matchUVContent(message.getContentRaw());
        if (messageData == null) {
            return;
        }
        String taskId = ConvertUtils.findTaskIdByFinalPrompt(messageData.getPrompt());
        if (taskId == null) {
            return;
        }


        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return;
        }

        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        if (messageData.getAction() == Action.UPSCALE) {
            queryWrapper.eq(Task::getRootTaskId, task.getTaskId())
                    .eq(Task::getAction, messageData.getAction())
                    .eq(Task::getFinalPrompt, messageData.getIndex());
        }
        if (messageData.getAction() == Action.VARIATION) {

            queryWrapper.eq(Task::getRootTaskId, task.getTaskId())
                    .eq(Task::getAction, messageData.getAction())
                    .notIn(Task::getStatus, TaskStatus.FAILED, TaskStatus.SUCCESS);
        }

        Task uvTask = taskMapper.selectOne(queryWrapper);

        if (uvTask == null) {
            return;
        }
        uvTask.setRequestId(message.getId());
        finishTask(uvTask, message);
        uvTask.notifyUser();
        // notifyUser(uvTask);

        taskMapper.updateById(uvTask);
    }


    public void finishTask(Task task, Message message) {
        task.setFinishTime(LocalDateTime.now());
        if (!message.getAttachments().isEmpty()) {
            task.setStatus(TaskStatus.SUCCESS);
            task.setDescription("100%");
            log.info("task[{}] : 100%", task.getTaskId());

            String imageUrl = message.getAttachments().get(0).getUrl();
            task.setImageUrl(imageUrl);
            int hashStartIndex = imageUrl.lastIndexOf("_");
            task.setMessageHash(CharSequenceUtil.subBefore(imageUrl.substring(hashStartIndex + 1), ".", true));
        } else {
            task.setStatus(TaskStatus.FAILED);
        }
    }


}
