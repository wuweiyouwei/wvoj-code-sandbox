package com.wv.wvojcodesendbox.security;

import java.security.Permission;

/**
 * 默认安全管理器
 *
 * @author wv
 * @version V1.0
 * @date 2024/1/23 20:49
 */
public class DefaultSecurityManager extends SecurityManager {

    /**
     * 检查所有的权限
     *
     * @param perm the requested permission.
     */
    @Override
    public void checkPermission(Permission perm) {
//        System.out.println("默认不做限制");
//        System.out.println(perm);
//        super.checkPermission(perm);
        throw new SecurityException("权限异常" + perm.toString());
    }
}
