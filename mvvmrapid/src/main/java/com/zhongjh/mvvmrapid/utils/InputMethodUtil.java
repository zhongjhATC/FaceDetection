package com.zhongjh.mvvmrapid.utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 *
 * @author zhongjh
 * @date 2021/3/31
 * 软键盘工具类
 */
public class InputMethodUtil {

    /**
     * 隐藏软键盘
     * @param activity activity
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
