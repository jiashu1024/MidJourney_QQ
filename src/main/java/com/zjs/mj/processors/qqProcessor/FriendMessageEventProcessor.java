package com.zjs.mj.processors.qqProcessor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjs.mj.constant.UserRole;
import com.zjs.mj.entity.SubmitResult;
import com.zjs.mj.entity.dto.User;
import com.zjs.mj.enums.Action;
import com.zjs.mj.entity.Task;
import com.zjs.mj.enums.ImagineMode;
import com.zjs.mj.enums.TaskStatus;
import com.zjs.mj.mapper.ImageMessageMapper;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.mapper.UserMapper;
import com.zjs.mj.util.TaskPool;
import com.zjs.mj.util.UserUtil;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.modules.SerializersModule;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FriendMessageEventProcessor extends ProcessorAdaptor {
    public FriendMessageEventProcessor(UserUtil userUtil, UserMapper userMapper, TaskMapper taskMapper, TaskPool taskPool, ImageMessageMapper imageMessageMapper) {
        super(userUtil, userMapper, taskMapper, taskPool, imageMessageMapper);
    }


//    private final TaskPool taskPool;
//    private final TaskMapper taskMapper;

//    public FriendMessageEventProcessor(UserUtil userUtil) {
//        super(userUtil);
////        this.taskPool = taskPool;
////        this.taskMapper = taskMapper;
//    }

    @Override
    public void process(Event event) {


        if (expire(event)) {
            return;
        }

        MessageEvent messageEvent = (MessageEvent) event;
        long qq = messageEvent.getSender().getId();
        MessageChain chain = messageEvent.getMessage();

        processStorageImageMessage(messageEvent);



        QuoteReply quoteReply = chain.get(QuoteReply.Key);
        User user = userUtil.getUser(String.valueOf(qq));
        MessageChainBuilder builder = new MessageChainBuilder().append(new QuoteReply(chain));

        if (quoteReply == null) {

            processImagineMatch(chain,builder,messageEvent,user,event);
//            Image image = chain.get(Image.Key);
//            if (image != null) {
//                return;
//            }

//            MessageContent messageContent = chain.get(PlainText.Key);
//            String prompt = messageContent.contentToString();
//            //处理非引用回复的作图请求 私聊直接发prompt即可作图
//            processImagineRequest(messageEvent, chain, user, event, builder, prompt);
        } else {
            MessageContent messageContent = chain.get(PlainText.Key);

            if (messageContent == null) {
                builder.append("请发送正确的指令").build();
                messageEvent.getSubject().sendMessage(builder.build());
                return;
            }

            String prompt = messageContent.contentToString();
            processQuoteReplyRequest(chain, messageEvent, builder, quoteReply, user, event,prompt);
        }

    }
}
//处理引用回复的作图请求
//            MessageContent messageContent = chain.get(PlainText.Key);
//            String strMessage = messageContent.contentToString();
//            strMessage = strMessage.replace(" ", "");
//            if (strMessage.startsWith("U") || strMessage.startsWith("V")) {
//                MessageSource source = quoteReply.getSource();
//                String key =  source.getInternalIds()[0] + "&" + source.getTime();
//
//                Task task = saveImageMessageProcessor.get(key);
//                if (task == null) {
//                    MessageChain reply = new MessageChainBuilder().append("任务不存在").build();
//                    friendMessageEvent.getSubject().sendMessage(reply);
//                } else {
//                    mjService.UOrV((MessageEvent) event, task,strMessage);
//                }
//            }



