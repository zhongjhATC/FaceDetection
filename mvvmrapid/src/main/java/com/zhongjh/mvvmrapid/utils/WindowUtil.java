package com.zhongjh.mvvmrapid.utils;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 *
 * @author zhongjh
 * @date 2021/3/25
 * Window有关工具
 */
public class WindowUtil {

    /**
     * 设置全屏
     *
     * @param activity            当前activity
     * @param isBottomTranslucent 是否优化底部，建议只有splash界面设置，其他界面因为兼容问题，还是不处理合适
     */
    public static void setFullScreen(Activity activity, boolean isBottomTranslucent) {
        // http为空的才启用全屏，因为外链是没有title返回的
        int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        int flagTranslucentNavigation = 0;
        if (isBottomTranslucent) {
            // 用这个会导致缩小导航栏的时候底部空白
            flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (isBottomTranslucent) {
                // 用这个会导致缩小导航栏的时候底部空白
                attributes.flags |= flagTranslucentNavigation;
            }
            window.setAttributes(attributes);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (isBottomTranslucent) {
                // 用这个会导致缩小导航栏的时候底部空白
                attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;
            }
            attributes.flags |= flagTranslucentStatus;
            window.setAttributes(attributes);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }



}
