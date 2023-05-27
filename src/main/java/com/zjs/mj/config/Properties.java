package com.zjs.mj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config")
public class Properties {
    private final MjConfig mj = new MjConfig();
    private final AliyunConfig aliyun = new AliyunConfig();
    private final QqConfig qq = new QqConfig();

    @Data
    public static class MjConfig {

        private int plan;
        /**
         * 你的服务器id.
         */
        private String guildId;
        /**
         * 你的频道id.
         */
        private String channelId;
        /**
         * 你的登录token.
         */
        private String userToken;
        /**
         * 你的机器人token.
         */
        private String botToken;

        /**
         * mj用户id
         */
        private String userId;

        /**
         * mj的token
         */
        private String token;

        /**
         * Mid journey机器人的名称.
         */
        private String mjBotName = "Midjourney Bot";
        /**
         * 调用discord接口时的user-agent.
         */
        private String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36";
    }

    @Data
    public static class AliyunConfig {
        /**
         * 阿里云翻译的APP_ID.
         */
        private String accessKeyId;
        /**
         * 阿里云翻译的密钥.
         */
        private String accessKeySecret;
    }

    @Data
    public static class QqConfig{
        private String account;
        private String password;
    }


}
