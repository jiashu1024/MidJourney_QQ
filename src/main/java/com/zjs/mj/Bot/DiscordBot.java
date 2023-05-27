package com.zjs.mj.Bot;

import com.zjs.mj.config.Properties;
import com.zjs.mj.processors.discordProcessor.MjMessageListener;
import com.zjs.mj.util.TaskPool;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiscordBot  implements ApplicationListener<ApplicationStartedEvent> {

    private final Properties properties;

    private final  MjMessageListener mjMessageListener;

    private final TaskPool taskPool;

    @Override
    public void onApplicationEvent(@NotNull ApplicationStartedEvent event) {
        //登录discord机器人
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(this.properties.getMj().getBotToken(),
                GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT);
        builder.addEventListeners(this.mjMessageListener);
        builder.build();
        new Thread(taskPool::work).start();
    }
}
