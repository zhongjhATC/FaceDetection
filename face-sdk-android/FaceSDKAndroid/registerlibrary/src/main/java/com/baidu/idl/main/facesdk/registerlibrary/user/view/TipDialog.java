package com.baidu.idl.main.facesdk.registerlibrary.user.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.registerlibrary.R;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.DensityUtils;


/**
 * Project: FaceSDKAndroid
 * User: v_liujialu01
 * Date: 2020/02/06
 */

public class TipDialog extends Dialog implements View.OnClickListener {
    private TextView mTextTitle;
    private TextView mTextMessage;
    private Button mBtnConfirm;
    private Button mBtnCancel;

    private OnTipDialogClickListener mOnTipDialogClickListener;
    private String mTipType;
    private Context mContext;

    public TipDialog(@NonNull Context context) {
        super(context, R.style.TipDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_tip, null);
        setContentView(view);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int widthPx = DensityUtils.getDisplayWidth(getContext());
        int dp = DensityUtils.px2dip(getContext(), widthPx) - 40;
        lp.width = DensityUtils.dip2px(getContext(), dp);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
        mTextTitle = view.findViewById(R.id.text_dialog_title);
        mTextMessage = view.findViewById(R.id.text_dialog_message);
        mBtnConfirm = view.findViewById(R.id.btn_dialog_confirm);
        mBtnConfirm.setOnClickListener(this);
        mBtnCancel = view.findViewById(R.id.btn_dialog_exit);
        mBtnCancel.setOnClickListener(this);
    }

    public void setOnTipDialogClickListener(OnTipDialogClickListener listener) {
        mOnTipDialogClickListener = listener;
    }

    public void setTextTitle(String title) {
        if (mTextTitle != null) {
            mTextTitle.setText(title);
            mTipType = title;
        }
    }

    public void setTextMessage(String message) {
        if (mTextMessage != null) {
            mTextMessage.setText(message);
        }
    }

    public void setTextConfirm(String confirm) {
        if (mBtnConfirm != null) {
            mBtnConfirm.setText(confirm);
        }
    }

    public void setTextCancel(String cancel) {
        if (mBtnCancel != null) {
            mBtnCancel.setText(cancel);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_dialog_confirm) {
            if (mOnTipDialogClickListener != null) {
                mOnTipDialogClickListener.onConfirm(mTipType);
                ;
            }
        } else if (id == R.id.btn_dialog_exit) {
            if (mOnTipDialogClickListener != null) {
                mOnTipDialogClickListener.onCancel();
            }
        }
    }

    public interface OnTipDialogClickListener {
        void onCancel();

        void onConfirm(String tipType);
    }
}
