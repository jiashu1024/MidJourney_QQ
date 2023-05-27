package com.zjs.mj.processors.discordProcessor;

import com.zjs.mj.config.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MjMessageListener extends ListenerAdapter {


    private final Properties properties;

    private final MjMessageProcessor mjMessageProcessor;

    /**
     * 忽略无关消息
     * @param message
     * @return
     */
   public boolean ignoreCommonMessage(Message message) {
       String channelId = message.getChannel().getId();
       if (!this.properties.getMj().getChannelId().equals(channelId)) {
           return true;
       }
       String authorName = message.getAuthor().getName();
       log.debug("{}: {}", authorName, message.getContentRaw());
       return !this.properties.getMj().getMjBotName().equals(authorName);
   }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        Message message = event.getMessage();
        if (ignoreCommonMessage(message)) {
            return;
        }
        mjMessageProcessor.onMessageUpdate(message);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        if (ignoreCommonMessage(message)) {
            return;
        }
        mjMessageProcessor.onMessageReceived(message);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
//        System.out.println(event);
//        System.out.println("删除id：" + event.getMessageId());
    }
}
