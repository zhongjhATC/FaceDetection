package com.baidu.aip.asrwakeup3.util;

import android.content.Context;

import com.baidu.aip.asrwakeup3.core.util.bluetooth.AndroidAudioManager;

public class BluetoothUtil {
    private static volatile int mode = -1;

    public static final int FULL_MODE = 1;

    public static final int SIMPLE_MODE = 2;

    public static void start(Context context, int mode) {
        BluetoothUtil.mode = mode;
        switch (mode) {
            case FULL_MODE:
                // 蓝牙 完整代码版本
                AndroidAudioManager.getInstance(context).startBluetooth();
                AndroidAudioManager.getInstance(context).routeAudioToBluetooth();
                break;
            case SIMPLE_MODE:
                // 蓝牙 极简代码版本
                AndroidAudioManager.getInstance(context).startSimpleBluetooth();
                break;
            /**
             case 3:
             // 有线
             AndroidAudioManager.getInstance(context).enableHeadsetReceiver();
             AndroidAudioManager.getInstance(context).routeAudioToSpeakerHelper(false);
             break;
             */
        }
    }

    public static void destory(Context context) {
        switch (mode) {
            case FULL_MODE:
                // 蓝牙 完整代码版本
                AndroidAudioManager.getInstance(context).destroy();
                break;
            case SIMPLE_MODE:
                // 蓝牙 极简代码版本
                AndroidAudioManager.getInstance(context).destorySimpleBluetooth();
                break;
            /**
             case 3:
             AndroidAudioManager.getInstance(context).routeAudioToSpeakerHelper(true);
             break;
             */

        }
        mode = -1;
    }
}
