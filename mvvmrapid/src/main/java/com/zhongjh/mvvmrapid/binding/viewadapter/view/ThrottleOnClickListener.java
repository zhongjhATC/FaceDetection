package com.zhongjh.mvvmrapid.binding.viewadapter.view;

import android.view.View;

/**
 * 1秒只允许点击一次
 */
public abstract class ThrottleOnClickListener implements View.OnClickListener {

    private long mLastClickTime;

    public ThrottleOnClickListener() {
    }

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime > 1000L) {
            // 经过了足够长的时间，允许点击
            onClick();
            mLastClickTime = currentTime;
        }
    }

    protected abstract void onClick();
}
