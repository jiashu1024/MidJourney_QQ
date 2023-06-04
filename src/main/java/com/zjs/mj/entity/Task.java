package com.zjs.mj.entity;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.zjs.mj.constant.UserRole;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.processors.qqProcessor.ChatProcessor;
import com.zjs.mj.processors.qqProcessor.ProcessorAdaptor;
import com.zjs.mj.service.MjService;
import com.zjs.mj.service.UserService;
import com.zjs.mj.util.AliTranslate;
import com.zjs.mj.util.FileUtil;
import com.zjs.mj.util.MessageChainTypeHandler;
import com.zjs.mj.util.TaskLimitSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.MessageSerializers;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.QuoteReply;

import java.io.File;
import java.time.LocalDateTime;

@Data
@Slf4j
@TableName(value = "task", autoResultMap = true)
public class Task {
    @TableId
    private String taskId;
    /**
     * fast或者relax
     */
    private ImagineMode mode;
    /**
     * 作图在discord中的message id
     */
    private String requestId;

    /**
     * 作图的类型
     */
    private Action action;

    /**
     * qq上输入的prompt 不包含taskId
     */
    @TableField(exist = false)
    private String prompt;
    /**
     * 翻译后的prompt
     */
    @TableField(exist = false)
    private String promptEn;
    /**
     * 最终的prompt 包含taskId，英文，去除一些参数(--fast --relax)官方不支持，仅作为用户命令
     */
    private String finalPrompt;
    /**
     * 结果图片url
     */
    private String imageUrl;

    /**
     * action后提交mj请求的结果
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private MjRequestResult<Void> result;
    /**
     * 任务的状态
     */
    private TaskStatus status;
    /**
     * 任务的描述
     */
    private String description;
    @TableField(typeHandler = JacksonTypeHandler.class)
    /**
     * 对应发起任务的user
     */
    private User user;
    /**
     * 对应发起请求的processor
     */
    @TableField(exist = false)
    private ChatProcessor processor;
    /**
     * 对应发起请求的qq的event
     */
    @TableField(exist = false)
    private Event event;
    /**
     * 对应发起请求的qq的消息的源，用这个能用来回复对应的用户消息
     */
    @TableField(typeHandler = MessageChainTypeHandler.class)
    private MessageChain messageChain;
    /**
     * 任务创建时间
     */
    private LocalDateTime createTime;
    /**
     * 任务完成时间
     */
    private LocalDateTime finishTime;
    /**
     * 对应mid journey中任务的id (用于后续发起UV请求)
     */
    private String messageHash;

    /**
     * 任务来源的key，当用户引用消息回复UV操作时，根据该成员找到对应的imagine任务
     */
    private String sourceKey;
    /**
     * 该任务相关的任务id，UV操作对应的Imagine任务id
     */
    private String relatedTaskId;
    /**
     * 对V出来的任务再进行UV操作时，根任务的id，用于将discord收到结果事件与task关联
     */
    private String rootTaskId;
    /**
     * 对于一些违规的作图，discord里面的作图结果会被删除，变成私发
     * 对于这种消息，需要根据该值，变换uv操作的请求参数
     */
    private boolean isDeleted;

    private boolean posted;


    public Task() {
    }

    public Task build(Action action, String prompt, User user, MessageChain chain, Event event, ChatProcessor processor) {
        return build(RandomUtil.randomNumbers(16), action, prompt, user, chain, event, processor);
    }

    public Task build(Action action, String prompt, User user, MessageChain chain, Event event, ChatProcessor processor, String imageUrl) {
        return build(RandomUtil.randomNumbers(16), action, prompt, user, chain, event, processor, imageUrl);
    }

