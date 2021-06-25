package com.baidu.idl.main.facesdk.identifylibrary.setting;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.identifylibrary.BaseActivity;
import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.identifylibrary.utils.IdentifyConfigUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.RegisterConfigUtils;


/**
 * author : shangrog
 * date : 2019/5/27 6:34 PM
 * description :最小人脸界面
 */

public class MinFaceActivity extends BaseActivity {
    private EditText mfEtAmount;
    private int initValue;
    private int thirty = 30;
    private int twoHundered = 200;
    private static final int ten = 10;

    private LinearLayout linerMinFace;
    private TextView minFaceText;
    private Button minFace;
    private String tagMsg = "";
    private LinearLayout minRepresent;
    private int showWidth;
    private int showXLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minface);

        init();
    }


    public void init() {
        initValue = SingleBaseConfig.getBaseConfig().getMinimumFace();
        minRepresent = findViewById(R.id.minRepresent);

        linerMinFace = findViewById(R.id.linerminface);
        minFaceText = findViewById(R.id.minFaceText);
        minFace = findViewById(R.id.minface);

        ImageView mfDecrease = findViewById(R.id.mf_Decrease);
        ImageView mfIncrease = findViewById(R.id.mf_Increase);
        mfEtAmount = findViewById(R.id.mf_etAmount);
        ImageView mfSave = findViewById(R.id.mf_save);
        mfEtAmount.setText(SingleBaseConfig.getBaseConfig().getMinimumFace() + "");

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                minFace.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        mfDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initValue > thirty && initValue <= twoHundered) {
                    initValue = initValue - ten;
                    mfEtAmount.setText(initValue + "");
                }
            }
        });

        mfIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initValue >= thirty && initValue < twoHundered) {
                    initValue = initValue + ten;
                    mfEtAmount.setText(initValue + "");
                }
            }
        });

        mfSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleBaseConfig.getBaseConfig().setMinimumFace(Integer.valueOf(mfEtAmount.getText().toString()));
                IdentifyConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            }
        });

        minFace.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (tagMsg.equals(getString(R.string.cw_minface))) {
                    tagMsg = "";
                    return;
                }
                tagMsg = getString(R.string.cw_minface);
                minFace.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerMinFace, minFaceText, MinFaceActivity.this,
                        getString(R.string.cw_minface), showWidth, showXLocation);
            }
        });
    }


    private void showAlertAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = minRepresent.getWidth();
        showXLocation = (int) minRepresent.getX();
    }

}
