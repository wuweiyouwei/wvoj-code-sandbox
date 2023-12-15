package com.wv.wvojcodesendbox;

import com.wv.wvojcodesendbox.model.ExecuteMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 进程工具类
 * @author wv
 * @version V1.0
 * @date 2023/12/12 14:48
 */
@Slf4j
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     * @param runProcess 进程
     * @param opName 命令
     * @return 执行信息
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();


        try {
            // 等待编译进程执行，获取错误码
            int exitCode = runProcess.waitFor();
            executeMessage.setExitValue(exitCode);
            if (exitCode == 0) {
                // 正常退出
                log.info(opName + "成功");
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringBuilder.append(compileOutputLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());
            } else {
                // 异常退出
                log.error(opName + "失败，错误码： " + exitCode);
                // 分批获取进程的正常输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder compileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    compileOutputStringBuilder.append(compileOutputLine);
                }
                executeMessage.setMessage(compileOutputStringBuilder.toString());

                // 分批获取进程的错误输出
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream(), StandardCharsets.UTF_8));
                StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
                // 逐行读取
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorCompileOutputStringBuilder.append(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(errorCompileOutputStringBuilder.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // 资源释放
        }
        return executeMessage;
    }
}
