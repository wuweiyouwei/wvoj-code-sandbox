package com.wv.wvojcodesendbox;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.wv.wvojcodesendbox.model.ExecuteCodeRequest;
import com.wv.wvojcodesendbox.model.ExecuteCodeResponse;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.UUID;

/**
 * Java 原生实现代码沙箱
 * @author wv
 * @version V1.0
 * @date 2023/12/11 17:20
 */
public class JavaNactiveCodeSandBox implements CodeSandBox{

    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";
    private static final String GLOBAL_CODE_CLASS_NAME = "Main.class";

    public static void main(String[] args) {
        JavaNactiveCodeSandBox javaNactiveCodeSandBox = new JavaNactiveCodeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        // 将代码文件读取到项目中
        String code = ResourceUtil.readStr("simpleCompute/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("Java");

        ExecuteCodeResponse executeCodeResponse = javaNactiveCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeRequest) {
        List<String> inputList = executeRequest.getInputList();
        String language = executeRequest.getLanguage();
        String code = executeRequest.getCode();

        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }

        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        FileUtil.writeString(code,
                userCodeParentPath + File.separator + GLOBAL_CODE_CLASS_NAME,
                StandardCharsets.UTF_8);

        return null;
    }
}
