package com.zjs.mj.processors.discordProcessor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import com.zjs.mj.entity.Task;
import com.zjs.mj.enums.Action;
import com.zjs.mj.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.OnlineMessageSource;

import java.time.LocalDateTime;

/**
 * 处理discord消息
 */
public interface MessageProcessor {


    /**
     * 新消息事件
     * 例如：作图结果消息，和waiting to start消息
     * @param message
     */
    void onMessageReceived(Message message);

    /**
     * 消息更新事件
     * 例如：作图进度消息改变则触发该事件
     * @param message
     */
    void onMessageUpdate(Message message);


}
