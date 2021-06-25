package com.baidu.idl.main.facesdk.gazelibrary.setting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.idl.main.facesdk.gazelibrary.BaseActivity;
import com.baidu.idl.main.facesdk.gazelibrary.R;
import com.baidu.idl.main.facesdk.gazelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.gazelibrary.utils.GazeConfigUtils;
import com.baidu.idl.main.facesdk.gazelibrary.utils.PWTextUtils;


/**
 * 镜像调节页面
 * Created by v_liujialu01 on 2019/6/17.
 */

public class MirrorSettingActivity extends BaseActivity implements View.OnClickListener {
    private Switch mSwitchMirrorRgb;
    private Switch mSwitchMirrorNir;
    private Switch switchDetectFrame;
    private ImageView mButtonMirrorSave;
    private int zero = 0;
    private int one = 1;
    public static final int cancle = 404;

    private LinearLayout linerDetectMirror;
    private TextView tvDetectMirror;
    private Button cwDetectMirror;

    private LinearLayout linerCameraDisplayMirror;
    private TextView tvCameraDisplayMirror;
    private Button cwCameraDisplayMirror;
    private String msgTag = "";

    //    private LinearLayout linerBarMirror;
    private LinearLayout mirrorRepresent;
    private int showWidth;
    private int showXLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mirror_setting);

//        linerBarMirror = findViewById(R.id.linerbarmirror);
//        setBarColor();
//        setLightStatusBarColor(this);
//        setBarLayout(linerBarMirror);

        initView();
        initData();
    }

    private void initView() {
        mirrorRepresent = findViewById(R.id.mirrorRepresent);

        mSwitchMirrorRgb = findViewById(R.id.switch_mirror_rgb);
        mSwitchMirrorNir = findViewById(R.id.switch_mirror_nir);
        switchDetectFrame = findViewById(R.id.switch_detect_frame);
        mButtonMirrorSave = findViewById(R.id.button_mirror_save);

        linerDetectMirror = findViewById(R.id.linerdetectmirror);
        tvDetectMirror = findViewById(R.id.tvdetectmirror);
        cwDetectMirror = findViewById(R.id.cwdetectmirror);

        linerCameraDisplayMirror = findViewById(R.id.linercameradisplaymirror);
        tvCameraDisplayMirror = findViewById(R.id.tvcameradisplaymirror);
        cwCameraDisplayMirror = findViewById(R.id.cwcameradisplaymirror);

        mButtonMirrorSave.setOnClickListener(this);

        PWTextUtils.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @SuppressLint("NewApi")
            @Override
            public void onDismiss() {
                cwDetectMirror.setBackground(getDrawable(R.mipmap.icon_setting_question));
                cwCameraDisplayMirror.setBackground(getDrawable(R.mipmap.icon_setting_question));
            }
        });

        cwDetectMirror.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_detectframe))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_detectframe);
                cwDetectMirror.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerDetectMirror, tvDetectMirror, MirrorSettingActivity.this,
                        getString(R.string.cw_detectframe), showWidth, showXLocation);
            }
        });

        cwCameraDisplayMirror.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (msgTag.equals(getString(R.string.cw_cameradisplay))) {
                    msgTag = "";
                    return;
                }
                msgTag = getString(R.string.cw_cameradisplay);
                cwCameraDisplayMirror.setBackground(getDrawable(R.mipmap.icon_setting_question_hl));
                PWTextUtils.showDescribeText(linerCameraDisplayMirror,
                        tvCameraDisplayMirror, MirrorSettingActivity.this,
                        getString(R.string.cw_cameradisplay), showWidth, showXLocation);
            }
        });

    }

    private void initData() {

        if (SingleBaseConfig.getBaseConfig().getRgbRevert()) {
            switchDetectFrame.setChecked(true);
        } else {
            switchDetectFrame.setChecked(false);
        }

        if (SingleBaseConfig.getBaseConfig().getMirrorRGB() == zero) {  // rgb无镜像
            mSwitchMirrorRgb.setChecked(false);
        } else if (SingleBaseConfig.getBaseConfig().getMirrorRGB() == one) {  // rgb有镜像
            mSwitchMirrorRgb.setChecked(true);
        }

        if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == zero) {  // nir无镜像
            mSwitchMirrorNir.setChecked(false);
        } else if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == one) {  // nir有镜像
            mSwitchMirrorNir.setChecked(true);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_mirror_save) {
            if (switchDetectFrame.isChecked()) {
                SingleBaseConfig.getBaseConfig().setRgbRevert(true);
            } else {
                SingleBaseConfig.getBaseConfig().setRgbRevert(false);
            }

            if (mSwitchMirrorRgb.isChecked()) {
                SingleBaseConfig.getBaseConfig().setMirrorRGB(one);
            } else {
                SingleBaseConfig.getBaseConfig().setMirrorRGB(zero);
            }

            if (mSwitchMirrorNir.isChecked()) {
                SingleBaseConfig.getBaseConfig().setMirrorNIR(one);
            } else {
                SingleBaseConfig.getBaseConfig().setMirrorNIR(zero);
            }

            GazeConfigUtils.modityJson();
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        showWidth = mirrorRepresent.getWidth();
        showXLocation = (int) mirrorRepresent.getX();
    }
}
