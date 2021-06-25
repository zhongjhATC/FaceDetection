package com.baidu.idl.main.facesdk.registerlibrary.user.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * SharedPreferences
 * Created by liujialu on 2020/02/11.
 */

public class ShareManager {
    // 定义存储用户名字段
    private static final String SP_DB_STATE = "db_state";

    private static ShareManager instance;
    private SharedPreferences sp;
    private Context mContext;

    private ShareManager(Context context) {
        this.mContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static synchronized ShareManager getInstance(Context context) {
        if (instance == null) {
            instance = new ShareManager(context);
        }
        return instance;
    }

    // 存储数据库状态
    public void setDBState(boolean state) {
        sp.edit().putBoolean(SP_DB_STATE, state).apply();
    }
    // 获取数据库状态
    public boolean getDBState() {
        return sp.getBoolean(SP_DB_STATE, false);
    }
}
