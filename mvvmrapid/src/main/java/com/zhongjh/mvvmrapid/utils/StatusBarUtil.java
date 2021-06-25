package com.zhongjh.mvvmrapid.utils;

import android.content.res.Resources;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.tencent.mmkv.MMKV;
import com.zhongjh.mvvmrapid.constants.Constants;

/**
 * Created by zhongjh on 2021/4/8.
 * 状态栏工具
 */
public class StatusBarUtil {

    /**
     * 初始化状态栏高度
     *
     * @param view 该view必须有android:fitsSystemWindows="true"属性，并且生效，如果不生效是不会获取到状态栏高度的
     */
    public static void initStatusBarHeight(View view) {
        // 判断是否缓存了mmkv
        MMKV kv = MMKV.defaultMMKV();
        int statusBarHeight = kv.decodeInt(Constants.STATUS_BAR_HEIGHT);
        if (statusBarHeight <= 0) {
            ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                kv.encode(Constants.STATUS_BAR_HEIGHT, insets.getSystemWindowInsetTop());
                return insets;
            });
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Resources resources) {
        // 获得状态栏高度
        MMKV kv = MMKV.defaultMMKV();
        if (kv != null) {
            if (kv.contains(Constants.STATUS_BAR_HEIGHT)) {
                return kv.decodeInt(Constants.STATUS_BAR_HEIGHT);
            } else {
                return getStatusBarHeightResources(resources);
            }
        }
        // 需要调用initStatusBarHeight，google官方推荐以这种方式获取
        return 0;
    }

    private static int getStatusBarHeightResources(Resources resources) {
        int result = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
