package com.wv.wvojcodesendbox.security;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;

/**
 * @author wv
 * @version V1.0
 * @date 2024/1/23 21:14
 */
public class TestSecurityManager {

    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());

        FileUtil.readLines("E:\\planet-programme\\wvoj-code-sendbox\\src\\main\\resources\\application.yml", StandardCharsets.UTF_8);
    }
}
