package com.wv.wvojcodesendbox;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.wv.wvojcodesendbox.mapper")
public class WvojCodeSendboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(WvojCodeSendboxApplication.class, args);
    }

}
