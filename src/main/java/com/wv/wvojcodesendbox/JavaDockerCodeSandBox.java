package com.wv.wvojcodesendbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.wv.wvojcodesendbox.model.ExecuteCodeRequest;
import com.wv.wvojcodesendbox.model.ExecuteCodeResponse;
import com.wv.wvojcodesendbox.model.ExecuteMessage;
import com.wv.wvojcodesendbox.model.JudgeInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * Java Docker实现代码沙箱
 *
 * @author wv
 * @version V1.0
 * @date 2023/12/11 17:20
 */
@Slf4j
public class JavaDockerCodeSandBox implements CodeSandBox {

    /**
     * 全局代码存放根目录
     */
    private static final String GLOBAL_CODE_DIR_NAME = "tempCode";

    /**
     * 全局代码类名
     */
    private static final String GLOBAL_CODE_CLASS_NAME = "Main.java";

    /**
     * 容器镜像
     */
    private static final String IMAGE_NAME = "openjdk:8-alpine";

    /**
     * 首次拉取镜像
     */
    private static boolean FIRST_INIT = true;

    public static void main(String[] args) {
        JavaDockerCodeSandBox javaNactiveCodeSandBox = new JavaDockerCodeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 2", "3 4"));
        // 将代码文件读取到项目中
        String code = ResourceUtil.readStr("simpleCompute/Main.java", StandardCharsets.UTF_8);
//        String code = ResourceUtil.readStr("unsafe/MemoryError.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("Java");

        ExecuteCodeResponse executeCodeResponse = javaNactiveCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeRequest) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
//        return executeCodeResponse;
        List<String> inputList = executeRequest.getInputList();
        String language = executeRequest.getLanguage();
        String code = executeRequest.getCode();

        // 1.将用户提交的代码保存为文件
        String userDir = System.getProperty("user.dir");
        String globalCodePathName = userDir + File.separator + GLOBAL_CODE_DIR_NAME;
        // 判断全局代码目录是否存在，没有则新建
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把用户的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_CODE_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 2.编译代码，得到 class 文件
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        } catch (Exception e) {
            return getErrorResponse(e);
        }

        // 3.创建 Docker 容器，将 class 文件放置容器中，运行代码
        // 3.1 创建客户端
        DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://192.168.192.128:2375").build();
        // 3.2 拉取镜像
        if (FIRST_INIT) {
            PullImageCmd pullImageCmd = dockerClient.pullImageCmd(IMAGE_NAME);
            PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
                @Override
                public void onNext(PullResponseItem item) {
                    System.out.println("下载镜像：" + item.getStatus());
                    super.onNext(item);
                }
            };
            try {
                pullImageCmd
                        .exec(pullImageResultCallback)
                        .awaitCompletion();
            } catch (InterruptedException e) {
                System.out.println("拉取镜像异常");
                throw new RuntimeException(e);
            }
            System.out.println("下载完成");
            FIRST_INIT = false;
        }
        // 3.3创建容器（可交互）
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(IMAGE_NAME);
        // 3.4配置容器
        HostConfig hostConfig = new HostConfig();
        // 100M
        hostConfig.withMemory(100 * 1000 * 1000L);
        // 占用一个 CPU
        hostConfig.withCpuCount(1L);
        // 容器挂载目录（将本地代码同步到容器）
//        hostConfig.setBinds(new Bind(globalCodePathName, new Volume("/wv/code")));
        String bindPath = userCodeFile.getPath();
        hostConfig.setBinds(new Bind(bindPath, new Volume("/wv/code")));
        CreateContainerResponse containerResponse = containerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                // 交互终端
                .withTty(true)
                .exec();
//        CreateContainerResponse containerResponse = containerCmd.exec();
        String containerId = containerResponse.getId();

        // 4.启动容器
        dockerClient.startContainerCmd(containerId).exec();


//        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        // 6.错误处理，提高程序健壮性
        return executeCodeResponse;
    }

    /**
     * 获取错误响应
     *
     * @param e
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(Throwable e) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(e.getMessage());
        // 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }

}
