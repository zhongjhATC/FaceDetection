package com.baidu.idl.main.facesdk.setting;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.activity.BaseActivity;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.license.BDFaceLicenseAuthInfo;
import com.baidu.idl.main.facesdk.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;


public class VersionMessageActivity extends BaseActivity {
    private TextView sdkVersion;
    private TextView systemVersion;
    private TextView activateStatus;
    private TextView activateType;
    private TextView activateData;
    private ImageView buttonVersionSave;
    private FaceAuth faceAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_versionmessage);

        init();
    }

    public void init() {
        faceAuth = new FaceAuth();
        buttonVersionSave = findViewById(R.id.button_version_save);
        sdkVersion = findViewById(R.id.sdkversion);
        systemVersion = findViewById(R.id.systemversion);
        activateStatus = findViewById(R.id.activatestatus);
        activateType = findViewById(R.id.activatetype);
        activateData = findViewById(R.id.activatedata);

        sdkVersion.setText(Utils.getVersionName(this));
        systemVersion.setText(android.os.Build.VERSION.RELEASE);
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            activateStatus.setText("未激活");
        } else {
            activateStatus.setText("已激活");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        BDFaceLicenseAuthInfo bdFaceLicenseAuthInfo = faceAuth.getAuthInfo(this);
        Date dateLong = new Date(bdFaceLicenseAuthInfo.expireTime * 1000L);
        String dateTime = simpleDateFormat.format(dateLong);

        activateData.setText(dateTime);
        buttonVersionSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
