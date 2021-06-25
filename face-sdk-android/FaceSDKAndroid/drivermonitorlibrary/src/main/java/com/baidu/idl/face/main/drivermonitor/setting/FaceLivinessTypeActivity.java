package com.baidu.idl.face.main.drivermonitor.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;


import com.baidu.idl.face.main.drivermonitor.activity.DrivermonitorBaseActivity;
import com.baidu.idl.face.main.drivermonitor.model.SingleBaseConfig;
import com.baidu.idl.face.main.drivermonitor.utils.DriverMonitorConfigUtils;
import com.baidu.idl.face.main.drivermonitor.utils.PWTextUtils;
import com.baidu.idl.main.facesdk.drivermonitor.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;


/**
 * author : shangrong
 * date : two019/five/two7 six:four8 PM
 * description :活体检测模式
 */
public class FaceLivinessTypeActivity extends DrivermonitorBaseActivity implements View.OnClickListener {

    private int liveTypeValue;

    // 1:rgb活体
    private static final int one = 1;
    // 2:rgb+nir活体
    private static final int two = 2;
    // 3:rgb+depth活体
    private static final int three = 3;
    // 4:rgb+nir+depth活体
    private static final int four = 4;

    private Button cwLivetype;
    private Button cwRgb;
    private Button cwRgbAndNir;
    private Button cwRgbAndDepth;

    private LinearLayout linerLiveTpye;
    private TextView tvLivType;

    private CheckBox flsRgbAndNirAndDepthLive;
    private CheckBox flsRgbLive;
    private CheckBox flsRgbAndNirLive;
    private CheckBox flsRgbAndDepthLive;
    private String msgTag = "";
    private int showWidth;
    private int showXLocation;
    private LinearLayout flRepresent;
    private View rgbView;
    private View rgbAndNirView;
    private View rgbAndDepthView;
    private Switch qcLiving;
    private LinearLayout qc_linerLiving;
    private ImageView qcGestureDecrease;
    private EditText qcGestureEtThreshold;
    private ImageView qcGestureIncrease;
    private int framesThreshold;

    private int ten = 10;
    private int zero = 0;
    // RGB活体阀值
    private ImageView thRgbLiveDecrease;
    private ImageView thRgbLiveIncrease;
    private EditText thRgbLiveEtThreshold;

    // NIR活体阀值
    private ImageView thNirLiveDecrease;
    private ImageView thNirLiveIncrease;
    private EditText thNirLiveEtThreshold;

    // Depth活体阀值
    private ImageView thdepthLiveDecrease;
    private ImageView thdepthLiveIncrease;
    private EditText thDepthLiveEtThreshold;

    private float rgbInitValue;
    private float nirInitValue;
    private float depthInitValue;

    private BigDecimal rgbDecimal;
    private BigDecimal nirDecimal;
    private BigDecimal depthDecimal;
    private BigDecimal nonmoralValue;
    private static final float templeValue = 0.05f;
    private Button cwLiveThrehold;
    private LinearLayout linerLiveThreshold;
    private TextView tvLive;
    private Button gateChangeLensBtn;
    private Button gateChangeLensBtnTwo;
    private int cameraType;
    private TextView gateChangeLensTv;
    private TextView gateChangeLensTvTwo;

