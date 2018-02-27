package com.ibeiliao.deployment.admin.context;


import com.ibeiliao.deployment.cfg.EncryptionPropertyPlaceholderConfigurer;

/**
 * 定义应用系统
 * @author linyi 2016/7/21.
 */
public class AppConstants {
    /**
     * 默认系统
     */
    public static final int APP_ID_DEFAULT = 1;

    /**
     * 登录路径
     */
    public static final String SSO_LOGIN_URL = EncryptionPropertyPlaceholderConfigurer.getConfig("sso.login.url");
}
