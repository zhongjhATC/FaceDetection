package com.zhongjh.mvvmrapid.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by zhongjh on 2021/3/25.
 * 用于提供给其他静态类调用context
 */
public class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private Utils() {
        throw new UnsupportedOperationException("不能直接实例化该类，仅仅提供给其他静态类调用context");
    }

    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    public static void init(@NonNull final Context context) {
        Utils.context = context.getApplicationContext();
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    public static Context getContext() {
        if (context != null) {
            return context;
        }
        throw new NullPointerException("应该在Application初始化");
    }

}
