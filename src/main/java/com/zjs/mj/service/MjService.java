package com.zjs.mj.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.zjs.mj.config.Properties;
import com.zjs.mj.entity.MjRequestResult;
import com.zjs.mj.enums.ImagineMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
@RequiredArgsConstructor
public class MjService {

    private static final String DISCORD_API_URL = "https://discord.com/api/v9/interactions";
    private final Properties properties;

    private String userAgent;
    private String relaxParamJson;
    private String fastParamJson;

    private String discordUploadUrl;

    private String imagineParamsJson;
    private String upscaleParamsJson;
    private String variationParamsJson;
    private String resetParamsJson;
    private String describeParamsJson;

    private String discordUserToken;
    private String discordGuildId;
    private String discordChannelId;

    @PostConstruct
    void init() {
        this.discordUserToken = this.properties.getMj().getUserToken();
        this.discordGuildId = this.properties.getMj().getGuildId();
        this.discordChannelId = this.properties.getMj().getChannelId();
        this.discordUploadUrl = "https://discord.com/api/v9/channels/" + this.discordChannelId + "/attachments";
        this.userAgent = this.properties.getMj().getUserAgent();
        this.imagineParamsJson = ResourceUtil.readUtf8Str("api-params/imagine.json");
        this.upscaleParamsJson = ResourceUtil.readUtf8Str("api-params/upscale.json");
        this.variationParamsJson = ResourceUtil.readUtf8Str("api-params/variation.json");
        this.resetParamsJson = ResourceUtil.readUtf8Str("api-params/reset.json");
        this.describeParamsJson = ResourceUtil.readUtf8Str("api-params/describe.json");
        this.relaxParamJson = ResourceUtil.readUtf8Str("api-params/relax.json");
        this.fastParamJson = ResourceUtil.readUtf8Str("api-params/fast.json");
    }

    public MjRequestResult<Void> relax() {
        String key = "$mode";
        String paramsStr = this.relaxParamJson.replace("$guild_id", this.discordGuildId)
                .replace("$channel_id", this.discordChannelId)
                .replace(key, "relax");
        return request(paramsStr);
    }

    public MjRequestResult<Void> fast() {
        String key = "$mode";
        String paramsStr = this.fastParamJson.replace("$guild_id", this.discordGuildId)
                .replace("$channel_id", this.discordChannelId)
                .replace(key, "fast");
        return request(paramsStr);
    }

