package com.baidu.idl.face.main.drivermonitor.model;

/**
 * author : shangrong
 * date : 2019/5/23 11:23 AM
 * description :配置BaseConfig单例
 */
public class SingleBaseConfig {
    private static BaseConfig BaseConfig;

    private SingleBaseConfig() {

    }

    public static BaseConfig getBaseConfig() {
        if (BaseConfig == null) {
            BaseConfig = new BaseConfig();
        }
        return BaseConfig;
    }

    public static void copyInstance(BaseConfig result) {
        BaseConfig = result;
    }
}