    public Task build(String taskId, Action action, String prompt, User user, MessageChain chain, Event event, ChatProcessor processor, String padImagineUrl) {
        this.taskId = taskId;
        this.action = action;
        this.prompt = prompt;
        this.status = TaskStatus.CREATED;
        this.user = user;
        this.messageChain = chain;
        this.event = event;
        this.processor = processor;
        this.createTime = LocalDateTime.now();

        if (prompt.contains("--fast")) {
            prompt = prompt.replace("--fast", "");
            this.mode = ImagineMode.FAST;
        } else if (prompt.contains("--relax")) {
            prompt = prompt.replace("--relax", "");
            this.mode = ImagineMode.RELAX;
        } else {
            if (user.getRole().equals(UserRole.ADMIN)) {
                //管理员默认使用fast模式
                this.mode = ImagineMode.FAST;
            } else if (user.getRole().equals(UserRole.PLUS)) {
                //plus用户默认使用fast模式，但是如果fast次数用完了，就使用relax模式
                if (user.getFastCount() > 0) {
                    this.mode = ImagineMode.FAST;
                } else {
                    this.mode = ImagineMode.RELAX;
                }
            } else {
                //普通用户默认使用relax模式
                this.mode = ImagineMode.RELAX;
            }
        }
        //如果是upscale或者variation，不需要翻译
        if (this.action.equals(Action.UPSCALE) || this.action.equals(Action.VARIATION)) {
            return this;
        }
        String realPrompt = prompt;
        String param = "";
        if (prompt.contains("--")) {
            realPrompt = prompt.substring(0, prompt.indexOf("--"));
            param = prompt.substring(prompt.indexOf("--"));
            param = buildCommandPara(param);
        }

        try {
            AliTranslate.TranslateResult result = AliTranslate.translate(realPrompt);
            this.setPromptEn(result.translate);
        } catch (Exception e) {
            this.status = TaskStatus.FAILED;
            this.description = "翻译失败";
        }
        this.finalPrompt = "[" + this.taskId + "]  " + padImagineUrl + "  " + this.promptEn + "   " + param;
        return this;
    }

    public Task build(String taskId, Action action, String prompt, User user, MessageChain chain, Event event, ChatProcessor processor) {
        return build(taskId, action, prompt, user, chain, event, processor, "");
    }

    private String buildCommandPara(String param) {
        if (param.contains("--iw")) {
            param = param.replace("--iw", "--iw ");
        }

        return param;
    }


    /**
     * 每个Task的执行逻辑
     *
     * @param service
     * @param set
     * @param mapper
     */
    public void run(MjService service, TaskLimitSet set, TaskMapper mapper) {
        //只有created的任务才能执行
        //翻译失败的任务不能被执行
        if (status.equals(TaskStatus.CREATED)) {
            MjRequestResult<Void> result = null;
            if (this.action.equals(Action.IMAGINE) || this.action.equals(Action.PAD_IMAGINE)) {
                result = service.imagine(this.finalPrompt, this.mode);
            } else if (this.action.equals(Action.UPSCALE)) {
                result = service.upscale(this.finalPrompt, this.messageHash, this.requestId, this.isDeleted);
            } else if (this.action.equals(Action.VARIATION)) {
                result = service.variation(this.finalPrompt, this.messageHash, this.mode, this.requestId, this.isDeleted);
            }
            //TODO: describe等命令支持
            if (result != null && result.isSuccess()) {
                this.status = TaskStatus.WAITING;
                this.setResult(result);
            } else {
                this.status = TaskStatus.FAILED;
                if (result != null) {
                    this.description = result.getDescription();
                }
                this.setResult(result);
                notifyUser();
            }
            mapper.updateById(this);
        }
        set.addTaskLimitSet(this);
    }

    /**
     * 用于创建消息的key，用于后续根据消息找到对应的任务
     *
     * @param source qq消息来源
     * @return
     */
    public static String createSourceKey(MessageSource source) {

        //使用targetId+time作为sourceKey 有可能出现时间不一致
        //使用targetId + ids，不同设备上ids不一致
        long id = source.getFromId();
        // int time = source.getTime()+1;
        int internalId = source.getInternalIds()[0];
        return id + "_" + internalId;
    }

    /**
     * 减少用户的fast或者relax次数
     */
//    public void decreaseCount() {
//        if (this.user.getRole().equals(UserRole.ADMIN)) {
//            return;
//        }
//        if (this.mode == ImagineMode.FAST) {
//            this.user.setFastCount(this.user.getFastCount() - 1);
//        } else {
//            this.user.setRelaxCount(this.user.getRelaxCount() - 1);
//        }
//    }

