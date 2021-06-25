package com.zhongjh.mvvmrapid.utils;


import android.app.ProgressDialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.zhongjh.mvvmrapid.R;

/**
 * @author zhongjh
 * @date 2021/4/21
 */
public class DialogUtil {

    /**
     * 显示ProgressDialog,过时是因为从设计上来说并不推荐使用这种类型阻挡用户操作，应该使用内嵌形式的progress
     *
     * @param context        上下文
     * @param progressDialog 当前的progressDialog,可以传递null
     */
    public static void showDialogProgressBar(@NonNull Context context, ProgressDialog progressDialog) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.common_loading));
        }
        progressDialog.show();
    }

    /**
     * 关闭ProgressDialog
     *
     * @param progressDialog 自己本身
     */
    public static void dismissDialogProgressBar(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
