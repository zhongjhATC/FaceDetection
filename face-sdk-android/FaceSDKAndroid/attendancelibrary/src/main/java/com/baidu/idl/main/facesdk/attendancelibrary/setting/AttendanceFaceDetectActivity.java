package com.baidu.idl.main.facesdk.attendancelibrary.setting;

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

import com.baidu.idl.main.facesdk.attendancelibrary.BaseActivity;
import com.baidu.idl.main.facesdk.attendancelibrary.R;
import com.baidu.idl.main.facesdk.attendancelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.AttendanceConfigUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.RegisterConfigUtils;


public class AttendanceFaceDetectActivity extends BaseActivity implements View.OnClickListener {

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
    private TextView thLiveTvThreshold;
    private TextView thIDTvThreshold;
    private TextView mixtureIDTvThreshold;
    private ImageView mixtureIDDecreaseAshDisposal;
    private ImageView mixtureIDIncreaseAshDisposal;
    private ImageView thIDDecreaseAshDisposal;
    private ImageView thIDIncreaseAshDisposal;
    private ImageView thLiveDecreaseAshDisposal;
    private ImageView thLiveIncreaseAshDisposal;
    private ImageView thLiveDecrease;
    private ImageView thLiveIncrease;
    private ImageView thIDDecrease;
    private ImageView thIDIncrease;
    private ImageView mixtureIDDecrease;
    private ImageView mixtureIDIncrease;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_face_detect);
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
        thLiveDecrease = findViewById(R.id.th_LiveDecrease);
        thLiveDecrease.setOnClickListener(this);
        thLiveEtThreshold = findViewById(R.id.th_LiveEtThreshold);
        thLiveIncrease = findViewById(R.id.th_LiveIncrease);
        thLiveIncrease.setOnClickListener(this);
        // 证件照模型
        thIDDecrease = findViewById(R.id.th_IDDecrease);
        thIDDecrease.setOnClickListener(this);
        thIDEtThreshold = findViewById(R.id.th_IDEtThreshold);
        thIDIncrease = findViewById(R.id.th_IDIncrease);
        thIDIncrease.setOnClickListener(this);
        // RGB+NIR混合模态阈值
        mixtureIDDecrease = findViewById(R.id.mixture_IDDecrease);
        mixtureIDDecrease.setOnClickListener(this);
        mixtureIDEtThreshold = findViewById(R.id.mixture_IDEtThreshold);
        mixtureIDIncrease = findViewById(R.id.mixture_IDIncrease);
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

        thLiveTvThreshold = findViewById(R.id.th_LiveTvThreshold);
        thIDTvThreshold = findViewById(R.id.th_IDTvThreshold);
        mixtureIDTvThreshold = findViewById(R.id.mixture_IDTvThreshold);
        mixtureIDDecreaseAshDisposal = findViewById(R.id.mixture_IDDecrease_Ash_disposal);
        mixtureIDIncreaseAshDisposal = findViewById(R.id.mixture_IDIncrease_Ash_disposal);
        thIDDecreaseAshDisposal = findViewById(R.id.th_IDDecrease_Ash_disposal);
        thIDIncreaseAshDisposal = findViewById(R.id.th_IDIncrease_Ash_disposal);
        thLiveDecreaseAshDisposal = findViewById(R.id.th_LiveDecrease_Ash_disposal);
        thLiveIncreaseAshDisposal = findViewById(R.id.th_LiveIncrease_Ash_disposal);

        mixture_zero.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mixture_zero.setChecked(true);
                    mixture_one.setChecked(false);
                    mixture_two.setChecked(false);
                    SingleBaseConfig.getBaseConfig().setActiveModel(1);

                    mixture_zero.setTextColor(getResources().getColor(R.color.white));
                    mixture_one.setTextColor(getResources().getColor(R.color.activition_color));
                    mixture_two.setTextColor(getResources().getColor(R.color.activition_color));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thLiveIncreaseAshDisposal.setVisibility(View.GONE);
                    thLiveDecrease.setVisibility(View.VISIBLE);
                    thLiveIncrease.setVisibility(View.VISIBLE);
                    thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDDecrease.setVisibility(View.GONE);
                    thIDIncrease.setVisibility(View.GONE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDDecrease.setVisibility(View.GONE);
                    mixtureIDIncrease.setVisibility(View.GONE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));

                    AttendanceConfigUtils.modityJson();
                    RegisterConfigUtils.modityJson();
                }
            }
        });

        mixture_one.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    mixture_zero.setChecked(false);
                    mixture_one.setChecked(true);
                    mixture_two.setChecked(false);
                    SingleBaseConfig.getBaseConfig().setActiveModel(2);

                    mixture_zero.setTextColor(getResources().getColor(R.color.activition_color));
                    mixture_one.setTextColor(getResources().getColor(R.color.white));
                    mixture_two.setTextColor(getResources().getColor(R.color.activition_color));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveDecrease.setVisibility(View.GONE);
                    thLiveIncrease.setVisibility(View.GONE);
                    thIDDecreaseAshDisposal.setVisibility(View.GONE);
                    thIDIncreaseAshDisposal.setVisibility(View.GONE);
                    thIDDecrease.setVisibility(View.VISIBLE);
                    thIDIncrease.setVisibility(View.VISIBLE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    mixtureIDDecrease.setVisibility(View.GONE);
                    mixtureIDIncrease.setVisibility(View.GONE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));

                    AttendanceConfigUtils.modityJson();
                    RegisterConfigUtils.modityJson();
                }
            }
        });

        mixture_two.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    rgbandnirLlMixture.setVisibility(View.VISIBLE);
                    rgbandnirMixture.setVisibility(View.VISIBLE);

                    mixture_zero.setChecked(false);
                    mixture_one.setChecked(false);
                    mixture_two.setChecked(true);
                    SingleBaseConfig.getBaseConfig().setActiveModel(3);
                    SingleBaseConfig.getBaseConfig().setType(2);

                    mixture_zero.setTextColor(getResources().getColor(R.color.activition_color));
                    mixture_one.setTextColor(getResources().getColor(R.color.activition_color));
                    mixture_two.setTextColor(getResources().getColor(R.color.white));

                    thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
                    thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thLiveDecrease.setVisibility(View.GONE);
                    thLiveIncrease.setVisibility(View.GONE);
                    thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    thIDDecrease.setVisibility(View.GONE);
                    thIDIncrease.setVisibility(View.GONE);
                    mixtureIDDecreaseAshDisposal.setVisibility(View.GONE);
                    mixtureIDIncreaseAshDisposal.setVisibility(View.GONE);
                    mixtureIDDecrease.setVisibility(View.VISIBLE);
                    mixtureIDIncrease.setVisibility(View.VISIBLE);

                    thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
                    mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.white));

                    AttendanceConfigUtils.modityJson();
                    RegisterConfigUtils.modityJson();
                } else {
                    rgbandnirLlMixture.setVisibility(View.GONE);
                    rgbandnirMixture.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (activeModel == one) {
            mixture_zero.setChecked(true);

            mixture_zero.setTextColor(getResources().getColor(R.color.white));
            mixture_one.setTextColor(getResources().getColor(R.color.activition_color));
            mixture_two.setTextColor(getResources().getColor(R.color.activition_color));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.white));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thLiveDecreaseAshDisposal.setVisibility(View.GONE);
            thLiveIncreaseAshDisposal.setVisibility(View.GONE);
            thLiveDecrease.setVisibility(View.VISIBLE);
            thLiveIncrease.setVisibility(View.VISIBLE);
            thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDDecrease.setVisibility(View.GONE);
            thIDIncrease.setVisibility(View.GONE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDDecrease.setVisibility(View.GONE);
            mixtureIDIncrease.setVisibility(View.GONE);


            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
        }
        if (activeModel == two) {
            mixture_one.setChecked(true);

            mixture_zero.setTextColor(getResources().getColor(R.color.activition_color));
            mixture_one.setTextColor(getResources().getColor(R.color.white));
            mixture_two.setTextColor(getResources().getColor(R.color.activition_color));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveDecrease.setVisibility(View.GONE);
            thLiveIncrease.setVisibility(View.GONE);
            thIDDecreaseAshDisposal.setVisibility(View.GONE);
            thIDIncreaseAshDisposal.setVisibility(View.GONE);
            thIDDecrease.setVisibility(View.VISIBLE);
            thIDIncrease.setVisibility(View.VISIBLE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            mixtureIDDecrease.setVisibility(View.GONE);
            mixtureIDIncrease.setVisibility(View.GONE);

            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
        }
        if (activeModel == three) {
            mixture_two.setChecked(true);

            mixture_zero.setTextColor(getResources().getColor(R.color.activition_color));
            mixture_one.setTextColor(getResources().getColor(R.color.activition_color));
            mixture_two.setTextColor(getResources().getColor(R.color.white));

            thLiveTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDTvThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDTvThreshold.setTextColor(getResources().getColor(R.color.white));
            thLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thLiveDecrease.setVisibility(View.GONE);
            thLiveIncrease.setVisibility(View.GONE);
            thIDDecreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDIncreaseAshDisposal.setVisibility(View.VISIBLE);
            thIDDecrease.setVisibility(View.GONE);
            thIDIncrease.setVisibility(View.GONE);
            mixtureIDDecreaseAshDisposal.setVisibility(View.GONE);
            mixtureIDIncreaseAshDisposal.setVisibility(View.GONE);
            mixtureIDDecrease.setVisibility(View.VISIBLE);
            mixtureIDIncrease.setVisibility(View.VISIBLE);

            thLiveEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            thIDEtThreshold.setTextColor(getResources().getColor(R.color.activition_color));
            mixtureIDEtThreshold.setTextColor(getResources().getColor(R.color.white));
        }

        thRgbandnirLiveEtThreshold.setText(lightThreshold + "");
        thLiveEtThreshold.setText(liveInitValue + "");
        thIDEtThreshold.setText(idInitValue + "");
        mixtureIDEtThreshold.setText(rgbAndNirThreshold + "");

        if (mixture_two.isChecked()) {
            rgbandnirLlMixture.setVisibility(View.VISIBLE);
            rgbandnirMixture.setVisibility(View.VISIBLE);
        } else {
            rgbandnirLlMixture.setVisibility(View.GONE);
            rgbandnirMixture.setVisibility(View.GONE);
        }
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

            AttendanceConfigUtils.modityJson();
            RegisterConfigUtils.modityJson();
            finish();
        } else if (id == R.id.th_rgbandnir_LiveDecrease) {
            if (lightThreshold > zero && lightThreshold <= 255) {
                lightThreshold = lightThreshold - 5;
                thRgbandnirLiveEtThreshold.setText(lightThreshold + "");
            }
        } else if (id == R.id.th_rgbandnir_LiveIncrease) {
            if (lightThreshold >= zero && lightThreshold < 255) {
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
                    AttendanceFaceDetectActivity.this, getString(R.string.cw_recognizethrehold),
                    showWidth, showXLocation);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = flsMixtureType.getWidth();
        showXLocation = (int) flRepresent.getLeft();
    }


}