package com.zhongjh.mvvmrapid.binding.viewadapter.edittext;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.BindingAdapter;

import com.zhongjh.mvvmrapid.binding.command.BindingCommand;


public class ViewAdapter {

    /**
     * EditText重新获取焦点的事件绑定
     */
    @BindingAdapter(value = {"requestFocus"}, requireAll = false)
    public static void requestFocusCommand(AppCompatEditText editText, final Boolean needRequestFocus) {
        if (needRequestFocus) {
            editText.setSelection(editText.getText().length());
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        editText.setFocusableInTouchMode(needRequestFocus);
    }

    /**
     * EditText输入文字改变的监听
     */
    @BindingAdapter(value = {"textChanged"}, requireAll = false)
    public static void addTextChangedListener(AppCompatEditText editText, final BindingCommand<String> textChanged) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (textChanged != null) {
                    textChanged.execute(text.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}
