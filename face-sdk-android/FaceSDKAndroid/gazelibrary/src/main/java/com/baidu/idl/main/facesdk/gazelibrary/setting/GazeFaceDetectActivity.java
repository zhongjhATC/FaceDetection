package com.baidu.idl.main.facesdk.gazelibrary.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.baidu.idl.main.facesdk.gazelibrary.BaseActivity;
import com.baidu.idl.main.facesdk.gazelibrary.R;
import com.baidu.idl.main.facesdk.gazelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.gazelibrary.utils.GazeConfigUtils;
import com.baidu.idl.main.facesdk.gazelibrary.utils.PWTextUtils;


public class GazeFaceDetectActivity extends BaseActivity implements View.OnClickListener {

    int zero = 0;
    int ten = 10;
    // 1:RGB模态生活照模型
    private static final int one = 1;
    // 2:RGB模态证件照模型
    private static final int two = 2;
    // 3:RGB+NIR混合模态模型
    private static final int three = 3;

    private static final int hundered = 100;

    private int activeModel;
    private ImageView qcSave;
    private LinearLayout rgbandnirLlMixture;
    private LinearLayout rgbandnirMixture;
    private int lightThreshold;
    private EditText thRgbandnirLiveEtThreshold;
    private EditText thLiveEtThreshold;
    private EditText thIDEtThreshold;
    private EditText mixtureIDEtThreshold;
    private int liveInitValue;
    private int idInitValue;
    private int rgbAndNirThreshold;
    private RadioGroup flsMixtureType;
    private LinearLayout flRepresent;

    private int showWidth;
    private int showXLocation;
    private RadioButton mixture_zero;
    private RadioButton mixture_one;
    private RadioButton mixture_two;
    private Button cwCameratype;

