package com.zhongjh.mvvmrapid.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.tbruyelle.rxpermissions2.Permission;

/**
 * Created by zhongjh on 2021/4/16.
 */
public class PermissionsUtil {

    /**
     * 管理权限的3种方式
     * @param permission 权限的返回值
     * @param activity 当前activity
     * @param onPermissions 事件
     */
    public static void managePermissions(Permission permission, Activity activity,OnPermissions onPermissions) {
        if (permission.granted) {
            onPermissions.onGranted();
        } else if (permission.shouldShowRequestPermissionRationale) {
            onPermissions.onShouldShowRequestPermissionRationale();
        } else {
            onPermissions.onToSettingActivity(activity);
        }
    }

    /**
     * 弹出提示框跳转到设置
     */
    private static void showAlertDialogToSetting(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("设置", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivityForResult(intent, 1000);
        });
        builder.setMessage("权限已被设置不再询问，需要自行到设置界面启动权限");
        builder.setTitle("提示");
        builder.show();
    }

    /**
     * 权限事件
     */
    private interface OnGrantedListener {
        /**
         * 所有权限都被授予!
         */
        void onGranted();
    }

    /**
     * 权限事件类
     */
    public static abstract class OnPermissions implements OnGrantedListener{

        /**
         * 至少有一个未经允许就拒绝了
         */
        public void onShouldShowRequestPermissionRationale() {
            ToastUtils.showShort("拒绝权限无法进入");
        }

        /**
         * 至少有一个人用“永不再问”来拒绝允许 需要进入设置
         */
        public void onToSettingActivity(Activity activity) {
            showAlertDialogToSetting(activity);
        }

    }

}
