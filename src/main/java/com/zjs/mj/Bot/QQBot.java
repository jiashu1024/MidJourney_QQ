package com.zjs.mj.Bot;

import com.zjs.mj.config.Properties;
import com.zjs.mj.processors.qqProcessor.FriendMessageEventProcessor;
import com.zjs.mj.processors.qqProcessor.GroupMessageEventProcessor;
import com.zjs.mj.service.WxBotService;
import com.zjs.mj.util.TaskPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.auth.BotAuthorization;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.BotOnlineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import xyz.cssxsh.mirai.tool.FixProtocolVersion;

import java.io.File;

@Component
@Slf4j
@RequiredArgsConstructor
public class QQBot implements ApplicationListener<ContextRefreshedEvent> {

    private final Properties properties;

    private final FriendMessageEventProcessor friendMessageEventProcessor;

    private final GroupMessageEventProcessor groupMessageEventProcessor;
    private final DiscordBot discordBot;

    private final QqLoginSolver qqLoginSolver;

    private final ApplicationContext context;

    private final TaskPool taskPool;

    public static boolean ok = false;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        FixProtocolVersion.update();
        Properties.QqConfig qq = properties.getQq();
        String account = qq.getAccount();
        String password = qq.getPassword();
        String loginType = qq.getLoginType();
        String protocolStr = qq.getProtocol();

        BotAuthorization botAuthorization;
        if (loginType.equals("password")) {
            botAuthorization = BotAuthorization.byPassword(password);
        } else {
            botAuthorization = BotAuthorization.byQRCode();
        }
        String cacheDir = "cache/" + account + "/" + loginType + "/" + protocolStr + "/";
        File cache = new File(cacheDir);
        if (!cache.exists()) {
           log.info("创建登录缓存文件夹cache文件夹成功:" + cache.mkdirs());
        }
        boolean isQrLoginType = loginType.equals("qrcode");

        log.info("开始登录qq: " + account);
        log.info("登录方式为: " + botAuthorization);
        log.info("登录协议为: " + protocolStr);

        Bot bot = BotFactory.INSTANCE.newBot(new Long(account), botAuthorization, new BotConfiguration() {{
            setProtocol(BotConfiguration.MiraiProtocol.valueOf(protocolStr));
            String proto = getProtocol().name();
            this.disableContactCache();
            this.noNetworkLog();
            setCacheDir(cache);
            String deviceInfo = cacheDir + proto + "device.json";
            if (isQrLoginType) {
                setLoginSolver(qqLoginSolver);
            }
            fileBasedDeviceInfo(deviceInfo);
        }});

        GlobalEventChannel.INSTANCE.subscribeAlways(Event.class, qqEvent -> {
            if (qqEvent instanceof GroupMessageEvent) {
                groupMessageEventProcessor.process(qqEvent);
            }
            if (qqEvent instanceof FriendMessageEvent) {
                friendMessageEventProcessor.process(qqEvent);
            }

            if (qqEvent instanceof BotOfflineEvent) {
                WxBotService.sendText("bot["+ bot.getId() + "]掉线");
            }

            if (qqEvent instanceof BotOnlineEvent) {
                WxBotService.sendText("bot["+ bot.getId() + "]上线");
            }
        });
        try {
            bot.login();
            ok = true;
            //WxBotService.sendText("bot["+ bot.getId() + "]登录成功");
        } catch (Exception e) {
            log.error("bot login error", e);
            WxBotService.sendText("bot登录失败\n" + e.getCause().getMessage());
            deleteFolder(cache);
            return;
        }

        try {
            discordBot.login();
            new Thread(taskPool::work).start();
        } catch (Exception e) {
            log.error("discord bot login error", e);
            WxBotService.sendText("discord bot登录失败\n" + e.getCause().getMessage());
            SpringApplication.exit(context, () -> 0);
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
