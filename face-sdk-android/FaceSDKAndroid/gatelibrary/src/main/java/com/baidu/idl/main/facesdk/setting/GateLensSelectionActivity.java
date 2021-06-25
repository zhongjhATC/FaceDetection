package com.baidu.idl.main.facesdk.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.idl.main.facesdk.activity.BaseActivity;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.utils.GateConfigUtils;
import com.baidu.idl.main.facesdk.utils.RegisterConfigUtils;
import com.baidu.idl.main.facesdk.utils.ToastUtils;


public class GateLensSelectionActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton fltZero;
    private RadioButton fltOne;
    private RadioButton fltTwo;
    private RadioButton fltThree;
    private RadioButton fltFour;
    private RadioButton fltFive;
    private RadioButton fltSix;
    private RadioButton fltSeven;
    private RadioButton fltEight;

    private int cameraTypeValue;

    // 0:奥比中光海燕、大白（640*400）
    private static final int zero = 0;
    // 1:奥比中光海燕Pro、Atlas（400*640）
    private static final int one = 1;
    // 2:奥比中光蝴蝶、Astra Pro\Pro S（640*480）
    private static final int two = 2;
    // 3:舜宇Seeker06
    private static final int three = 3;
    // 4:螳螂慧视天蝎P1
    private static final int four = 4;
    // 5:瑞识M720N
    private static final int five = 5;
    // 6:奥比中光Deeyea(结构光)
    private static final int six = 6;
    // 7:华捷艾米A100S、A200(结构光)
    private static final int seven = 7;
    // 6:Pico DCAM710(ToF)
    private static final int eight = 8;
    private RadioGroup flsCameraType;
    private int liveTypeValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lens_selections);
        init();

    }

    private void init() {
        fltZero = findViewById(R.id.flt_zero);
        fltOne = findViewById(R.id.flt_one);
        fltTwo = findViewById(R.id.flt_two);
        fltThree = findViewById(R.id.flt_three);
        fltFour = findViewById(R.id.flt_four);
        fltFive = findViewById(R.id.flt_five);
        fltSix = findViewById(R.id.flt_six);
        fltSeven = findViewById(R.id.flt_seven);
        fltEight = findViewById(R.id.flt_eight);

        ImageView flsSave = findViewById(R.id.fls_save);
        flsSave.setOnClickListener(this);
        cameraTypeValue = SingleBaseConfig.getBaseConfig().getCameraType();
        liveTypeValue = SingleBaseConfig.getBaseConfig().getType();

        if (liveTypeValue == three || liveTypeValue == four) {
            setlectCamera();
        }

        flsCameraType = findViewById(R.id.fls_camera_type);
        flsCameraType.setOnCheckedChangeListener(cameraType);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraTypeValue == zero) {
            fltZero.setChecked(true);
        }
        if (cameraTypeValue == one) {
            fltOne.setChecked(true);
        }
        if (cameraTypeValue == two) {
            fltTwo.setChecked(true);
        }
        if (cameraTypeValue == three) {
            fltThree.setChecked(true);
        }
        if (cameraTypeValue == four) {
            fltFour.setChecked(true);
        }
        if (cameraTypeValue == five) {
            fltFive.setChecked(true);
        }
        if (cameraTypeValue == six) {
            fltSix.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fls_save) {
            if (fltZero.isChecked() || fltOne.isChecked() || fltTwo.isChecked()
                    || fltThree.isChecked() || fltFour.isChecked() || fltFive.isChecked()
                    || fltSix.isChecked() || fltSeven.isChecked() || fltEight.isChecked()) {
                cameraSelect();
                GateConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            } else {
                ToastUtils.toast(this, "请选择镜头型号在进行返回操作");
            }
        }
    }

    public void cameraSelect() {
        if (cameraTypeValue == zero) {
            SingleBaseConfig.getBaseConfig().setCameraType(zero);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(400);
        }
        if (cameraTypeValue == one) {
            SingleBaseConfig.getBaseConfig().setCameraType(one);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(400);
        }
        if (cameraTypeValue == two) {
            SingleBaseConfig.getBaseConfig().setCameraType(two);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(480);
        }
        if (cameraTypeValue == three) {
            SingleBaseConfig.getBaseConfig().setCameraType(three);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(480);
        }
        if (cameraTypeValue == four) {
            SingleBaseConfig.getBaseConfig().setCameraType(four);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(480);
        }
        if (cameraTypeValue == five) {
            SingleBaseConfig.getBaseConfig().setCameraType(five);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(480);
        }
        if (cameraTypeValue == six) {
            SingleBaseConfig.getBaseConfig().setCameraType(six);

            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
            SingleBaseConfig.getBaseConfig().setDepthWidth(640);
            SingleBaseConfig.getBaseConfig().setDepthHeight(480);
        }
    }

    public RadioGroup.OnCheckedChangeListener cameraType = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.flt_zero) {
                cameraTypeValue = zero;
            } else if (checkedRadioButtonId == R.id.flt_one) {
                cameraTypeValue = one;
            } else if (checkedRadioButtonId == R.id.flt_two) {
                cameraTypeValue = two;
            } else if (checkedRadioButtonId == R.id.flt_three) {
                cameraTypeValue = three;
            } else if (checkedRadioButtonId == R.id.flt_four) {
                cameraTypeValue = four;
            } else if (checkedRadioButtonId == R.id.flt_five) {
                cameraTypeValue = five;
            } else if (checkedRadioButtonId == R.id.flt_six) {
                cameraTypeValue = six;
            } else if (checkedRadioButtonId == R.id.flt_seven) {
                cameraTypeValue = seven;
            } else if (checkedRadioButtonId == R.id.flt_eight) {
                cameraTypeValue = eight;
            }
        }
    };


    public void setlectCamera() {
        if (cameraTypeValue == zero) {
            fltZero.setChecked(true);
        }
        if (cameraTypeValue == one) {
            fltOne.setChecked(true);
        }
        if (cameraTypeValue == two) {
            fltTwo.setChecked(true);
        }
        if (cameraTypeValue == three) {
            fltThree.setChecked(true);
        }
        if (cameraTypeValue == four) {
            fltFour.setChecked(true);
        }
        if (cameraTypeValue == five) {
            fltFive.setChecked(true);
        }
        if (cameraTypeValue == six) {
            fltSix.setChecked(true);
        }
        if (cameraTypeValue == seven) {
            fltSix.setChecked(true);
        }
        if (cameraTypeValue == eight) {
            fltSix.setChecked(true);
        }
    }
}