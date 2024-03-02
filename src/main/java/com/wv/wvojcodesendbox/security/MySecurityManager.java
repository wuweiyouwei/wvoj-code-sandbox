package com.wv.wvojcodesendbox.security;

/**
 * 安全管理器
 *
 * @author wv
 * @version V1.0
 * @date 2024/1/23 21:12
 */
public class MySecurityManager extends SecurityManager {

    /**
     * 限制执行命令
     *
     * @param cmd the specified system command.
     */
    @Override
    public void checkExec(String cmd) {
        super.checkExec(cmd);
    }

    /**
     * 限制读文件权限
     *
     * @param file the system-dependent file name.
     */
    @Override
    public void checkRead(String file) {
        throw new SecurityException("读文件权限异常" + file);
    }

    /**
     * 限制写文件权限
     *
     * @param file the system-dependent filename.
     */
    @Override
    public void checkWrite(String file) {
        super.checkWrite(file);
    }

    /**
     * 限制网络连接
     *
     * @param host the host name port to connect to.
     * @param port the protocol port to connect to.
     */
    @Override
    public void checkConnect(String host, int port) {
        super.checkConnect(host, port);
    }
}
