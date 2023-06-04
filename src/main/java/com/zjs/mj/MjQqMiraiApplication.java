package com.zjs.mj;

import com.zjs.mj.config.Properties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class MjQqMiraiApplication {

    @Autowired
    Properties properties;

    public static void main(String[] args) {
        SpringApplication.run(MjQqMiraiApplication.class, args);
    }




    @PostConstruct
    private  void setProxy() {
        if (!properties.getProxy().isEnable()) {
            return;
        }
        System.setProperty("http.proxyHost", properties.getProxy().getHttp().getHost());
        System.setProperty("http.proxyPort", properties.getProxy().getHttp().getPort());
        System.setProperty("https.proxyHost", properties.getProxy().getHttps().getHost());
        System.setProperty("https.proxyPort", properties.getProxy().getHttps().getPort());
        System.setProperty("socksProxyHost", properties.getProxy().getSocks().getHost());
        System.setProperty("socksProxyPort", properties.getProxy().getSocks().getPort());
    }

}
