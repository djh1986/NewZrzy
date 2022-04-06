package com.zrzyyzt.runtimeviewer.Config;

import android.content.Context;

import com.zrzyyzt.runtimeviewer.Config.Entity.ConfigEntity;
import com.zrzyyzt.runtimeviewer.Config.Xml.XmlParser;

/**
 * 系统配置获取
 */
public class AppConfig {

    /**
     * 获取应用程序配置信息
     */
    public static ConfigEntity getConfig(Context context) {
        ConfigEntity config = null;
        try {
            config = XmlParser.getConfig(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return config;
    }
}
