package com.zjs.mj.service;

import com.zjs.mj.entity.Task;
import com.zjs.mj.entity.dto.ImageMessage;
import com.zjs.mj.mapper.ImageMessageMapper;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImageMessageService {

    private final ImageMessageMapper mapper;

    private static ImageMessageMapper imageMessageMapper;

    @PostConstruct
    private void init() {
        imageMessageMapper = mapper;
    }


//    public static void saveImageMessage(Task task) {
//        ImageMessage imageMessage = new ImageMessage();
//        imageMessage.setImageUrl(task.getImageUrl());
//        MessageEvent event = (MessageEvent) task.getEvent();
//
//        imageMessage.setQq(String.valueOf(event.getBot().getId()));
//        if (event instanceof GroupMessageEvent) {
//            imageMessage.setGroupId(String.valueOf(((GroupMessageEvent) event).getGroup().getId()));
//        }
//        imageMessage.setTime(LocalDateTime.now());
//        imageMessageMapper.insert(imageMessage);
//    }

    public static void processStorageImageMessage(MessageEvent messageEvent) {
        MessageChain chain = messageEvent.getMessage();
        Image image = chain.get(Image.Key);
        if (image == null) {
            return;
        }

        String qq = null;
        String groupId = null;
        String url = Image.queryUrl(image);

        if (messageEvent instanceof FriendMessageEvent) {
            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) messageEvent;
            qq = String.valueOf(friendMessageEvent.getSender().getId());
        }
        if (messageEvent instanceof GroupMessageEvent) {
            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) messageEvent;
            qq = String.valueOf(groupMessageEvent.getSender().getId());
            groupId = String.valueOf(groupMessageEvent.getGroup().getId());
        }



        processStorageImageMessage(url, messageEvent.getSource(), qq, groupId);
//        ImageMessage message = new ImageMessage();
//        message.setMessageId(Task.createSourceKey(messageEvent.getSource()));
//        message.setImageUrl(Image.queryUrl(image));
//        if (messageEvent instanceof FriendMessageEvent) {
//            FriendMessageEvent friendMessageEvent = (FriendMessageEvent) messageEvent;
//            message.setQq(String.valueOf(friendMessageEvent.getSender().getId()));
//        }
//        if (messageEvent instanceof GroupMessageEvent) {
//            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) messageEvent;
//            message.setQq(String.valueOf(groupMessageEvent.getSender().getId()));
//            message.setGroupId(String.valueOf(groupMessageEvent.getGroup().getId()));
//        }
//        message.setTime(LocalDateTime.now());
//        imageMessageMapper.insert(message);
    }

    public static void processStorageImageMessage(String url, MessageSource source, String qq, String groupId) {
        ImageMessage message = new ImageMessage();
        message.setMessageId(Task.createSourceKey(source));
        message.setImageUrl(url);
        message.setQq(qq);
        message.setGroupId(groupId);
        message.setTime(LocalDateTime.now());
        imageMessageMapper.insert(message);
    }


}
