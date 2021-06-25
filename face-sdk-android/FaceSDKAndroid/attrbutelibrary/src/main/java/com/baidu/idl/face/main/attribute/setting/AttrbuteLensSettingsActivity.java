package com.baidu.idl.face.main.attribute.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.idl.face.main.attribute.activity.AttributeBaseActivity;
import com.baidu.idl.face.main.attribute.model.SingleBaseConfig;
import com.baidu.idl.face.main.attribute.utils.AttributeConfigUtils;
import com.baidu.idl.main.facesdk.attrbutelibrary.R;


public class AttrbuteLensSettingsActivity extends AttributeBaseActivity implements View.OnClickListener {


    private TextView tvSettingFaceDetectAngle;
    private TextView tvSettingDisplayAngle;
    private ImageView qcSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gate_lens_settings);

        init();
    }

    private void init() {
        // 人脸检测角度
        LinearLayout configFaceDetectAngle = findViewById(R.id.configFaceDetectAngle);
        configFaceDetectAngle.setOnClickListener(this);
        tvSettingFaceDetectAngle = findViewById(R.id.tvSettingFaceDetectAngle);
        // 人脸回显角度
        LinearLayout configDisplayAngle = findViewById(R.id.configDisplayAngle);
        configDisplayAngle.setOnClickListener(this);
        tvSettingDisplayAngle = findViewById(R.id.tvSettingDisplayAngle);
        // 镜像设置
        LinearLayout configMirror = findViewById(R.id.configMirror);
        configMirror.setOnClickListener(this);


        qcSave = findViewById(R.id.qc_save);
        qcSave.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvSettingFaceDetectAngle.setText(SingleBaseConfig.getBaseConfig().getDetectDirection() + "");
        tvSettingDisplayAngle.setText(SingleBaseConfig.getBaseConfig().getVideoDirection() + "");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.configFaceDetectAngle) {
            Intent intent = new Intent(this, FaceDetectAngleActivity.class);
            startActivity(intent);
        } else if (id == R.id.configDisplayAngle) {
            Intent intent = new Intent(this, CameraDisplayAngleActivity.class);
            startActivity(intent);
        } else if (id == R.id.configMirror) {
            Intent intent = new Intent(this, AttributeLensSettingActivity.class);
            startActivity(intent);
        } else if (id == R.id.qc_save) {
            AttributeConfigUtils.modityJson();
            finish();
        }
    }
}