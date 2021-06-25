package com.baidu.idl.main.facesdk.paymentlibrary.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;


import com.baidu.idl.main.facesdk.paymentlibrary.R;
import com.baidu.idl.main.facesdk.paymentlibrary.activity.BaseActivity;
import com.baidu.idl.main.facesdk.paymentlibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.PaymentConfigUtils;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.RegisterConfigUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class PaymentConfigQualtifyActivity extends BaseActivity {
    private Switch qcQuality;

    private ImageView qcGestureDecrease;
    private EditText qcGestureEtThreshold;
    private ImageView qcGestureIncrease;

    private ImageView qcIlluminiationDecrease;
    private EditText qcIlluminiationEtThreshold;
    private ImageView qcIlluminiationIncrease;

    private ImageView qcBlurDecrease;
    private EditText qcBlurEtThreshold;
    private ImageView qcBlurIncrease;

    private ImageView qcEyeDecrease;
    private EditText qcEyeEtThreshold;
    private ImageView qcEyeIncrease;

    private ImageView qcCheekDecrease;
    private EditText qcCheekEtThreshold;
    private ImageView qcCheekIncrease;

    private ImageView qcNoseDecrease;
    private EditText qcNoseEtThreshold;
    private ImageView qcNoseIncrease;

    private ImageView qcMouseDecrease;
    private EditText qcMouseEtThreshold;
    private ImageView qcMouseIncrease;

    private ImageView qcChinDecrease;
    private EditText qcChinEtThreshold;
    private ImageView qcChinIncrease;

    private LinearLayout qcLinerQuality;

    private float gestureValue;
    private int illuminiationValue;
    private float blurValue;
    private float occlusionEye;
    private float occlusionCheek;
    private float occlusionNose;
    private float occlusionMouth;
    private float occulusionChin;


    private BigDecimal gestureDecimal;
    private BigDecimal blurDecimal;
    private BigDecimal occlusionLeftEyeDecimal;
    private BigDecimal occlusionLeftCheekDecimal;
    private BigDecimal occlusionNoseDecimal;
    private BigDecimal occulusionChinDecimal;

    private BigDecimal obNonmoralValue;
    private BigDecimal gestureNormalValue;

    private ImageView qcSave;

    private LinearLayout linerGesture;
    private TextView tvGesture;
    private Button cwGesture;

    private LinearLayout linerIlluminiation;
    private TextView tvIlluminiation;
    private Button cwIlluminiation;

    private LinearLayout linerBlur;
    private TextView tvBlur;
    private Button cwBlur;

    private LinearLayout linerocclusion;
    private TextView tvocclusion;
    private Button cwocclusion;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    private LinearLayout qcLinerFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_config_qualtify);

        init();
    }

    public void init() {
        linerGesture = findViewById(R.id.linergesture);
        qcLinerFirst = findViewById(R.id.qc_LinerFirst);

        qcSave = findViewById(R.id.qc_save);

        tvGesture = findViewById(R.id.tvgesture);
        cwGesture = findViewById(R.id.cwgesture);

        linerIlluminiation = findViewById(R.id.linerilluminiation);
        tvIlluminiation = findViewById(R.id.tvilluminiation);
        cwIlluminiation = findViewById(R.id.cwilluminiation);

        linerBlur = findViewById(R.id.linerblur);
        tvBlur = findViewById(R.id.tvblur);
        cwBlur = findViewById(R.id.cwblur);

        linerocclusion = findViewById(R.id.linerocclusion);
        tvocclusion = findViewById(R.id.tvocclusion);
        cwocclusion = findViewById(R.id.cwocclusion);

        qcQuality = findViewById(R.id.qc_Quality);
        qcLinerQuality = findViewById(R.id.qc_LinerQuality);

        qcGestureDecrease = findViewById(R.id.qc_GestureDecrease);
        qcGestureEtThreshold = findViewById(R.id.qc_GestureEtThreshold);
        qcGestureIncrease = findViewById(R.id.qc_GestureIncrease);

        qcIlluminiationDecrease = findViewById(R.id.qc_IlluminiationDecrease);
        qcIlluminiationEtThreshold = findViewById(R.id.qc_IlluminiationEtThreshold);
        qcIlluminiationIncrease = findViewById(R.id.qc_IlluminiationIncrease);

        qcBlurDecrease = findViewById(R.id.qc_BlurDecrease);
        qcBlurEtThreshold = findViewById(R.id.qc_BlurEtThreshold);
        qcBlurIncrease = findViewById(R.id.qc_BlurIncrease);

        qcEyeDecrease = findViewById(R.id.qc_EyeDecrease);
        qcEyeEtThreshold = findViewById(R.id.qc_EyeEtThreshold);
        qcEyeIncrease = findViewById(R.id.qc_EyeIncrease);

        qcCheekDecrease = findViewById(R.id.qc_CheekDecrease);
        qcCheekEtThreshold = findViewById(R.id.qc_CheekEtThreshold);
        qcCheekIncrease = findViewById(R.id.qc_CheekIncrease);

        qcNoseDecrease = findViewById(R.id.qc_NoseDecrease);
        qcNoseEtThreshold = findViewById(R.id.qc_NoseEtThreshold);
        qcNoseIncrease = findViewById(R.id.qc_NoseIncrease);

        qcMouseDecrease = findViewById(R.id.qc_MouseDecrease);
        qcMouseEtThreshold = findViewById(R.id.qc_MouseEtThreshold);
        qcMouseIncrease = findViewById(R.id.qc_MouseIncrease);

        qcChinDecrease = findViewById(R.id.qc_ChinDecrease);
        qcChinEtThreshold = findViewById(R.id.qc_ChinEtThreshold);
        qcChinIncrease = findViewById(R.id.qc_ChinIncrease);

        gestureValue = SingleBaseConfig.getBaseConfig().getGesture();
        illuminiationValue = SingleBaseConfig.getBaseConfig().getIllumination();
        blurValue = SingleBaseConfig.getBaseConfig().getBlur();
        occlusionEye = SingleBaseConfig.getBaseConfig().getLeftEye();
        occlusionCheek = SingleBaseConfig.getBaseConfig().getLeftCheek();
        occlusionNose = SingleBaseConfig.getBaseConfig().getNose();
        occlusionMouth = SingleBaseConfig.getBaseConfig().getMouth();
        occulusionChin = SingleBaseConfig.getBaseConfig().getChinContour();

        qcGestureEtThreshold.setText((int) gestureValue + "");
        qcIlluminiationEtThreshold.setText(+illuminiationValue + "");
        qcBlurEtThreshold.setText(blurValue + "");
        qcEyeEtThreshold.setText(occlusionEye + "");
        qcCheekEtThreshold.setText(occlusionCheek + "");
        qcNoseEtThreshold.setText(occlusionNose + "");
        qcMouseEtThreshold.setText(occlusionMouth + "");
        qcChinEtThreshold.setText(occulusionChin + "");

        obNonmoralValue = new BigDecimal(0.1 + "");
        gestureNormalValue = new BigDecimal(5 + "");

        if (SingleBaseConfig.getBaseConfig().isQualityControl()) {
            qcQuality.setChecked(true);
            qcLinerQuality.setVisibility(View.VISIBLE);
        } else {
            qcQuality.setChecked(false);
            qcLinerQuality.setVisibility(View.GONE);
        }

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwGesture.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwBlur.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwIlluminiation.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwocclusion.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        qcQuality.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    qcQuality.setChecked(true);
                    qcLinerQuality.setVisibility(View.VISIBLE);
                } else {
                    qcQuality.setChecked(false);
                    qcLinerQuality.setVisibility(View.GONE);
                }
            }
        });

        setClickListener();
        initEdittextStatus();
    }

    private void initEdittextStatus() {

        qcGestureEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.valueOf(s.toString()) == 90) {
                    qcGestureIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Integer.valueOf(s.toString()) == 0) {
                    qcGestureDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Integer.valueOf(s.toString()) > 0 && Integer.valueOf(s.toString()) < 90) {
                    qcGestureIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcGestureDecrease.setImageResource(R.mipmap.icon_setting_minus);
                }
            }
        });

        if (Integer.valueOf(qcGestureEtThreshold.getText().toString()) == 90) {
            qcGestureIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
        }
        if (Integer.valueOf(qcGestureEtThreshold.getText().toString()) == 0) {
            qcGestureDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }

        qcIlluminiationEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Integer.valueOf(s.toString()) == 255) {
                    qcIlluminiationIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Integer.valueOf(s.toString()) == 0) {
                    qcIlluminiationDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Integer.valueOf(s.toString()) > 0 && Integer.valueOf(s.toString()) < 255) {
                    qcIlluminiationIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcIlluminiationDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Integer.valueOf(qcIlluminiationEtThreshold.getText().toString()) == 255) {
            qcIlluminiationIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Integer.valueOf(qcIlluminiationEtThreshold.getText().toString()) == 0) {
            qcIlluminiationDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }


        qcBlurEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcBlurIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcBlurDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcBlurIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcBlurDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcBlurEtThreshold.getText().toString()) == 1f) {
            qcBlurIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcBlurEtThreshold.getText().toString()) == 0f) {
            qcBlurDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }


        qcEyeEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcEyeIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcEyeDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcEyeIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcEyeDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcEyeEtThreshold.getText().toString()) == 1f) {
            qcEyeIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcEyeEtThreshold.getText().toString()) == 0f) {
            qcEyeDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }


        qcCheekEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcCheekIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcCheekDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcCheekIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcCheekDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcCheekEtThreshold.getText().toString()) == 1f) {
            qcCheekIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcCheekEtThreshold.getText().toString()) == 0f) {
            qcCheekDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }

        qcNoseEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcNoseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcNoseDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcNoseIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcNoseDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcNoseEtThreshold.getText().toString()) == 1f) {
            qcNoseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcNoseEtThreshold.getText().toString()) == 0f) {
            qcNoseDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }

        qcMouseEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcMouseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcMouseDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcMouseIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcMouseDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcMouseEtThreshold.getText().toString()) == 1f) {
            qcMouseIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcMouseEtThreshold.getText().toString()) == 0f) {
            qcMouseDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }

        qcChinEtThreshold.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Float.valueOf(s.toString()) == 1f) {
                    qcChinIncrease.setImageResource(R.mipmap.adding_sign_to_gray);
                }
                if (Float.valueOf(s.toString()) == 0f) {
                    qcChinDecrease.setImageResource(R.mipmap.gray_minus_sign);
                }
                if (Float.valueOf(s.toString()) > 0f && Float.valueOf(s.toString()) < 1f) {
                    qcChinIncrease.setImageResource(R.mipmap.icon_setting_add);
                    qcChinDecrease.setImageResource(R.mipmap.icon_setting_minus );
                }
            }
        });

        if (Float.valueOf(qcChinEtThreshold.getText().toString()) == 1f) {
            qcChinIncrease.setImageResource(R.mipmap.adding_sign_to_gray);

        }
        if (Float.valueOf(qcChinEtThreshold.getText().toString()) == 0f) {
            qcChinDecrease.setImageResource(R.mipmap.gray_minus_sign);
        }
    }

    public void setClickListener() {

        qcGestureDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gestureValue <= 90 && gestureValue > 0) {
                    gestureDecimal = new BigDecimal(gestureValue + "");
                    gestureValue = gestureDecimal.subtract(gestureNormalValue).floatValue();
                    qcGestureEtThreshold.setText((int) gestureValue + "");
                }
            }
        });

        qcGestureIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gestureValue < 90 && gestureValue >= 0) {
                    gestureDecimal = new BigDecimal(gestureValue + "");
                    gestureValue = gestureDecimal.add(gestureNormalValue).floatValue();
                    qcGestureEtThreshold.setText((int) gestureValue + "");
                }
            }
        });

        qcIlluminiationDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (illuminiationValue > 0 && illuminiationValue <= 255) {
                    if (illuminiationValue > 10) {
                        illuminiationValue = illuminiationValue - 10;
                        qcIlluminiationEtThreshold.setText(illuminiationValue + "");
                    } else {
                        illuminiationValue = 0;
                        qcIlluminiationEtThreshold.setText(illuminiationValue + "");
                    }
                }
            }
        });

        qcIlluminiationIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (illuminiationValue >= 0 && illuminiationValue < 255) {
                    if (illuminiationValue == 250) {
                        illuminiationValue = illuminiationValue + 5;
                        qcIlluminiationEtThreshold.setText(illuminiationValue + "");
                    } else {
                        illuminiationValue = illuminiationValue + 10;
                        qcIlluminiationEtThreshold.setText(illuminiationValue + "");
                    }
                }
            }
        });

        qcBlurDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blurValue > 0f && blurValue <= 1f) {
                    blurDecimal = new BigDecimal(blurValue + "");
                    blurValue = blurDecimal.subtract(obNonmoralValue).floatValue();
                    qcBlurEtThreshold.setText(blurValue + "");
                }
            }
        });

        qcBlurIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (blurValue >= 0f && blurValue < 1f) {
                    blurDecimal = new BigDecimal(blurValue + "");
                    blurValue = blurDecimal.add(obNonmoralValue).floatValue();
                    qcBlurEtThreshold.setText(blurValue + "");
                }
            }
        });

        qcEyeDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionEye > 0f && occlusionEye <= 1f) {
                    occlusionLeftEyeDecimal = new BigDecimal(occlusionEye + "");
                    occlusionEye = occlusionLeftEyeDecimal.subtract(obNonmoralValue).floatValue();
                    qcEyeEtThreshold.setText(occlusionEye + "");
                }
            }
        });

        qcEyeIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionEye >= 0f && occlusionEye < 1f) {
                    occlusionLeftEyeDecimal = new BigDecimal(occlusionEye + "");
                    occlusionEye = occlusionLeftEyeDecimal.add(obNonmoralValue).floatValue();
                    qcEyeEtThreshold.setText(occlusionEye + "");
                }
            }
        });

        qcCheekDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionCheek > 0f && occlusionCheek <= 1f) {
                    occlusionLeftCheekDecimal = new BigDecimal(occlusionCheek + "");
                    occlusionCheek = occlusionLeftCheekDecimal.subtract(obNonmoralValue).floatValue();
                    qcCheekEtThreshold.setText(occlusionCheek + "");
                }
            }
        });

        qcCheekIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionCheek >= 0f && occlusionCheek < 1f) {
                    occlusionLeftCheekDecimal = new BigDecimal(occlusionCheek + "");
                    occlusionCheek = occlusionLeftCheekDecimal.add(obNonmoralValue).floatValue();
                    qcCheekEtThreshold.setText(occlusionCheek + "");
                }
            }
        });

        qcNoseDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionNose > 0f && occlusionNose <= 1f) {
                    occlusionNoseDecimal = new BigDecimal(occlusionNose + "");
                    occlusionNose = occlusionNoseDecimal.subtract(obNonmoralValue).floatValue();
                    qcNoseEtThreshold.setText(occlusionNose + "");
                }
            }
        });

        qcNoseIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionNose >= 0f && occlusionNose < 1f) {
                    occlusionNoseDecimal = new BigDecimal(occlusionNose + "");
                    occlusionNose = occlusionNoseDecimal.add(obNonmoralValue).floatValue();
                    qcNoseEtThreshold.setText(occlusionNose + "");
                }
            }
        });

        qcMouseDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionMouth > 0f && occlusionMouth <= 1f) {
                    occlusionNoseDecimal = new BigDecimal(occlusionMouth + "");
                    occlusionMouth = occlusionNoseDecimal.subtract(obNonmoralValue).floatValue();
                    qcMouseEtThreshold.setText(occlusionMouth + "");
                }
            }
        });

        qcMouseIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occlusionMouth >= 0f && occlusionMouth < 1f) {
                    occlusionNoseDecimal = new BigDecimal(occlusionMouth + "");
                    occlusionMouth = occlusionNoseDecimal.add(obNonmoralValue).floatValue();
                    qcMouseEtThreshold.setText(occlusionMouth + "");
                }
            }
        });

        qcChinDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occulusionChin > 0f && occulusionChin <= 1f) {
                    occulusionChinDecimal = new BigDecimal(occulusionChin + "");
                    occulusionChin = occulusionChinDecimal.subtract(obNonmoralValue).floatValue();
                    qcChinEtThreshold.setText(occulusionChin + "");
                }
            }
        });

        qcChinIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (occulusionChin >= 0f && occulusionChin < 1f) {
                    occulusionChinDecimal = new BigDecimal(occulusionChin + "");
                    occulusionChin = occulusionChinDecimal.add(obNonmoralValue).floatValue();
                    qcChinEtThreshold.setText(occulusionChin + "");
                }
            }
        });


        qcSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (qcQuality.isChecked()) {
                    SingleBaseConfig.getBaseConfig().setQualityControl(true);
                } else {
                    SingleBaseConfig.getBaseConfig().setQualityControl(false);
                }

                SingleBaseConfig.getBaseConfig().setGesture(
                        Float.valueOf(qcGestureEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setIllumination(
                        Integer.valueOf(qcIlluminiationEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setBlur(
                        Float.valueOf(qcBlurEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setLeftEye(
                        Float.valueOf(qcEyeEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setRightEye(
                        Float.valueOf(qcEyeEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setLeftCheek(
                        Float.valueOf(qcCheekEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setRightCheek(
                        Float.valueOf(qcCheekEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setNose(
                        Float.valueOf(qcNoseEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setMouth(
                        Float.valueOf(qcMouseEtThreshold.getText().toString()));
                SingleBaseConfig.getBaseConfig().setChinContour(
                        Float.valueOf(qcChinEtThreshold.getText().toString()));

                PaymentConfigUtils.modityJson();
                RegisterConfigUtils.modityJson();
                finish();
            }
        });

        cwGesture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_gesture))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_gesture);
                cwGesture.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerGesture, tvGesture, PaymentConfigQualtifyActivity.this,
                        getString(R.string.cw_gesture), showWidth, showXLocation);
            }
        });
        cwIlluminiation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_illuminiation))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_illuminiation);
                cwIlluminiation.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerIlluminiation, tvIlluminiation, PaymentConfigQualtifyActivity.this,
                        getString(R.string.cw_illuminiation), showWidth, showXLocation);
            }
        });
        cwBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_blur))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_blur);
                cwBlur.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerBlur, tvBlur, PaymentConfigQualtifyActivity.this,
                        getString(R.string.cw_blur), showWidth, showXLocation);
            }
        });
        cwocclusion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_occulusion))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_occulusion);
                cwocclusion.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerocclusion, tvocclusion, PaymentConfigQualtifyActivity.this,
                        getString(R.string.cw_occulusion), showWidth, showXLocation);
            }
        });
    }


    public static String roundByScale(float numberValue) {
        // 构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        // format 返回的是字符串
        String resultNumber = decimalFormat.format(numberValue);
        return resultNumber;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = qcLinerFirst.getWidth();
        showXLocation = (int) linerGesture.getX();
    }
}
