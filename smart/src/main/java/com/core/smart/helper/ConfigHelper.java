package com.core.smart.helper;

import com.core.smart.tools.ConfigConstant;
import com.core.smart.tools.PropsUtil;

import java.util.Properties;

/**
 * 属性文件帮助类
 * Created by Administrator on 2017/11/7.
 */
public final class ConfigHelper {
    private static final Properties CONFIG_PROPS = PropsUtil.loadProps(ConfigConstant.CONFIG_FILE);

    /**
     * jdbc driver
     */
    public static String getJdbcDriver(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_DRIVER);
    }

    /**
     * jdbc url
     */
    public static String getJdbcUrl(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_URL);
    }

    /**
     * jdbc username
     */
    public static String getJdbcUsername(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_USERNAME);
    }

    /**
     * jdbc pwd
     */
    public static String getJdbcPassword(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_PASSWORD);
    }

    /**
     * 基础应用包名
     */
    public static String getAppBasePackage(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_BASE_PACKAGE);
    }

    /**
     * jsp 路径
     */
    public static String getAppJspPath(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_JSP_PATH,"/WEB-INF/pages/");
    }

    /**
     * 静态资源路径
     */
    public static String getAppAssetPath(){
        return PropsUtil.getString(CONFIG_PROPS,ConfigConstant.APP_ASSET_PATH,"/asset/");
    }

}