    /**
     * 用于任务完成后通知给用户
     */
    public void notifyUser() {
        Bot bot = Bot.getInstances().get(0);
        Action action = this.getAction();
        TaskStatus status = this.getStatus();
        MessageChain chain = this.getMessageChain();
        MessageSource messageSource = chain.get(MessageSource.Key);
        Contact contact = null;
        long qq = messageSource.getFromId();
        contact = bot.getFriend(qq);
        if (messageSource.getTargetId() != bot.getId()) {
            long group = messageSource.getTargetId();
            Group botGroup = bot.getGroup(group);
            if (botGroup != null) {
                contact = botGroup;
            }
        }
        if (contact == null) {
            log.warn("contact null");
            return;
        }
        MessageChainBuilder builder = new MessageChainBuilder().append(new QuoteReply(messageSource));

        if (status.equals(TaskStatus.SUCCESS)) {
            if (action.equals(Action.IMAGINE) || action.equals(Action.PAD_IMAGINE)) {
                sendResultNeedStorage(this, contact, builder);
            } else if (action.equals(Action.UPSCALE)) {
                File file = null;
                try {
                    file = FileUtil.downloadFile(this.getImageUrl());
                } catch (Exception e) {
                    log.error("download file error ", e);
                    builder.append("图片下载失败").build();
                    contact.sendMessage(builder.build());
                    return;
                }
                log.info("begin to send image to {}", contact.getId());
                MessageReceipt<Contact> receipt = Contact.sendImage(contact, file);
                this.setSourceKey(Task.createSourceKey(receipt.getSource()));
                log.info("send image to {} success", contact.getId());
                builder.append("图片放大成功").build();
                contact.sendMessage(builder.build());
            } else if (action.equals(Action.VARIATION)) {
                sendResultNeedStorage(this, contact, builder);
            }

        } else if (status.equals(TaskStatus.RUNNING)) {
            //  if (action.equals(Action.IMAGINE)) {
            MessageChain build = builder.append(new QuoteReply(messageSource)).append("正在生成图片，请稍后……").build();
            contact.sendMessage(build);
            // }
        } else if (status.equals(TaskStatus.FAILED)) {
            builder.append("图片生成失败，原因：").append(this.getDescription()).build();
            contact.sendMessage(builder.build());
        }

    }

    /**
     * 发送任务结果，需要将图片存储到本地
     * 机器人给用户发送完imagine结果的图片后，需要记住这个发送消息的源，用于后续uv操作根据这个key找到对应的imagine任务，用于携带参数发起uv请求
     *
     * @param task
     * @param contact
     * @param builder
     */
    private void sendResultNeedStorage(Task task, Contact contact, MessageChainBuilder builder) {
        File file = null;
        try {
            file = FileUtil.downloadFile(task.getImageUrl());
        } catch (Exception e) {
            log.error("image download error", e);
            builder.append("服务器下载结果图片失败").build();
            contact.sendMessage(builder.build());
            return;
        }

        log.info("begin to send image to {}", contact.getId());
        MessageReceipt<Contact> receipt = Contact.sendImage(contact, file);
        log.info("send image to {} success", contact.getId());


        task.setSourceKey(Task.createSourceKey(receipt.getSource()));
        builder.append("引用图片回复：\n")
                .append("U1 U2 U3 U4放大图片\n")
                .append("V1 V2 V3 V4扩展风格")
                .build();
        contact.sendMessage(builder.build());
        UserService.decreaseCount(task);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", mode=" + mode +
                ", requestId='" + requestId + '\'' +
                ", action=" + action +
                ", prompt='" + prompt + '\'' +
                ", promptEn='" + promptEn + '\'' +
                ", finalPrompt='" + finalPrompt + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", result=" + result +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", user=" + user +
                ", processor=" + processor +
                ", event=" + event +
                ", messageChain=" + messageChain +
                ", createTime=" + createTime +
                ", finishTime=" + finishTime +
                ", messageHash='" + messageHash + '\'' +
                ", sourceKey='" + sourceKey + '\'' +
                ", relatedTaskId='" + relatedTaskId + '\'' +
                ", rootTaskId='" + rootTaskId + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
