package com.baidu.idl.face.main.activity.start;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.idl.face.main.activity.BaseActivity;
import com.baidu.idl.face.main.activity.FaceSDKManager;
import com.baidu.idl.face.main.attribute.utils.AttributeConfigUtils;
import com.baidu.idl.face.main.drivermonitor.utils.DriverMonitorConfigUtils;
import com.baidu.idl.face.main.finance.utils.FinanceConfigUtils;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.AttendanceConfigUtils;
import com.baidu.idl.main.facesdk.gazelibrary.utils.GazeConfigUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.IdentifyConfigUtils;
import com.baidu.idl.main.facesdk.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.paymentlibrary.utils.PaymentConfigUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.RegisterConfigUtils;
import com.baidu.idl.main.facesdk.utils.GateConfigUtils;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        boolean isConfigExit = GateConfigUtils.isConfigExit();
        boolean isInitConfig = GateConfigUtils.initConfig();
        boolean isPaymentConfigExit = PaymentConfigUtils.isConfigExit();
        boolean isInitPaymentConfig = PaymentConfigUtils.initConfig();
        boolean isAttributeConfigExit = AttributeConfigUtils.isConfigExit();
        boolean isAttributeInitConfig = AttributeConfigUtils.initConfig();
        boolean isAttendanceConfigExit = AttendanceConfigUtils.isConfigExit();
        boolean isAttendanceInitConfig = AttendanceConfigUtils.initConfig();
        boolean isIdentifyConfigExit = IdentifyConfigUtils.isConfigExit();
        boolean isIdentifyInitConfig = IdentifyConfigUtils.initConfig();
        boolean isGazeConfigExit = GazeConfigUtils.isConfigExit();
        boolean isGazeInitConfig = GazeConfigUtils.initConfig();
        boolean isRegisterConfigExit = RegisterConfigUtils.isConfigExit();
        boolean isRegisterInitConfig = RegisterConfigUtils.initConfig();
        boolean isDrivermonitorConfigExit = DriverMonitorConfigUtils.isConfigExit();
        boolean isDrivermonitorInitConfig = DriverMonitorConfigUtils.initConfig();
        boolean isFinanceConfigExit = FinanceConfigUtils.isConfigExit();
        boolean isFinanceInitConfig = FinanceConfigUtils.initConfig();

        if (isInitConfig && isConfigExit && isPaymentConfigExit && isInitPaymentConfig
                && isAttributeInitConfig && isAttributeConfigExit
                && isAttendanceInitConfig && isAttendanceConfigExit
                && isIdentifyInitConfig && isIdentifyConfigExit
                && isGazeInitConfig && isGazeConfigExit
                && isRegisterInitConfig && isRegisterConfigExit
                && isDrivermonitorInitConfig && isDrivermonitorConfigExit
                && isFinanceInitConfig && isFinanceConfigExit) {
            Toast.makeText(StartActivity.this, "初始配置加载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StartActivity.this, "初始配置失败,将重置文件内容为默认配置", Toast.LENGTH_SHORT).show();
            GateConfigUtils.modityJson();
            PaymentConfigUtils.modityJson();
            AttributeConfigUtils.modityJson();
            AttendanceConfigUtils.modityJson();
            IdentifyConfigUtils.modityJson();
            GazeConfigUtils.modityJson();
            RegisterConfigUtils.modityJson();
            DriverMonitorConfigUtils.modityJson();
            FinanceConfigUtils.modityJson();
        }

        initLicense();
    }

    private void initLicense() {
        FaceSDKManager.getInstance().init(mContext, new SdkInitListener() {
            @Override
            public void initStart() {

            }

            public void initLicenseSuccess() {

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        startActivity(new Intent(mContext, HomeActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initLicenseFail(int errorCode, String msg) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        startActivity(new Intent(mContext, ActivitionActivity.class));
                        finish();
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 2000);
            }

            @Override
            public void initModelSuccess() {
            }

            @Override
            public void initModelFail(int errorCode, String msg) {

            }
        });
    }
}
