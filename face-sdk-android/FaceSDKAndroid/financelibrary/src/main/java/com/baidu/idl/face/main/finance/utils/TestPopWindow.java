package com.baidu.idl.face.main.finance.utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.baidu.idl.main.facesdk.financelibrary.R;

public class TestPopWindow extends PopupWindow {
    private String TAG = "TestPopWindow";
    private final Context gContext;
    private View view;

    public TestPopWindow(Context context) {
        this(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Log.d(TAG, "TestPopWindow: " + ViewGroup.LayoutParams.WRAP_CONTENT
                + "bbb:" + ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public TestPopWindow(Context context, int width, int height) {
        super(context);
        this.gContext = context;
        view = View.inflate(context, R.layout.layout_no_face_detected, null);
        view.findViewById(R.id.retest_detectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickFinance.rester(true);
            }
        });
        view.findViewById(R.id.back_homeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnClickFinance.rester(false);
            }
        });

        setContentView(view);
        // 设置窗口的高和宽
        setWidth(width);
        setHeight(height);
        // 设置弹窗内科点击
        setTouchable(true);
        setOutsideTouchable(true);
        setFocusable(true);
        // TODO去除背景
        setBackgroundDrawable(null);

    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent, Gravity.CENTER, 0, -50);
        } else {
            this.dismiss();
        }
    }

    public void closePopupWindow() {
        if (this.isShowing()) {
            this.dismiss();
        }
    }


    public void setmOnClickFinance(OnClickFinance mOnClickFinance) {
        this.mOnClickFinance = mOnClickFinance;
    }

    public OnClickFinance mOnClickFinance;

    public interface OnClickFinance {
        void rester(boolean isReTest);
    }
}
