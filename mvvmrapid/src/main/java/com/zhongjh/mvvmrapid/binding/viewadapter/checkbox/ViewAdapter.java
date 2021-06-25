package com.zhongjh.mvvmrapid.binding.viewadapter.checkbox;


import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.databinding.BindingAdapter;

import com.zhongjh.mvvmrapid.binding.command.BindingCommand;


public class ViewAdapter {

    /**
     * 绑定监听
     * @param bindingCommand 带参数的回调
     */
    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void setCheckedChanged(final AppCompatCheckBox checkBox, final BindingCommand<Boolean> bindingCommand) {
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> bindingCommand.execute(b));
    }
}
