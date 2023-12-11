package com.wv.wvojcodesendbox.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wv
 * @version V1.0
 * @date 2023/12/11 16:13
 */
@RestController("/")
public class CheckController {

    @GetMapping("/check")
    public String check(){
        return "ok";
    }
}
