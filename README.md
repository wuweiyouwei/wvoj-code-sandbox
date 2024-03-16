## 代码沙箱实现

**何为代码沙箱？**

**只负责接收代码和输入，返回编译运行的结果，不负责判题（可以做作为独立的服务 / 项目，提供给其他需要执行代码的项目使用）**



### Java 原生实现代码沙箱

核心思路：Java 源代码 => 编译（javac）=> 运行（java）

核心依赖：Java 进程类 Process

业务逻辑：

1. 把用户的代码保存为文件
2. 编译代码，得到 class 文件
3. 执行代码，得到输出结果
4. 收集整理输出结果
5. 文件清理，释放空间
6. 错误处理，提升程序健壮性



#### 安全性分析

用户提交恶意代码，危害系统

1. 执行阻塞（耗用时间）
2. 内存占用（耗用内存）
3. 读取文件（信息泄露）
4. 修改文件（写入木马）
5. 执行其他操作（高危操作）



#### 如何保证沙箱安全

1）超时控制：通过创建一个守护线程，超时后自动中断 Process 实现

```java
 new Thread(() -> {
     try {
         Thread.sleep(TIME_OUT);
         System.out.println("超时了，中断");
         runProcess.destroy();
     } catch (InterruptedException e) {
         throw new RuntimeException(e);
     }
 }).start();
```

2）限制给用户分配的资源

我们不能让每个 java 进程的执行占用的 JVM 最大堆内存空间都和系统默认的一致（我的 JVM 默认最大占用 8G 内存），实际上应该更小（执行用户的题目代码也不需要这么多），比如说 256MB。
在启动 Java 程序时，可以指定 JVM 的参数：-Xmx256m（最大堆空间大小）



3）代码限制（黑白名单）

先定义一个黑名单
可以使用 Hutool 的工具类 WordTree 来检验字符串中是否包含黑名单字段

```java
    /**
     * 黑名单字段方式，防止用户恶意操作
     */
    private static final List<String> BLACK_LIST = Arrays.asList("exec", "Files");


    public static final WordTree WORD_TREE;

    static {
        WORD_TREE = new WordTree();
        WORD_TREE.addWords(BLACK_LIST);
    }

	FoundWord foundWord = WORD_TREE.matchWord(code);
        if (foundWord != null) {
            System.out.println("发现违禁词：" + foundWord.getWord());
            return null;
        }

```



4）限制用户的操作权限（文件、网络、指令）

限制用户对文件、内存、CPU、网络等资源的访问和操作。

通过自定义 Java 安全管理器（实现 Security Manager）重写 checkWrite()、checkExec() 限制 Java 代码的执行权限。



5）运行环境隔离

Docker：系统层面上，把用户程序封装到沙箱里，和宿主机（我们的电脑/服务器）隔离开。





### Docker 代码沙箱

为什么要用 Docker 技术？

**为了提升系统的安全性，把不同程序和宿主机进行隔离，使得某个程序和应用的执行不会影响到系统本身。**

什么是容器？

**理解为对一系列应用程序、服务、和环境的封装，从而把程序运行在一个隔离的、密闭的、隐私的空间内，对外整体提供服务。（可以把容器理解为一个全新的电脑（操作系统））**



业务逻辑：

Docker 负责运行 Java(class 文件)，给出运行结果。

1. 把用户的代码保存为文件
2. 编译代码，得到 class 文件
3. 将编译好的文件（class 文件）上传至 Docker 容器中
4. 在 Docker 容器中执行代码，得到输出结果
5. 收集整理输出结果
6. 文件清理，释放空间
7. 错误处理，提升程序健壮性

