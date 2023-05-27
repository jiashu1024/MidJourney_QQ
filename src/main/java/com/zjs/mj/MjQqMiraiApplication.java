package com.zjs.mj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MjQqMiraiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MjQqMiraiApplication.class, args);
    }

}
