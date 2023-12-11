package com.wv.wvojcodesendbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码沙箱通用请求类
 * @author wv
 * @version V1.0
 * @date 2023/12/6 16:19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeRequest {


    /**
     * 输入用例
     */
    private List<String> inputList;

    /**
     * 编程语言
     */
    private String language;

    /**
     * 代码
     */
    private String code;
}
