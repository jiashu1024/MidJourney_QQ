package com.zjs.mj.processors.qqProcessor;

import com.zjs.mj.entity.dto.User;
import com.zjs.mj.mapper.ImageMessageMapper;
import com.zjs.mj.mapper.TaskMapper;
import com.zjs.mj.mapper.UserMapper;
import com.zjs.mj.util.TaskPool;
import com.zjs.mj.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GroupMessageEventProcessor extends ProcessorAdaptor {


    public GroupMessageEventProcessor(UserUtil userUtil, UserMapper userMapper, TaskMapper taskMapper, TaskPool taskPool, ImageMessageMapper imageMessageMapper) {
        super(userUtil, userMapper, taskMapper, taskPool, imageMessageMapper);
    }

    @Override
    public void process(Event event) {

        if (expire(event)) {
            return;
        }
        GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
        MessageChain chain = groupMessageEvent.getMessage();

        processStorageImageMessage(groupMessageEvent);

        User user = userUtil.getUser(String.valueOf(groupMessageEvent.getSender().getId()));
        MessageChainBuilder replyChain = new MessageChainBuilder().append(new QuoteReply(chain));
        QuoteReply quoteReply = chain.get(QuoteReply.Key);
        if (quoteReply == null) {
            MessageContent messageContent = chain.get(At.Key);
            if (messageContent == null) {
                log.debug("group {} at message content is null", groupMessageEvent.getGroup().getId());
                return;
            }
            if (messageContent instanceof At) {
                At at = (At) messageContent;
                if (at.getTarget() == ((GroupMessageEvent) event).getBot().getId()) {
                    PlainText contentText = (PlainText) chain.stream().filter(PlainText.class::isInstance).findFirst().orElse(null);
                    if (contentText == null) {
                        log.debug("group {} at message contentText is null", groupMessageEvent.getGroup().getId());
                        return;
                    }
                    String prompt = contentText.contentToString();
                    processImagineRequest(groupMessageEvent, chain, user, event,replyChain,prompt);
                }
            }

        } else {

            MessageContent messageContent = chain.get(PlainText.Key);
            if (messageContent == null) {
                log.debug("group {} message content is null", groupMessageEvent.getGroup().getId());
                return;
            }
            PlainText contentText = (PlainText) chain.stream().filter(PlainText.class::isInstance).findFirst().orElse(null);
            String prompt = null;
            if (contentText != null) {
                prompt = contentText.contentToString();
                processQuoteReplyRequest(chain,groupMessageEvent,replyChain,quoteReply, user, event,prompt);
            }
        }
    }
}
