package com.zjs.mj.service;

import cn.hutool.http.HttpUtil;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.zjs.mj.config.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WxBotService {

    private final Properties properties;

    private static String url;

    @PostConstruct
    private void init() {
        url = properties.getWx().getWebHook();
    }

    public static void sendText(String message) {
        Map<String,Object> params = new HashMap<>();
        params.put("msgtype","text");
        Map<String,Object> text = new HashMap<>();
        text.put("content",message);
        params.put("text",text);
        String result = HttpUtil.post(url, JSONUtil.toJsonPrettyStr(params));
        log.info("推送微信图片: {}", result);
    }

    public static void sendImage(byte[] imageBytes) {
        Map<String, Object> params = new HashMap<>();
        params.put("msgtype", "image");
        Map<String, Object> image = new HashMap<>();

        image.put("base64", Base64.getEncoder().encodeToString(imageBytes));
        image.put("md5", calculateMd5(imageBytes));
        params.put("image", image);
        String result = HttpUtil.post(url, JSONUtil.toJsonPrettyStr(params));
        log.info("推送微信消息: {}", result);
    }

    private static String calculateMd5(byte[] imageBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md.digest(imageBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : md5Bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


}
