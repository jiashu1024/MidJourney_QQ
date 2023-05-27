package com.zjs.mj.util;

import com.aliyun.alimt20181012.Client;
import com.aliyun.alimt20181012.models.TranslateGeneralRequest;
import com.aliyun.alimt20181012.models.TranslateGeneralResponse;
import com.aliyun.alimt20181012.models.TranslateGeneralResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.zjs.mj.config.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 阿里的翻译
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AliTranslate {

    private final Properties properties;

    private static Client client;

    @PostConstruct
    public void init() {
       String accessKeyId = properties.getAliyun().getAccessKeyId();
       String accessKeySecret = properties.getAliyun().getAccessKeySecret();
        try {
            client = createClient(accessKeyId, accessKeySecret);
        } catch (Exception e) {
            log.error("初始化阿里翻译失败");
        }
    }

    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "mt.cn-hangzhou.aliyuncs.com";
        return new Client(config);
    }

    public static TranslateResult translate(String text) throws Exception {
        TranslateGeneralRequest translateGeneralRequest = new TranslateGeneralRequest()
                .setFormatType("text")
                .setSourceLanguage("zh")
                .setSourceText(text)
                .setScene("general")
                .setTargetLanguage("en");
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            TranslateGeneralResponse translateGeneralResponse = client.translateGeneralWithOptions(translateGeneralRequest, runtime);
            TranslateGeneralResponseBody body = translateGeneralResponse.getBody();
            return new TranslateResult(body.code,body.getData().getTranslated());
        } catch (TeaException error) {
            // 如有需要，请打印 error
            log.error(error.getMessage());
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            log.error(error.getMessage());
        }
        return new TranslateResult(500);
    }

    public static class TranslateResult{
        public int code;
        public String translate;

        public TranslateResult(int code, String translate){
            this.code = code;
            this.translate = translate;
        }

        public TranslateResult(int code){
            this.code = code;
        }



    }


}
