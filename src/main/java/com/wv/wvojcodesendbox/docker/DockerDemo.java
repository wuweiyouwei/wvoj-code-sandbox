package com.wv.wvojcodesendbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.LogContainerResultCallback;

/**
 * @author wv
 * @version V1.0
 * @date 2023/12/14 16:54
 */
public class DockerDemo {

    public static void main(String[] args) throws InterruptedException {
        DockerClient dockerClient = DockerClientBuilder.getInstance("tcp://8.140.253.156:2375").build();

        // 1.拉取镜像
        String image = "nginx:latest";
//        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
//        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
//            @Override
//            public void onNext(PullResponseItem item) {
//                System.out.println("下载镜像：" + item.getStatus());
//                super.onNext(item);
//            }
//        };
//        pullImageCmd
//                .exec(pullImageResultCallback)
//                .awaitCompletion();
//        System.out.println("下载完成");

        // 2.创建容器
        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
        CreateContainerResponse createContainerResponse = containerCmd
                .withCmd("echo", "hello docker")
                .exec();
        System.out.println(createContainerResponse);
        String containerId = createContainerResponse.getId();

        // 3.查看容器状态
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        for (Container container : listContainersCmd.withShowAll(true).exec()) {
            System.out.println(container);
        }

        // 4.启动容器
        dockerClient.startContainerCmd(containerId).exec();

        // 5.查看日志
        LogContainerResultCallback logContainerResultCallback = new LogContainerResultCallback() {
            @Override
            public void onNext(Frame item) {
                System.out.println(item.getStreamType());
                System.out.println("日志：" + new String(item.getPayload()));
                super.onNext(item);
            }
        };
        dockerClient
                .logContainerCmd(containerId)
                .withStdOut(true)
                .withStdErr(true)
                .exec(logContainerResultCallback)
                // 阻塞等待日志输出
                .awaitCompletion();

        // 删除容器
        dockerClient.removeContainerCmd(containerId).withForce(true).exec();
        // 删除镜像
        dockerClient.removeImageCmd(image).withForce(true).exec();
    }
}
