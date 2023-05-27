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

    public static boolean ok = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        FixProtocolVersion.update();
        String account = properties.getQq().getAccount();
        String password = properties.getQq().getPassword();


        Bot bot = BotFactory.INSTANCE.newBot(new Long(account), BotAuthorization.byQRCode(), new BotConfiguration() {{
            setProtocol(MiraiProtocol.ANDROID_WATCH);
            String proto = getProtocol().name();
            this.disableContactCache();
            this.noNetworkLog();
            setCacheDir(new File("cache"));
            String deviceInfo = "cache/" + account + "-"+ proto + "device.json";
            fileBasedDeviceInfo(deviceInfo);
        }});
//        Bot bot = BotFactory.INSTANCE.newBot(new Long(account), password, new BotConfiguration() {{
//            setProtocol(MiraiProtocol.MACOS);
//            String proto = getProtocol().name();
//            this.disableContactCache();
//            this.noNetworkLog();
//            setCacheDir(new File("cache"));
//            String deviceInfo = "cache/" + account + "-"+ proto + "device.json";
//            fileBasedDeviceInfo(deviceInfo);
//        }});

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
            ok = true;
        } catch (Exception e) {
            log.error("bot login error", e);
            //删除当前目录下的cache文件夹
            File file = new File("cache");
            if (file.exists()) {
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        System.out.println("删除文件:" + f.getName());
                        f.delete();
                    }
                }
            }
        }


    }
}
