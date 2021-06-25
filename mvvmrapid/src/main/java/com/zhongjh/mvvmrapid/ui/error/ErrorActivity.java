package com.zhongjh.mvvmrapid.ui.error;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.zhongjh.mvvmrapid.BR;
import com.zhongjh.mvvmrapid.R;
import com.zhongjh.mvvmrapid.base.ui.BaseActivity;
import com.zhongjh.mvvmrapid.databinding.ActivityErrorBinding;
import com.zhongjh.mvvmrapid.utils.WindowUtil;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import cat.ereza.customactivityoncrash.config.CaocConfig;

/**
 * 自定义的一个异常Activity,因为CaocConfig没有提供获取异常消息后自定义处理，所以只能通过这个来处理了
 *
 * @author zhongjh
 * @date 2021/5/13
 */
public class ErrorActivity extends BaseActivity<ActivityErrorBinding, ErrorModel> {

    /**
     * 配置对象
     */
    private CaocConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowUtil.setFullScreen(ErrorActivity.this, false);
        super.onCreate(savedInstanceState);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_error;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public boolean isStartAutoHideSoftKeyboard() {
        return false;
    }

    @Override
    public void initParam() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        // 获取所有的信息
        String errorDetails = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this, getIntent());
        // 获取堆栈跟踪信息
        String stackTrace = CustomActivityOnCrash.getStackTraceFromIntent(getIntent());
        // 获取错误报告的Log信息
        String activityLog = CustomActivityOnCrash.getActivityLogFromIntent(getIntent());
        // 获得配置信息
        config = CustomActivityOnCrash.getConfigFromIntent(getIntent());

        binding.tvError.setText("【errorDetails】\n" + errorDetails + "\n\n\n【stackTrace】\n" + stackTrace + "\n\n\n【activityLog】\n" + activityLog);
        binding.tvError.setTextColor(Color.BLUE);
        viewModel.addLog(binding.tvError.getText().toString());
    }

    @Override
    public void initViewObservable() {
        viewModel.mUiChange.onClickRestart.observe(this, aVoid -> CustomActivityOnCrash.restartApplication(ErrorActivity.this, config));
    }

}
