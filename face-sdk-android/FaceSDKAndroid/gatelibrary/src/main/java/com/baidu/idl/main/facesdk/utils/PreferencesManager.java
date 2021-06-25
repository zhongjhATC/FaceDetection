package com.baidu.idl.main.facesdk.utils;

import android.content.Context;

/**
 * Created by l on 2017/8/14.
 */

public class PreferencesManager extends BasePreferencesManager {


    private static final String RGB_DEPTH = "rgb_depth";
    private static final String RGB_NIR_DEPTH = "rgb_nir_depth";
    private static final String TYPE = "type";

    private static PreferencesManager instance = null;

    protected PreferencesManager(Context context) {
        super(context);
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void setType(int type) {
        setInt(TYPE, "type", type);
    }

    public int getType() {
        return getInt(TYPE, "type", 0);
    }

    public void setRgbDepth(int rgb_depth) {
        setInt(RGB_DEPTH, "rgb_depth", rgb_depth);
    }

    public int getRgbDepth() {
        return getInt(RGB_DEPTH, "rgb_depth", 0);
    }

    public void setRgbNirDepth(int rgb_nir_depth) {
        setInt(RGB_NIR_DEPTH, "rgb_nir_depth", rgb_nir_depth);
    }

    public int getRgbNirDepth() {
        return getInt(RGB_NIR_DEPTH, "rgb_nir_depth", 0);
    }

}
