package com.baidu.idl.main.facesdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class BasePreferencesManager {

    /**
     * 上下问对象
     */
    private Context mContext;

    protected BasePreferencesManager(Context context) {
        this.mContext = context;
    }

    protected void clear(String preferences) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    protected void setBoolean(String preferences, String key, boolean value) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    protected boolean getBoolean(String preferences, String key, boolean defaultvalue) {
        SharedPreferences sp = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultvalue);
    }

    protected int getInt(String preferences, String key, int defaultvalue) {
        SharedPreferences sp = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultvalue);
    }

    protected void setInt(String preferences, String key, int value) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    protected String getString(String preferences, String key, String defaultvalue) {
        SharedPreferences sp = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        return sp.getString(key, defaultvalue);
    }

    protected void setString(String preferences, String key, String value) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    protected float getFloat(String preferences, String key, float defaultvalue) {
        SharedPreferences sp = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultvalue);
    }

    protected void setFloat(String preferences, String key, float value) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public long getLong(String preferences, String key, long defaultvalue) {
        SharedPreferences sp = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE);
        return sp.getLong(key, defaultvalue);
    }

    protected void setLong(String preferences, String key, long value) {
        Editor editor = mContext.getSharedPreferences(preferences, Context.MODE_PRIVATE).edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
