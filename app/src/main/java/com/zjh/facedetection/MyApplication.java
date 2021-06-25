package com.zjh.facedetection;


import android.view.Gravity;

import com.blankj.utilcode.util.LogUtils;
import com.zjh.facedetection.constants.FilePaths;
import com.zjh.facedetection.ui.main.MainActivity;
import com.squareup.leakcanary.LeakCanary;
import com.zhongjh.mvvmrapid.BuildConfig;
import com.zhongjh.mvvmrapid.base.BaseApplication;
import com.zhongjh.mvvmrapid.ui.error.ErrorActivity;
import com.zhongjh.mvvmrapid.utils.KLog;
import com.zhongjh.mvvmrapid.utils.ToastUtils;

import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * @author zhongjh
 * @date 2021/3/25
 * <p>
 * 代码规范：https://github.com/getActivity/AndroidCodeStandard
 *
 */
public class MyApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // 是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        // 初始化全局异常崩溃
        initCrash();
        // 初始化Log
        initLog();
        // 初始化Toast的全局样式
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);

        // 内存泄漏检测
        if (!LeakCanary.isInAnalyzerProcess(this)) {
            LeakCanary.install(this);
        }
    }

    /**
     * 异常奔溃后自动打开新的Activity,还可以选择重新启动
     */
    private void initCrash() {
        CaocConfig.Builder.create()
                // 背景模式,开启沉浸式
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT)
                // 是否启动全局异常捕获
                .enabled(true)
                // 是否显示错误详细信息
                .showErrorDetails(true)
                // 是否显示重启按钮
                .showRestartButton(true)
                // 是否跟踪Activity
                .trackActivities(true)
                // 崩溃的间隔时间(毫秒)
                .minTimeBetweenCrashesMs(2000)
                // 错误图标
                .errorDrawable(R.mipmap.ic_launcher)
                // 重新启动后的activity
                .restartActivity(MainActivity.class)
                // 崩溃后的错误监听
//                .eventListener(new YourCustomEventListener())
                // 崩溃后的错误activity
                .errorActivity(ErrorActivity.class)
                .apply();
    }

    /**
     * 初始化log，搭配奔溃把奔溃信息存储到Log
     */
    private void initLog() {
        LogUtils.getConfig().setLogSwitch(true).setLog2FileSwitch(true)
                .setDir(FilePaths.log(this)).setSaveDays(7);
    }

}
