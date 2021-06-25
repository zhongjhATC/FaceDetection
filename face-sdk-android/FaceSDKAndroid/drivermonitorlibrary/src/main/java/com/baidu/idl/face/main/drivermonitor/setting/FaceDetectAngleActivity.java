package com.baidu.idl.face.main.drivermonitor.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.idl.face.main.drivermonitor.activity.DrivermonitorBaseActivity;
import com.baidu.idl.face.main.drivermonitor.model.SingleBaseConfig;
import com.baidu.idl.face.main.drivermonitor.utils.DriverMonitorConfigUtils;
import com.baidu.idl.face.main.drivermonitor.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.drivermonitor.R;


/**
 * author : shangrog
 * date : 2019/5/27 6:37 PM
 * description :人脸检测角度设置
 */
public class FaceDetectAngleActivity extends DrivermonitorBaseActivity {
    private RadioButton fdaPreviewZeroAngle;
    private RadioButton fdaPreviewNinetyAngle;
    private RadioButton fdaPreviewOneHundredEighty;
    private RadioButton fdaPreviewTwoHundredSeventy;

    private int zero = 0;
    private int ninety = 90;
    private int oneHundredEighty = 180;
    private int twoHundredSeventy = 270;

    private LinearLayout linerdetectangle;
    private TextView tvdetectangle;
    private Button cwdetectangle;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;

//    private LinearLayout linerBarFaceDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facedetectangle);

//        linerBarFaceDetect = findViewById(R.id.linerbarfacedetect);
//        setBarColor();
//        setWhiteStatusBarColor(this);
//        setBarLayout(linerBarFaceDetect);

        init();
    }

    public void init() {
        linerdetectangle = findViewById(R.id.linerdetectangle);
        tvdetectangle = findViewById(R.id.tvdetectangle);
        cwdetectangle = findViewById(R.id.cwdetectangle);

        fdaPreviewZeroAngle = findViewById(R.id.fda_preview_zero_angle);
        fdaPreviewNinetyAngle = findViewById(R.id.fda_preview_ninety_angle);
        fdaPreviewOneHundredEighty = findViewById(R.id.fda_preview_one_hundred_eighty);
        fdaPreviewTwoHundredSeventy = findViewById(R.id.fda_preview_two_hundred_seventy);
        ImageView fdaSave = findViewById(R.id.fda_save);

        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == zero) {
            fdaPreviewZeroAngle.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == ninety) {
            fdaPreviewNinetyAngle.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == oneHundredEighty) {
            fdaPreviewOneHundredEighty.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getDetectDirection() == twoHundredSeventy) {
            fdaPreviewTwoHundredSeventy.setChecked(true);
        }

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwdetectangle.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        fdaPreviewZeroAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdaPreviewNinetyAngle.setChecked(false);
                fdaPreviewOneHundredEighty.setChecked(false);
                fdaPreviewTwoHundredSeventy.setChecked(false);

            }
        });

        fdaPreviewNinetyAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdaPreviewZeroAngle.setChecked(false);
                fdaPreviewOneHundredEighty.setChecked(false);
                fdaPreviewTwoHundredSeventy.setChecked(false);

            }
        });

        fdaPreviewOneHundredEighty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdaPreviewZeroAngle.setChecked(false);
                fdaPreviewNinetyAngle.setChecked(false);
                fdaPreviewTwoHundredSeventy.setChecked(false);

            }
        });

        fdaPreviewTwoHundredSeventy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdaPreviewZeroAngle.setChecked(false);
                fdaPreviewNinetyAngle.setChecked(false);
                fdaPreviewOneHundredEighty.setChecked(false);

            }
        });

        fdaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fdaPreviewZeroAngle.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(zero);
                }
                if (fdaPreviewNinetyAngle.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(ninety);
                }
                if (fdaPreviewOneHundredEighty.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(oneHundredEighty);
                }
                if (fdaPreviewTwoHundredSeventy.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setDetectDirection(twoHundredSeventy);
                }
                DriverMonitorConfigUtils.modityJson();
                finish();
            }
        });


        cwdetectangle.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_detectangle))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_detectangle);
                cwdetectangle.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerdetectangle, tvdetectangle, FaceDetectAngleActivity.this,
                        getString(R.string.cw_detectangle), showWidth, showXLocation);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = linerdetectangle.getWidth() - 40;
        showXLocation = 20;
    }
}