    private MjRequestResult<Void> switchMode(ImagineMode mode) {
        MjRequestResult<Void> modeResult = null;
        if (mode.equals(ImagineMode.RELAX)) {
            modeResult = relax();
        } else if (mode.equals(ImagineMode.FAST)) {
            modeResult = fast();
        } else {
            log.error("imagine mode error");
            return MjRequestResult.failure("imagine mode error");
        }
        if (!modeResult.isSuccess()) {
            return modeResult;
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        return MjRequestResult.success("success");
    }

    public MjRequestResult<Void> imagine(String prompt, ImagineMode mode) {

        MjRequestResult<Void> modeResult = switchMode(mode);
        if (!modeResult.isSuccess()) {
            return modeResult;
        }

        String paramsStr = this.imagineParamsJson.replace("$guild_id", this.discordGuildId)
                .replace("$channel_id", this.discordChannelId);
        JSONObject params = new JSONObject(paramsStr);
        params.getJSONObject("data").getJSONArray("options").getJSONObject(0)
                .put("value", prompt);

        return request(params.toString());
    }

    public MjRequestResult<Void> upscale(String index,String messageHash,String requestId,boolean isDelete) {
        String paramsStr = this.upscaleParamsJson.replace("$guild_id", this.discordGuildId)
                .replace("$channel_id", this.discordChannelId)
                .replace("$index", String.valueOf(index))
                .replace("$message_id", requestId)
                .replace("$message_hash", messageHash);
        if (isDelete) {
            paramsStr = paramsStr.replace("$flag", "64");
        } else {
            paramsStr = paramsStr.replace("$flag", "0");
        }
        return request(paramsStr);
    }

    public MjRequestResult<Void> variation(String index,String messageHash,ImagineMode mode,String requestId,boolean isDelete) {
        MjRequestResult<Void> modeResult = switchMode(mode);
        if (!modeResult.isSuccess()) {
            return modeResult;
        }

        String paramsStr = this.variationParamsJson.replace("$guild_id", this.discordGuildId)
                .replace("$channel_id", this.discordChannelId)
                .replace("$message_id", requestId)
                .replace("$index", String.valueOf(index))
                .replace("$message_hash", messageHash);
        if (isDelete) {
            paramsStr = paramsStr.replace("$flag", "64");
        } else {
            paramsStr = paramsStr.replace("$flag", "0");
        }
        return request(paramsStr);
    }










//
//    public void UOrV(MessageEvent event, Task task, String command) {
//
//        Contact contact = event.getSubject();
//        int index = 0;
//        try {
//            index = Integer.parseInt(command.substring(1));
//        } catch (Exception e) {
//            MessageChain reply = new MessageChainBuilder().append(new QuoteReply(event.getMessage())).append("error number").build();
//            contact.sendMessage(reply);
//            return;
//        }
//
//        if (index <= 0 || index >= 5) {
//            MessageChain reply = new MessageChainBuilder().append(event.getMessage()).append("请输入正确的数字").build();
//            contact.sendMessage(reply);
//            return;
//        }
//        State state = new State();
//        if (event instanceof FriendMessageEvent) {
//            state.setType(EventType.FRIEND_REQUEST.name());
//            state.setQq(String.valueOf(contact.getId()));
//        } else if (event instanceof GroupMessageEvent) {
//            state.setType(EventType.GROUP_REQUEST.name());
//            GroupMessageEvent groupMessageEvent = (GroupMessageEvent) event;
//            state.setQq(String.valueOf(groupMessageEvent.getSender().getId()));
//            state.setGroup(String.valueOf(groupMessageEvent.getGroup().getId()));
//        }
//
//        state.setChain(MessageChain.serializeToJsonString(event.getMessage()));
//        String action = "";
//        if (command.startsWith("U")) {
//            action = "UPSCALE";
//        } else if (command.startsWith("V")) {
//            action = "VARIATION";
//        }
//        ProxyResult result = this.UOrV(action, state, index, task.getId());
//        if (result.getCode() == 1) {
//            MessageChain reply = new MessageChainBuilder().append(new QuoteReply(event.getMessage())).append("提交图片生成任务成功").build();
//            event.getSubject().sendMessage(reply);
//        }
//
//
//    }





    private ResponseEntity<String> postJson(String paramsStr) {
        return postJson(DISCORD_API_URL, paramsStr);
    }

    private ResponseEntity<String> postJson(String url, String paramsStr) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", this.discordUserToken);
        headers.add("User-Agent", this.userAgent);
        HttpEntity<String> httpEntity = new HttpEntity<>(paramsStr, headers);
        return new RestTemplate().postForEntity(url, httpEntity, String.class);
    }

    private MjRequestResult<Void> request(String paramsStr) {
        try {
            ResponseEntity<String> responseEntity = postJson(paramsStr);
            if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
                return MjRequestResult.success(responseEntity.getStatusCodeValue());
            }
            return MjRequestResult.failure(responseEntity.getStatusCodeValue(), CharSequenceUtil.sub(responseEntity.getBody(), 0, 100));
        } catch (HttpClientErrorException e) {
            try {
                JSONObject error = new JSONObject(e.getResponseBodyAsString());
                return MjRequestResult.failure(error.optInt("code", e.getRawStatusCode()), error.optString("message"));
            } catch (Exception je) {
                return MjRequestResult.failure(e.getRawStatusCode(), CharSequenceUtil.sub(e.getMessage(), 0, 100));
            }
        }
    }


}
