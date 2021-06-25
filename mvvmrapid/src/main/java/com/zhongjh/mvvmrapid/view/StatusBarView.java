package com.zhongjh.mvvmrapid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.tencent.mmkv.MMKV;
import com.zhongjh.mvvmrapid.constants.Constants;
import com.zhongjh.mvvmrapid.utils.StatusBarUtil;

/**
 * Created by zhongjh on 2021/4/9.
 * 这是使用状态栏的默认高度，配合StatusBarUtils.initStatusBarHeight使用
 */
public class StatusBarView extends View {

    int statusBarHeight;

    public StatusBarView(Context context) {
        this(context, null);
    }

    public StatusBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 设置高度
        setMeasuredDimension(widthMeasureSpec, statusBarHeight);
    }

    public void setup() {
        statusBarHeight = StatusBarUtil.getStatusBarHeight(getResources());
    }

}
