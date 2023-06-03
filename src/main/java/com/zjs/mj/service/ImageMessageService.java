package com.zjs.mj.service;

import com.zjs.mj.entity.Task;
import com.zjs.mj.entity.dto.ImageMessage;
import com.zjs.mj.mapper.ImageMessageMapper;
import lombok.RequiredArgsConstructor;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImageMessageService {

    private  final ImageMessageMapper mapper;

    private static ImageMessageMapper imageMessageMapper;

    @PostConstruct
    private void init() {
        imageMessageMapper = mapper;
    }


    public static void saveImageMessage(Task task) {
        ImageMessage imageMessage = new ImageMessage();
        imageMessage.setImageUrl(task.getImageUrl());
        MessageEvent  event = (MessageEvent) task.getEvent();

        imageMessage.setQq(String.valueOf(event.getBot().getId()));
        if (event instanceof GroupMessageEvent) {
            imageMessage.setGroupId(String.valueOf(((GroupMessageEvent) event).getGroup().getId()));
        }
        imageMessage.setTime(LocalDateTime.now());
        imageMessageMapper.insert(imageMessage);
    }
}
