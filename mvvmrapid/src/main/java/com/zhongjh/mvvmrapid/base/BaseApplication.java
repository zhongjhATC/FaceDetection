package com.zhongjh.mvvmrapid.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.tencent.mmkv.MMKV;
import com.zhongjh.mvvmrapid.utils.Utils;

/**
 * application基类
 * @author zhongjh
 */
public class BaseApplication extends Application {

    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        setApplication(this);
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("请调用setApplication方法或者继承于BaseApplication");
        }
        return sInstance;
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     */
    public static synchronized void setApplication(@NonNull Application application) {
        sInstance = application;
        // 初始化工具类
        Utils.init(application);
        // 初始化MMKV存储数据
        MMKV.initialize(application);
        // 注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                AppManager.getAppManager().addActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                AppManager.getAppManager().removeActivity(activity);
            }
        });
    }


}