    private String msgTag = "";
    private TextView tvThreshold;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaze_face_detect);
        init();
    }

    private void init() {
        // 模型
        activeModel = SingleBaseConfig.getBaseConfig().getActiveModel();

        liveInitValue = SingleBaseConfig.getBaseConfig().getLiveThreshold();
        idInitValue = SingleBaseConfig.getBaseConfig().getIdThreshold();
        rgbAndNirThreshold = SingleBaseConfig.getBaseConfig().getRgbAndNirThreshold();
        // 模态切换光线阈值
        lightThreshold = SingleBaseConfig.getBaseConfig().getCamera_lightThreshold();

        // 模型和模态
        flsMixtureType = findViewById(R.id.fls_mixture_type);
        flsMixtureType.setOnCheckedChangeListener(liveType);
        mixture_zero = findViewById(R.id.mixture_zero);
        mixture_one = findViewById(R.id.mixture_one);
        mixture_two = findViewById(R.id.mixture_two);

        mixture_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rgbandnirLlMixture.setVisibility(View.VISIBLE);
                    rgbandnirMixture.setVisibility(View.VISIBLE);
                } else {
                    rgbandnirLlMixture.setVisibility(View.GONE);
                    rgbandnirMixture.setVisibility(View.GONE);
                }
            }
        });

        // RGB/NIR模态切换条件
        rgbandnirLlMixture = findViewById(R.id.rgbandnir_ll_mixture);
        rgbandnirMixture = findViewById(R.id.rgbandnir_mixture);
        ImageView thRgbandnirLiveDecrease = findViewById(R.id.th_rgbandnir_LiveDecrease);
        thRgbandnirLiveDecrease.setOnClickListener(this);
        thRgbandnirLiveEtThreshold = findViewById(R.id.th_rgbandnir_LiveEtThreshold);
        ImageView thRgbandnirLiveIncrease = findViewById(R.id.th_rgbandnir_LiveIncrease);
        thRgbandnirLiveIncrease.setOnClickListener(this);

        // 识别阈值
        // 生活照模型
        ImageView thLiveDecrease = findViewById(R.id.th_LiveDecrease);
        thLiveDecrease.setOnClickListener(this);
        thLiveEtThreshold = findViewById(R.id.th_LiveEtThreshold);
        ImageView thLiveIncrease = findViewById(R.id.th_LiveIncrease);
        thLiveIncrease.setOnClickListener(this);
        // 证件照模型
        ImageView thIDDecrease = findViewById(R.id.th_IDDecrease);
        thIDDecrease.setOnClickListener(this);
        thIDEtThreshold = findViewById(R.id.th_IDEtThreshold);
        ImageView thIDIncrease = findViewById(R.id.th_IDIncrease);
        thIDIncrease.setOnClickListener(this);
        // RGB+NIR混合模态阈值
        ImageView mixtureIDDecrease = findViewById(R.id.mixture_IDDecrease);
        mixtureIDDecrease.setOnClickListener(this);
        mixtureIDEtThreshold = findViewById(R.id.mixture_IDEtThreshold);
        ImageView mixtureIDIncrease = findViewById(R.id.mixture_IDIncrease);
        mixtureIDIncrease.setOnClickListener(this);

        qcSave = findViewById(R.id.qc_save);
        qcSave.setOnClickListener(this);
        flRepresent = findViewById(R.id.flRepresent);

        cwCameratype = findViewById(R.id.cw_cameratype);
        cwCameratype.setOnClickListener(this);
        tvThreshold = findViewById(R.id.tvthreshold);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwCameratype.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        if (SingleBaseConfig.getBaseConfig().getActiveModel() == 1) {
            mixture_zero.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getActiveModel() == 2) {
            mixture_one.setChecked(true);
        }
        if (SingleBaseConfig.getBaseConfig().getActiveModel() == 3) {
            mixture_two.setChecked(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeModel == one) {
            mixture_zero.setChecked(true);
        }
        if (activeModel == two) {
            mixture_one.setChecked(true);
        }
        if (activeModel == three) {
            mixture_two.setChecked(true);
        }

        thRgbandnirLiveEtThreshold.setText(lightThreshold + "");
        thLiveEtThreshold.setText(liveInitValue + "");
        thIDEtThreshold.setText(idInitValue + "");
        mixtureIDEtThreshold.setText(rgbAndNirThreshold + "");
    }

    public RadioGroup.OnCheckedChangeListener liveType = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.mixture_zero) {
                activeModel = one;
            } else if (checkedRadioButtonId == R.id.mixture_one) {
                activeModel = two;
            } else if (checkedRadioButtonId == R.id.mixture_two) {
                activeModel = three;
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.qc_save) {
            SingleBaseConfig.getBaseConfig().setCamera_lightThreshold(
                    Integer.valueOf(thRgbandnirLiveEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setLiveThreshold(
                    Integer.valueOf(thLiveEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setIdThreshold(
                    Integer.valueOf(thIDEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setRgbAndNirThreshold(
                    Integer.valueOf(mixtureIDEtThreshold.getText().toString()));

            if (activeModel == one) {
                SingleBaseConfig.getBaseConfig().setActiveModel(one);
            }
            if (activeModel == two) {
                SingleBaseConfig.getBaseConfig().setActiveModel(two);
            }
            if (activeModel == three) {
                SingleBaseConfig.getBaseConfig().setActiveModel(three);
            }

            GazeConfigUtils.modityJson();
            finish();
        } else if (id == R.id.th_rgbandnir_LiveDecrease) {
            if (lightThreshold > zero && lightThreshold <= hundered) {
                lightThreshold = lightThreshold - 5;
                thRgbandnirLiveEtThreshold.setText(lightThreshold + "");
            }
        } else if (id == R.id.th_rgbandnir_LiveIncrease) {
            if (lightThreshold >= zero && lightThreshold < hundered) {
                lightThreshold = lightThreshold + 5;
                thRgbandnirLiveEtThreshold.setText(lightThreshold + "");
            }
        } else if (id == R.id.th_LiveDecrease) {
            if (liveInitValue > zero && liveInitValue <= hundered) {
                liveInitValue = liveInitValue - 5;
                thLiveEtThreshold.setText(liveInitValue + "");
            }
        } else if (id == R.id.th_LiveIncrease) {
            if (liveInitValue >= zero && liveInitValue < hundered) {
                liveInitValue = liveInitValue + 5;
                thLiveEtThreshold.setText(liveInitValue + "");
            }
        } else if (id == R.id.th_IDDecrease) {
            if (idInitValue > zero && idInitValue <= hundered) {
                idInitValue = idInitValue - 5;
                thIDEtThreshold.setText(idInitValue + "");
            }
        } else if (id == R.id.th_IDIncrease) {
            if (idInitValue >= zero && idInitValue < hundered) {
                idInitValue = idInitValue + 5;
                thIDEtThreshold.setText(idInitValue + "");
            }
        } else if (id == R.id.mixture_IDDecrease) {
            if (rgbAndNirThreshold > zero && rgbAndNirThreshold <= hundered) {
                rgbAndNirThreshold = rgbAndNirThreshold - 5;
                mixtureIDEtThreshold.setText(rgbAndNirThreshold + "");
            }
        } else if (id == R.id.mixture_IDIncrease) {
            if (rgbAndNirThreshold >= zero && rgbAndNirThreshold < hundered) {
                rgbAndNirThreshold = rgbAndNirThreshold + 5;
                mixtureIDEtThreshold.setText(rgbAndNirThreshold + "");
            }
        } else if (id == R.id.cw_cameratype) {
            if (msgTag.equals(getString(R.string.cw_recognizethrehold))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_recognizethrehold);
            cwCameratype.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(cwCameratype, tvThreshold,
                    GazeFaceDetectActivity.this,
                    getString(R.string.cw_recognizethrehold),
                    showWidth, showXLocation);

        } else if (id == R.id.mixture_zero) {
            mixture_zero.setChecked(true);
            mixture_one.setChecked(false);
            mixture_two.setChecked(false);
            SingleBaseConfig.getBaseConfig().setActiveModel(1);
            GazeConfigUtils.modityJson();
        } else if (id == R.id.mixture_one) {
            mixture_zero.setChecked(false);
            mixture_one.setChecked(false);
            mixture_two.setChecked(false);
            SingleBaseConfig.getBaseConfig().setActiveModel(2);
            GazeConfigUtils.modityJson();
        } else if (id == R.id.mixture_two) {
            mixture_zero.setChecked(false);
            mixture_one.setChecked(false);
            mixture_two.setChecked(true);
            SingleBaseConfig.getBaseConfig().setActiveModel(3);
            GazeConfigUtils.modityJson();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = flsMixtureType.getWidth();
        showXLocation = (int) flRepresent.getLeft();
    }


}