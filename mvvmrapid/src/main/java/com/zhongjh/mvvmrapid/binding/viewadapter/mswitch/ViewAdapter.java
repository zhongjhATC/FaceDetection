package com.zhongjh.mvvmrapid.binding.viewadapter.mswitch;

import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.BindingAdapter;

import com.zhongjh.mvvmrapid.binding.command.BindingCommand;

public class ViewAdapter {

    /**
     * 设置开关状态
     *
     * @param mSwitch Switch控件
     */
    @BindingAdapter("switchState")
    public static void setSwitchState(SwitchCompat mSwitch, boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    /**
     * Switch的状态改变监听
     *
     * @param mSwitch        Switch控件
     * @param changeListener 事件绑定命令
     */
    @BindingAdapter("onCheckedChangeCommand")
    public static void onCheckedChangeCommand(final SwitchCompat mSwitch, final BindingCommand<Boolean> changeListener) {
        if (changeListener != null) {
            mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> changeListener.execute(isChecked));
        }
    }
}
