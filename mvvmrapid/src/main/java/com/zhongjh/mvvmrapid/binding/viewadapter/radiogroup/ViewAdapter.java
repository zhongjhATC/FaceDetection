package com.zhongjh.mvvmrapid.binding.viewadapter.radiogroup;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.BindingAdapter;

import com.zhongjh.mvvmrapid.binding.command.BindingCommand;

public class ViewAdapter {

    @BindingAdapter(value = {"onCheckedChangedCommand"}, requireAll = false)
    public static void onCheckedChangedCommand(final RadioGroup radioGroup, final BindingCommand<String> bindingCommand) {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = group.findViewById(checkedId);
            bindingCommand.execute(radioButton.getText().toString());
        });
    }
}
