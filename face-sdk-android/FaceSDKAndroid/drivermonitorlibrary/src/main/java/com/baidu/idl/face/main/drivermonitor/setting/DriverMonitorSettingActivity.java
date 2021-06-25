package com.baidu.idl.face.main.drivermonitor.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.idl.face.main.drivermonitor.activity.DrivermonitorBaseActivity;
import com.baidu.idl.face.main.drivermonitor.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.drivermonitor.R;
import com.baidu.idl.main.facesdk.license.BDFaceLicenseAuthInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DriverMonitorSettingActivity extends DrivermonitorBaseActivity implements View.OnClickListener {

    private ImageView gateSetttingBack;
    private LinearLayout gateFaceDetection;
    private LinearLayout gateConfigQualtify;
    private LinearLayout gateHuotiDetection;
    private LinearLayout gateLensSettings;
    private TextView tvSettingQualtify;
    private TextView tvSettingLiviness;
    private LinearLayout configVersionMessage;
    private TextView tvSettingEffectiveDate;
    private FaceAuth faceAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivermonitor_setting);
        init();
    }

    private void init() {
        faceAuth = new FaceAuth();
        // 返回
        gateSetttingBack = findViewById(R.id.gate_settting_back);
        gateSetttingBack.setOnClickListener(this);
        // 人脸检测
        gateFaceDetection = findViewById(R.id.gate_face_detection);
        gateFaceDetection.setOnClickListener(this);
        // 质量检测
        gateConfigQualtify = findViewById(R.id.gate_config_qualtify);
        gateConfigQualtify.setOnClickListener(this);
        // 活体检测
        gateHuotiDetection = findViewById(R.id.gate_huoti_detection);
        gateHuotiDetection.setOnClickListener(this);
        // 镜头设置
        gateLensSettings = findViewById(R.id.gate_lens_settings);
        gateLensSettings.setOnClickListener(this);
        // 版本信息
        configVersionMessage = findViewById(R.id.configVersionMessage);
        configVersionMessage.setOnClickListener(this);
        tvSettingQualtify = findViewById(R.id.tvSettingQualtify);
        tvSettingLiviness = findViewById(R.id.tvSettingLiviness);

        tvSettingEffectiveDate = findViewById(R.id.tvSettingEffectiveDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SingleBaseConfig.getBaseConfig().isQualityControl()) {
            tvSettingQualtify.setText("开启");
        } else {
            tvSettingQualtify.setText("关闭");
        }
        if (SingleBaseConfig.getBaseConfig().isLivingControl()) {
            tvSettingLiviness.setText("开启");
        } else {
            tvSettingLiviness.setText("关闭");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        BDFaceLicenseAuthInfo bdFaceLicenseAuthInfo = faceAuth.getAuthInfo(this);
        Date dateLong = new Date(bdFaceLicenseAuthInfo.expireTime * 1000L);
        String dateTime = simpleDateFormat.format(dateLong);

        tvSettingEffectiveDate.setText("有效期至" + dateTime);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.gate_settting_back) {
            finish();
        } else if (id == R.id.gate_face_detection) {
            Intent intent = new Intent(DriverMonitorSettingActivity.this, DrivermonitorMinFaceActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_config_qualtify) {
            Intent intent = new Intent(DriverMonitorSettingActivity.this, DriverMonitorConfigQualtifyActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_huoti_detection) {
            Intent intent = new Intent(DriverMonitorSettingActivity.this, FaceLivinessTypeActivity.class);
            startActivity(intent);
        } else if (id == R.id.gate_lens_settings) {
            Intent intent = new Intent(DriverMonitorSettingActivity.this, DriverMonitorLensSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.configVersionMessage) {
            Intent intent = new Intent(DriverMonitorSettingActivity.this, VersionMessageActivity.class);
            startActivity(intent);
        }
    }
}