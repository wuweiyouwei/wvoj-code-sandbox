package com.wv.wvojcodesendbox;

import com.wv.wvojcodesendbox.model.ExecuteCodeRequest;
import com.wv.wvojcodesendbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * @author wv
 * @version V1.0
 * @date 2024/1/26 16:16
 */
@Component
public class JavaNativeCodeSandBox extends JavaCodeSandBoxTemplate{

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
