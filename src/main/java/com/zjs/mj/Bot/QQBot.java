package com.zjs.mj.Bot;

import com.zjs.mj.config.Properties;
import com.zjs.mj.processors.qqProcessor.FriendMessageEventProcessor;
import com.zjs.mj.processors.qqProcessor.GroupMessageEventProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Slf4j
@RequiredArgsConstructor
public class QQBot implements ApplicationListener<ContextRefreshedEvent> {

    private final Properties properties;

    private final FriendMessageEventProcessor friendMessageEventProcessor;

    private final GroupMessageEventProcessor groupMessageEventProcessor;
    private final DiscordBot discordBot;



    public static boolean ok = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        FixProtocolVersion.update();
        Properties.QqConfig qq = properties.getQq();
        String account = qq.getAccount();
        String password =qq.getPassword();
        String loginType = qq.getLoginType();
        String protocolStr = qq.getProtocol();

        BotAuthorization botAuthorization;
        if (loginType.equals("password")) {
            botAuthorization = BotAuthorization.byPassword(password);
        } else {
            botAuthorization = BotAuthorization.byQRCode();
        }
        String cacheDir = "cache/"+account + "/" + loginType + "/" + protocolStr + "/";
        File cache = new File(cacheDir);
        if (!cache.exists()) {
            System.out.println("创建cache文件夹成功:" + cache.mkdirs());
        }

        log.info("登录方式为: " + botAuthorization.toString());
        log.info("登录协议为: " + protocolStr);

        Bot bot = BotFactory.INSTANCE.newBot(new Long(account),botAuthorization, new BotConfiguration() {{
            setProtocol(BotConfiguration.MiraiProtocol.valueOf(protocolStr));
            String proto = getProtocol().name();
            this.disableContactCache();
            this.noNetworkLog();
            setCacheDir(cache);
            String deviceInfo = cacheDir + proto + "device.json";
            fileBasedDeviceInfo(deviceInfo);
        }});

        GlobalEventChannel.INSTANCE.subscribeAlways(Event.class, qqEvent -> {
            if (qqEvent instanceof GroupMessageEvent) {
                groupMessageEventProcessor.process(qqEvent);
            }
            if (qqEvent instanceof FriendMessageEvent) {
                friendMessageEventProcessor.process(qqEvent);
            }
        });
        try {
            bot.login();
            discordBot.login();
            ok = true;
        } catch (Exception e) {
            log.error("bot login error", e);
            deleteFolder(cache);
        }
    }

    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file); // 递归删除子文件或子文件夹
                }
            }
        }

        // 删除空文件夹或文件
        folder.delete();
    }
}
