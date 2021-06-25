package com.baidu.idl.main.facesdk.view;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

/**
 * author : shangrong
 * date : 2019/6/10 10:12 PM
 * description :
 */
public class RoundCheckBox extends AppCompatCheckBox {
    public RoundCheckBox(Context context) {
        this(context, null);
    }

    public RoundCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.radioButtonStyle);
    }

    public RoundCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
