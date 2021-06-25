package com.baidu.idl.main.facesdk.paymentlibrary.setting;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.paymentlibrary.R;
import com.baidu.idl.main.facesdk.paymentlibrary.activity.BaseActivity;
import com.baidu.idl.main.facesdk.paymentlibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.PaymentConfigUtils;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.RegisterConfigUtils;


/**
 * author : shangrog
 * date : 2019/5/27 6:41 PM
 * description :摄像头视频流回显角度
 */
public class CameraDisplayAngleActivity extends BaseActivity {
    private RadioButton cdaDisplayZeroAngle;
    private RadioButton cdaDisplayNinetyAngle;
    private RadioButton cdaDisplayOneHundredEighty;
    private RadioButton cdaDisplayTwoHundredSeventy;
    private int zero = 0;
    private int ninety = 90;
    private int oneHundredEighty = 180;
    private int twoHundredSeventy = 270;

    private LinearLayout linerCameraDisplay;
    private TextView tvCameraDisplay;
    private Button cwCameraDisplay;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
//    private LinearLayout linerBarCameraDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameradisplayangle);

//        linerBarCameraDisplay = findViewById(R.id.linerbarcameradisplay);
//        setBarColor();
//        setLightStatusBarColor(this);
//        setBarLayout(linerBarCameraDisplay);

        init();
    }

    public void init() {
        linerCameraDisplay = findViewById(R.id.linercameradisplay);
        tvCameraDisplay = findViewById(R.id.tvcameradisplay);
        cwCameraDisplay = findViewById(R.id.cwcameradisplay);

        cdaDisplayZeroAngle = findViewById(R.id.cda_display_zero_angle);
        cdaDisplayNinetyAngle = findViewById(R.id.cda_display_ninety_angle);
        cdaDisplayOneHundredEighty = findViewById(R.id.cda_display_one_hundred_eighty);
        cdaDisplayTwoHundredSeventy = findViewById(R.id.cda_display_two_hundred_seventy);

        ImageView cdaSave = findViewById(R.id.cda_save);

        if (SingleBaseConfig.getBaseConfig().getVideoDirection() == zero) {
            cdaDisplayZeroAngle.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getVideoDirection() == ninety) {
            cdaDisplayNinetyAngle.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getVideoDirection() == oneHundredEighty) {
            cdaDisplayOneHundredEighty.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getVideoDirection() == twoHundredSeventy) {
            cdaDisplayTwoHundredSeventy.setChecked(true);
        }

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwCameraDisplay.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        cdaDisplayZeroAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdaDisplayNinetyAngle.setChecked(false);
                cdaDisplayOneHundredEighty.setChecked(false);
                cdaDisplayTwoHundredSeventy.setChecked(false);

            }
        });

        cdaDisplayNinetyAngle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdaDisplayZeroAngle.setChecked(false);
                cdaDisplayOneHundredEighty.setChecked(false);
                cdaDisplayTwoHundredSeventy.setChecked(false);

            }
        });

        cdaDisplayOneHundredEighty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdaDisplayZeroAngle.setChecked(false);
                cdaDisplayNinetyAngle.setChecked(false);
                cdaDisplayTwoHundredSeventy.setChecked(false);

            }
        });

        cdaDisplayTwoHundredSeventy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cdaDisplayZeroAngle.setChecked(false);
                cdaDisplayNinetyAngle.setChecked(false);
                cdaDisplayOneHundredEighty.setChecked(false);

            }
        });

        cdaSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cdaDisplayZeroAngle.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setVideoDirection(zero);
                }
                if (cdaDisplayNinetyAngle.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setVideoDirection(ninety);
                }
                if (cdaDisplayOneHundredEighty.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setVideoDirection(oneHundredEighty);
                }
                if (cdaDisplayTwoHundredSeventy.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setVideoDirection(twoHundredSeventy);
                }
                PaymentConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            }
        });

        cwCameraDisplay.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_cameradisplayangle))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_cameradisplayangle);
                cwCameraDisplay.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerCameraDisplay, tvCameraDisplay, CameraDisplayAngleActivity.this,
                        getString(R.string.cw_cameradisplayangle), showWidth, showXLocation);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = linerCameraDisplay.getWidth() - 40;
        showXLocation = 20;
    }
}
