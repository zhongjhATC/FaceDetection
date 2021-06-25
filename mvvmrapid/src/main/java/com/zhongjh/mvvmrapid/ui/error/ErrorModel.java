package com.zhongjh.mvvmrapid.ui.error;

import android.app.Application;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.zhongjh.mvvmrapid.base.viewmodel.BaseViewModel;
import com.zhongjh.mvvmrapid.binding.command.BindingAction;
import com.zhongjh.mvvmrapid.binding.command.BindingCommand;
import com.zhongjh.mvvmrapid.binding.command.BindingConsumer;
import com.zhongjh.mvvmrapid.bus.event.SingleLiveEvent;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

/**
 * @author zhongjh
 * @date 2021/4/15
 */
public class ErrorModel extends BaseViewModel {

    public ErrorModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 界面发生改变的观察者
     */
    public UiChangeObservable mUiChange = new UiChangeObservable();

    /**
     * 通知UI事件
     */
    public static class UiChangeObservable {
        // 登录界面的账号错误
        public SingleLiveEvent<Void> onClickRestart = new SingleLiveEvent<>();
    }

    /**
     * 重启
     */
    public BindingCommand<Void> onClickRestart = new BindingCommand<>(new BindingAction() {
        @Override
        public void call() {
            onClickRestart.execute();
        }
    });

    /**
     * 添加日志
     * @param error 所有异常信息
     */
    public void addLog(String error) {
        LogUtils.e(error);
    }

}
