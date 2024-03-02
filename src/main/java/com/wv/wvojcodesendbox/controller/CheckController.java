package com.wv.wvojcodesendbox.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wv.wvojcodesendbox.JavaCodeSandBoxTemplate;
import com.wv.wvojcodesendbox.JavaNativeCodeSandBox;
import com.wv.wvojcodesendbox.model.ExecuteCodeRequest;
import com.wv.wvojcodesendbox.model.ExecuteCodeResponse;
import com.wv.wvojcodesendbox.model.domain.User;
import com.wv.wvojcodesendbox.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wv
 * @version V1.0
 * @date 2023/12/11 16:13
 */
@RestController("/")
public class CheckController {

    private static final String AUTH_REQUEST_HEADER = "auth";
    private static final String AUTH_REQUEST_SECRET = "secretKey";

    @GetMapping("/check")
    public String check() {
        return "ok";
    }

    @Resource
    private JavaNativeCodeSandBox javaNativeCodeSandBox;

    @Resource
    private UserService userService;

    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest executeCodeRequest, HttpServletRequest request,
                                           HttpServletResponse response) {
        String accessKey = request.getHeader("accessKey");
        String timestamp = request.getHeader("timestamp");
        String nonce = request.getHeader("nonce");
        String sign = request.getHeader("sign");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        User user = userService.getOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("权限异常");
        }
        if (Long.parseLong(nonce) > 9999) {
            // 检验随机数
            throw new RuntimeException("权限异常");
        }
        if ((System.currentTimeMillis() / 1000) - Long.parseLong(timestamp) > 5) {
            // 检验时间戳
            throw new RuntimeException("权限异常");
        }
        // 签名校验
        String secretKey = user.getSecretKey();
        String generatedSign = DigestUtil.md5Hex(executeCodeRequest.toString() + "." + secretKey);
        if (StrUtil.isNotEmpty(generatedSign) && !generatedSign.equals(sign)) {
            // 签名校验失败
            throw new RuntimeException("权限异常");
        }

        if (executeCodeRequest == null) {
            throw new RuntimeException("请求参数不能为空");
        }
        return javaNativeCodeSandBox.executeCode(executeCodeRequest);
    }
}