    int isSelectShot = -1;
    int isSelectShotTwo = -1;
    private TextView rgbThresholdTv;
    private TextView nirThresholdTv;
    private TextView depthThresholdTv;
    private ImageView thRgbLiveDecreaseAshDisposal;
    private ImageView thRgbLiveIncreaseAshDisposal;
    private ImageView thNirLiveDecreaseAshDisposal;
    private ImageView thNirLiveIncreaseAshDisposal;
    private ImageView thDepthLiveDecreaseAshDisposal;
    private ImageView thDepthLiveIncreaseAshDisposal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivermonitor_facelivinesstype);
        init();
    }

    public void init() {
        flRepresent = findViewById(R.id.flRepresent);
        rgbView = findViewById(R.id.rgbView);
        rgbAndNirView = findViewById(R.id.rgbAndNirView);
        rgbAndDepthView = findViewById(R.id.rgbAndDepthView);
        linerLiveTpye = findViewById(R.id.linerlivetpye);
        tvLivType = findViewById(R.id.tvlivetype);

        cwLivetype = findViewById(R.id.cw_livetype);
        cwLivetype.setOnClickListener(this);
        cwRgb = findViewById(R.id.cw_rgb);
        cwRgb.setOnClickListener(this);
        cwRgbAndNir = findViewById(R.id.cw_rgbandnir);
        cwRgbAndNir.setOnClickListener(this);
        cwRgbAndDepth = findViewById(R.id.cw_rgbanddepth);
        cwRgbAndDepth.setOnClickListener(this);

        flsRgbAndNirAndDepthLive = findViewById(R.id.fls_rgbandniranddepth_live);
        flsRgbLive = findViewById(R.id.fls_rgb_live);
        flsRgbAndNirLive = findViewById(R.id.fls_rgbandnir_live);
        flsRgbAndDepthLive = findViewById(R.id.fls_rgbanddepth_live);

        flsRgbLive.setEnabled(false);
        flsRgbAndDepthLive.setEnabled(false);
        flsRgbAndNirAndDepthLive.setEnabled(false);

        // 返回
        ImageView flsSave = findViewById(R.id.fls_save);
        flsSave.setOnClickListener(this);
        // 活体检测开关
        qcLiving = findViewById(R.id.qc_Living);
        qc_linerLiving = findViewById(R.id.qc_LinerLiving);

        // 帧数阈值
        qcGestureDecrease = findViewById(R.id.qc_GestureDecrease);
        qcGestureDecrease.setOnClickListener(this);
        qcGestureEtThreshold = findViewById(R.id.qc_GestureEtThreshold);
        qcGestureIncrease = findViewById(R.id.qc_GestureIncrease);
        qcGestureIncrease.setOnClickListener(this);
        framesThreshold = SingleBaseConfig.getBaseConfig().getFramesThreshold();
        // rgb活体
        thRgbLiveDecrease = findViewById(R.id.th_RgbLiveDecrease);
        thRgbLiveDecrease.setOnClickListener(this);
        thRgbLiveIncrease = findViewById(R.id.th_RgbLiveIncrease);
        thRgbLiveIncrease.setOnClickListener(this);
        thRgbLiveEtThreshold = findViewById(R.id.th_RgbLiveEtThreshold);
        // nir活体
        thNirLiveDecrease = findViewById(R.id.th_NirLiveDecrease);
        thNirLiveDecrease.setOnClickListener(this);
        thNirLiveIncrease = findViewById(R.id.th_NirLiveIncrease);
        thNirLiveIncrease.setOnClickListener(this);
        thNirLiveEtThreshold = findViewById(R.id.th_NirLiveEtThreshold);
        // depth活体
        thdepthLiveDecrease = findViewById(R.id.th_depthLiveDecrease);
        thdepthLiveDecrease.setOnClickListener(this);
        thdepthLiveIncrease = findViewById(R.id.th_depthLiveIncrease);
        thdepthLiveIncrease.setOnClickListener(this);
        thDepthLiveEtThreshold = findViewById(R.id.th_depthLiveEtThreshold);

        rgbInitValue = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        nirInitValue = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        depthInitValue = SingleBaseConfig.getBaseConfig().getDepthLiveScore();

        cwLiveThrehold = findViewById(R.id.cw_livethrehold);
        cwLiveThrehold.setOnClickListener(this);
        linerLiveThreshold = findViewById(R.id.linerlivethreshold);
        tvLive = findViewById(R.id.tvlive);

        nonmoralValue = new BigDecimal(templeValue + "");

        rgbThresholdTv = findViewById(R.id.rgb_thresholdTv);
        nirThresholdTv = findViewById(R.id.nir_thresholdTv);
        depthThresholdTv = findViewById(R.id.depth_thresholdTv);
        thRgbLiveDecreaseAshDisposal = findViewById(R.id.th_RgbLiveDecrease_Ash_disposal);
        thRgbLiveIncreaseAshDisposal = findViewById(R.id.th_RgbLiveIncrease_Ash_disposal);
        thNirLiveDecreaseAshDisposal = findViewById(R.id.th_NirLiveDecrease_Ash_disposal);
        thNirLiveIncreaseAshDisposal = findViewById(R.id.th_NirLiveIncrease_Ash_disposal);
        thDepthLiveDecreaseAshDisposal = findViewById(R.id.th_depthLiveDecrease_Ash_disposal);
        thDepthLiveIncreaseAshDisposal = findViewById(R.id.th_depthLiveIncrease_Ash_disposal);

        // 更换镜头按钮
        gateChangeLensBtn = findViewById(R.id.gate_change_lens_btn);
        gateChangeLensBtn.setOnClickListener(this);

        gateChangeLensBtnTwo = findViewById(R.id.gate_change_lens_btn_two);
        gateChangeLensBtnTwo.setOnClickListener(this);


        gateChangeLensTv = findViewById(R.id.gate_change_lens_tv);
        gateChangeLensTvTwo = findViewById(R.id.gate_change_lens_tv_two);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwLivetype.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwRgb.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwRgbAndNir.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwRgbAndDepth.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwLiveThrehold.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        qcLiving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    liveTypeValue = two;
                    if (liveTypeValue == 2) {
                        flsRgbAndNirLive.setChecked(true);
                    }
                    qcLiving.setChecked(true);
                    qc_linerLiving.setVisibility(View.VISIBLE);
                } else {
                    qcLiving.setChecked(false);
                    qc_linerLiving.setVisibility(View.INVISIBLE);
                    liveTypeValue = zero;
                    justify();
                }
            }
        });

        flsRgbLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    flsRgbLive.setChecked(true);
                    flsRgbAndNirLive.setChecked(false);
                    flsRgbAndDepthLive.setChecked(false);
                    flsRgbAndNirAndDepthLive.setChecked(false);
                    gateChangeLensBtn.setVisibility(View.GONE);
                    gateChangeLensBtnTwo.setVisibility(View.GONE);
                    liveTypeValue = one;
                    // nir 置灰
                    nirThresholdTv.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveDecrease.setVisibility(View.GONE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveIncrease.setVisibility(View.GONE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    // depth 置灰
                    depthThresholdTv.setTextColor(getResources().getColor(R.color.hui_color));
                    thdepthLiveDecrease.setVisibility(View.GONE);
                    thDepthLiveEtThreshold.setTextColor(getResources().getColor(R.color.hui_color));
                    thdepthLiveIncrease.setVisibility(View.GONE);
                    thDepthLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thDepthLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    justify();
                }
            }
        });
        flsRgbAndNirLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    flsRgbAndNirLive.setEnabled(false);
                    flsRgbAndNirLive.setChecked(true);
                    flsRgbLive.setChecked(false);
                    flsRgbAndDepthLive.setChecked(false);
                    flsRgbAndNirAndDepthLive.setChecked(false);
                    gateChangeLensBtn.setVisibility(View.GONE);
                    gateChangeLensBtnTwo.setVisibility(View.GONE);
                    liveTypeValue = two;

                    nirThresholdTv.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveDecrease.setVisibility(View.VISIBLE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveIncrease.setVisibility(View.VISIBLE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.GONE);

                    // depth 置灰
                    depthThresholdTv.setTextColor(getResources().getColor(R.color.hui_color));
                    thdepthLiveDecrease.setVisibility(View.GONE);
                    thDepthLiveEtThreshold.setTextColor(getResources().getColor(R.color.hui_color));
                    thdepthLiveIncrease.setVisibility(View.GONE);
                    thDepthLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thDepthLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);
                    justify();
                } else {
                    flsRgbAndNirLive.setEnabled(true);
                }
            }
        });
        flsRgbAndDepthLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isSelectShot = 0;
                    isSelectShotTwo = -1;
                    flsRgbAndDepthLive.setChecked(true);
                    flsRgbLive.setChecked(false);
                    flsRgbAndNirLive.setChecked(false);
                    flsRgbAndNirAndDepthLive.setChecked(false);
                    gateChangeLensBtn.setVisibility(View.VISIBLE);
                    gateChangeLensBtnTwo.setVisibility(View.GONE);
                    liveTypeValue = three;
                    if (isSelectShot != -1) {
                        if (cameraType == zero) {
                            gateChangeLensTv.setText("奥比中光海燕、大白（640*400）");
                        } else if (cameraType == one) {
                            gateChangeLensTv.setText("奥比中光海燕Pro、Atlas（400*640）");
                        } else if (cameraType == two) {
                            gateChangeLensTv.setText("奥比中光蝴蝶、Astra Pro\\Pro S（640*480）");
                        }
                    } else {
                        gateChangeLensTv.setText("此模态下需设定镜头型号");
                    }

                    // nir 置灰
                    nirThresholdTv.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveDecrease.setVisibility(View.GONE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.hui_color));
                    thNirLiveIncrease.setVisibility(View.GONE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.VISIBLE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.VISIBLE);

                    depthThresholdTv.setTextColor(getResources().getColor(R.color.white));
                    thdepthLiveDecrease.setVisibility(View.VISIBLE);
                    thDepthLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thdepthLiveIncrease.setVisibility(View.VISIBLE);
                    thDepthLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thDepthLiveIncreaseAshDisposal.setVisibility(View.GONE);
                    justify();
                } else {
                    gateChangeLensTv.setText("此模态下需设定镜头型号");
                }
            }
        });
        flsRgbAndNirAndDepthLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isSelectShot = -1;
                    isSelectShotTwo = 0;
                    flsRgbAndNirAndDepthLive.setChecked(true);
                    flsRgbLive.setChecked(false);
                    flsRgbAndNirLive.setChecked(false);
                    flsRgbAndDepthLive.setChecked(false);
                    gateChangeLensBtn.setVisibility(View.GONE);
                    gateChangeLensBtnTwo.setVisibility(View.VISIBLE);
                    liveTypeValue = four;
                    if (isSelectShotTwo != -1) {
                        if (cameraType == zero) {
                            gateChangeLensTvTwo.setText("奥比中光海燕、大白（640*400）");
                        } else if (cameraType == one) {
                            gateChangeLensTvTwo.setText("奥比中光海燕Pro、Atlas（400*640）");
                        } else if (cameraType == two) {
                            gateChangeLensTvTwo.setText("奥比中光蝴蝶、Astra Pro\\Pro S（640*480）");
                        }
                    } else {
                        gateChangeLensTvTwo.setText("此模态下需设定镜头型号");
                    }

                    nirThresholdTv.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveDecrease.setVisibility(View.VISIBLE);
                    thNirLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thNirLiveIncrease.setVisibility(View.VISIBLE);
                    thNirLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thNirLiveIncreaseAshDisposal.setVisibility(View.GONE);

                    depthThresholdTv.setTextColor(getResources().getColor(R.color.white));
                    thdepthLiveDecrease.setVisibility(View.VISIBLE);
                    thDepthLiveEtThreshold.setTextColor(getResources().getColor(R.color.white));
                    thdepthLiveIncrease.setVisibility(View.VISIBLE);
                    thDepthLiveDecreaseAshDisposal.setVisibility(View.GONE);
                    thDepthLiveIncreaseAshDisposal.setVisibility(View.GONE);
                    justify();
                } else {
                    gateChangeLensTvTwo.setText("此模态下需设定镜头型号");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 镜头类型
        cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
        // 活体类型
        liveTypeValue = SingleBaseConfig.getBaseConfig().getType();

        if (isSelectShotTwo != -1) {
            if (cameraType == zero) {
                gateChangeLensTvTwo.setText("奥比中光海燕、大白（640*400）");
            } else if (cameraType == one) {
                gateChangeLensTvTwo.setText("奥比中光海燕Pro、Atlas（400*640）");
            } else if (cameraType == two) {
                gateChangeLensTvTwo.setText("奥比中光蝴蝶、Astra Pro\\Pro S（640*480）");
            }
        } else {
            gateChangeLensTvTwo.setText("此模态下需设定镜头型号");
        }

        if (isSelectShot != -1) {
            if (cameraType == zero) {
                gateChangeLensTv.setText("奥比中光海燕、大白（640*400）");
            } else if (cameraType == one) {
                gateChangeLensTv.setText("奥比中光海燕Pro、Atlas（400*640）");
            } else if (cameraType == two) {
                gateChangeLensTv.setText("奥比中光蝴蝶、Astra Pro\\Pro S（640*480）");
            }
        } else {
            gateChangeLensTv.setText("此模态下需设定镜头型号");
        }

        if (SingleBaseConfig.getBaseConfig().isLivingControl()) {
            qcLiving.setChecked(true);
            qc_linerLiving.setVisibility(View.VISIBLE);
        } else {
            qcLiving.setChecked(false);
            qc_linerLiving.setVisibility(View.INVISIBLE);
        }

        qcGestureEtThreshold.setText(framesThreshold + "");
        thRgbLiveEtThreshold.setText(roundByScale(rgbInitValue));
        thNirLiveEtThreshold.setText(roundByScale(nirInitValue));
        thDepthLiveEtThreshold.setText(roundByScale(depthInitValue));


        if (liveTypeValue == one) {
            flsRgbLive.setChecked(true);
            flsRgbAndNirLive.setChecked(false);
            flsRgbAndDepthLive.setChecked(false);
            flsRgbAndNirAndDepthLive.setChecked(false);
        }
        if (liveTypeValue == two) {
            flsRgbAndNirLive.setChecked(true);
            flsRgbLive.setChecked(false);
            flsRgbAndDepthLive.setChecked(false);
            flsRgbAndNirAndDepthLive.setChecked(false);
        }
        if (liveTypeValue == three) {
            flsRgbAndDepthLive.setChecked(true);
            flsRgbLive.setChecked(false);
            flsRgbAndNirLive.setChecked(false);
            flsRgbAndNirAndDepthLive.setChecked(false);
        }
        if (liveTypeValue == four) {
            flsRgbAndNirAndDepthLive.setChecked(true);
            flsRgbLive.setChecked(false);
            flsRgbAndNirLive.setChecked(false);
            flsRgbAndDepthLive.setChecked(false);
        }
        if (liveTypeValue == zero) {
            SingleBaseConfig.getBaseConfig().setType(0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = flRepresent.getWidth();
        showXLocation = (int) flRepresent.getLeft();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.fls_save) {
            if (qcLiving.isChecked()) {
                SingleBaseConfig.getBaseConfig().setLivingControl(true);
            } else {
                SingleBaseConfig.getBaseConfig().setLivingControl(false);
                SingleBaseConfig.getBaseConfig().setType(zero);
            }
            SingleBaseConfig.getBaseConfig().setFramesThreshold(
                    Integer.valueOf(qcGestureEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setRgbLiveScore(
                    Float.valueOf(thRgbLiveEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setNirLiveScore(
                    Float.valueOf(thNirLiveEtThreshold.getText().toString()));
            SingleBaseConfig.getBaseConfig().setDepthLiveScore(
                    Float.valueOf(thDepthLiveEtThreshold.getText().toString()));


            justify();
            DriverMonitorConfigUtils.modityJson();
            finish();
        } else if (id == R.id.cw_livetype) {
            if (msgTag.equals(getString(R.string.cw_livedetecttype))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_livedetecttype);
            cwLivetype.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(linerLiveTpye, tvLivType, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_livedetecttype)
                    , showWidth, showXLocation);
        } else if (id == R.id.cw_rgb) {
            if (msgTag.equals(getString(R.string.cw_rgblive))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_rgblive);
            cwRgb.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(rgbView, rgbView, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_rgblive)
                    , showWidth, 0);
        } else if (id == R.id.cw_rgbandnir) {
            if (msgTag.equals(getString(R.string.cw_rgbandnir))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_rgbandnir);
            cwRgbAndNir.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(rgbAndNirView, rgbAndNirView,
                    FaceLivinessTypeActivity.this, getString(R.string.cw_rgbandnir)
                    , showWidth, 0);
        } else if (id == R.id.cw_rgbanddepth) {
            if (msgTag.equals(getString(R.string.cw_rgbanddepth))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_rgbanddepth);
            cwRgbAndDepth.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(rgbAndDepthView, rgbAndDepthView,
                    FaceLivinessTypeActivity.this, getString(R.string.cw_rgbanddepth)
                    , showWidth, 0);
            // 减
        } else if (id == R.id.qc_GestureDecrease) {
            if (framesThreshold > one && framesThreshold <= ten) {
                framesThreshold = framesThreshold - 1;
                qcGestureEtThreshold.setText(framesThreshold + "");
            }
            // 加
        } else if (id == R.id.qc_GestureIncrease) {
            if (framesThreshold >= one && framesThreshold < ten) {
                framesThreshold = framesThreshold + 1;
                qcGestureEtThreshold.setText(framesThreshold + "");
            }
        } else if (id == R.id.th_RgbLiveDecrease) {
            if (rgbInitValue > zero && rgbInitValue <= one) {
                rgbDecimal = new BigDecimal(rgbInitValue + "");
                rgbInitValue = rgbDecimal.subtract(nonmoralValue).floatValue();
                thRgbLiveEtThreshold.setText(roundByScale(rgbInitValue));
            }
        } else if (id == R.id.th_RgbLiveIncrease) {
            if (rgbInitValue >= zero && rgbInitValue < one) {
                rgbDecimal = new BigDecimal(rgbInitValue + "");
                rgbInitValue = rgbDecimal.add(nonmoralValue).floatValue();
                thRgbLiveEtThreshold.setText(roundByScale(rgbInitValue));
            }
        } else if (id == R.id.th_NirLiveDecrease) {
            if (nirInitValue > zero && nirInitValue <= one) {
                nirDecimal = new BigDecimal(nirInitValue + "");
                nirInitValue = nirDecimal.subtract(nonmoralValue).floatValue();
                thNirLiveEtThreshold.setText(roundByScale(nirInitValue));
            }
        } else if (id == R.id.th_NirLiveIncrease) {
            if (nirInitValue >= zero && nirInitValue < one) {
                nirDecimal = new BigDecimal(nirInitValue + "");
                nirInitValue = nirDecimal.add(nonmoralValue).floatValue();
                thNirLiveEtThreshold.setText(roundByScale(nirInitValue));
            }
        } else if (id == R.id.th_depthLiveDecrease) {
            if (depthInitValue > zero && depthInitValue <= one) {
                depthDecimal = new BigDecimal(depthInitValue + "");
                depthInitValue = depthDecimal.subtract(nonmoralValue).floatValue();
                thDepthLiveEtThreshold.setText(roundByScale(depthInitValue));
            }
        } else if (id == R.id.th_depthLiveIncrease) {
            if (depthInitValue >= zero && depthInitValue < one) {
                depthDecimal = new BigDecimal(depthInitValue + "");
                depthInitValue = depthDecimal.add(nonmoralValue).floatValue();
                thDepthLiveEtThreshold.setText(roundByScale(depthInitValue));
            }
        } else if (id == R.id.cw_livethrehold) {
            if (msgTag.equals(getString(R.string.cw_livethrehold))) {
                msgTag = "";
                return;
            }
            msgTag = getString(R.string.cw_livethrehold);
            cwLiveThrehold.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
            PWTextUtils.showDescribeText(linerLiveThreshold, tvLive, FaceLivinessTypeActivity.this,
                    getString(R.string.cw_livethrehold), showWidth, showXLocation);
        } else if (id == R.id.gate_change_lens_btn) {
            startActivity(new Intent(FaceLivinessTypeActivity.this, DriverMonitorLensSelectionActivity.class));
        } else if (id == R.id.gate_change_lens_btn_two) {
            startActivity(new Intent(FaceLivinessTypeActivity.this, DriverMonitorLensSelectionActivity.class));
        }
    }

    public void justify() {
        if (liveTypeValue == one) {
            SingleBaseConfig.getBaseConfig().setType(one);
            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);


        }
        if (liveTypeValue == two) {
            SingleBaseConfig.getBaseConfig().setType(two);
            SingleBaseConfig.getBaseConfig().setRgbAndNirWidth(640);
            SingleBaseConfig.getBaseConfig().setRgbAndNirHeight(480);
        }
        if (liveTypeValue == three) {
            SingleBaseConfig.getBaseConfig().setType(three);
        }
        if (liveTypeValue == four) {
            SingleBaseConfig.getBaseConfig().setType(four);
        }

        if (liveTypeValue == zero) {
            SingleBaseConfig.getBaseConfig().setType(0);
        }
    }

    public static String roundByScale(float numberValue) {
        // 构造方法的字符格式这里如果小数不足2位,会以0补足.
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        // format 返回的是字符串
        String resultNumber = decimalFormat.format(numberValue);
        return resultNumber;
    }
}
