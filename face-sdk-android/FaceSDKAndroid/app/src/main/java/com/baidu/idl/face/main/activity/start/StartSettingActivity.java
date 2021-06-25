package com.baidu.idl.face.main.activity.start;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.idl.face.main.activity.BaseActivity;
import com.baidu.idl.facesdkdemo.R;

public class StartSettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_setting);
        initView();
    }

    private void initView() {
        TextView setting_urlTv = findViewById(R.id.setting_urlTv);
        setting_urlTv.setOnClickListener(this);
        ImageView startSettingBack = findViewById(R.id.start_setting_back);
        startSettingBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_urlTv:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://login.bce.baidu.com/");
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.start_setting_back:
                finish();
                break;
        }
    }
}
