package com.baidu.idl.main.facesdk.registerlibrary.user.utils;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.registerlibrary.R;


public class PWTextUtils {
    private static PopupWindow popupWindow;
    private static PopupWindow.OnDismissListener onDismissListener;

    public static void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        PWTextUtils.onDismissListener = onDismissListener;
    }

    /**
     * @param target    显示在哪个View下方
     * @param reference 使用此View在屏幕上的X坐标来控制显示View的X坐标
     * @param context
     * @param text      需要显示的内容
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void showDescribeText(View target, View reference, Context context,
                                        String text, int showWidth, int showXLocation) {
        popupWindow = new PopupWindow();
        if (onDismissListener != null) {
            popupWindow.setOnDismissListener(onDismissListener);
        }
        popupWindow.setWidth(showWidth);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        View pView = LayoutInflater.from(context).inflate(R.layout.popupwindow_text, null);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.cw_round));
        TextView textView = pView.findViewById(R.id.showText);
        textView.setText(text);
        popupWindow.setContentView(pView);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.sr_pw_rectangle));
        popupWindow.showAsDropDown(target, showXLocation, 0, Gravity.CENTER);
    }

    public static int getTargetX(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
        return location[0];
    }

    public static void closePop(Window window) {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.alpha = 1.0f;
            window.setAttributes(lp);
        }
    }


}
