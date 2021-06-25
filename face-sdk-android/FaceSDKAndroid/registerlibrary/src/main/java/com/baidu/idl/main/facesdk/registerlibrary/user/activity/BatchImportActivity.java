package com.baidu.idl.main.facesdk.registerlibrary.user.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.registerlibrary.R;
import com.baidu.idl.main.facesdk.registerlibrary.user.api.FaceApi;
import com.baidu.idl.main.facesdk.registerlibrary.user.db.DBManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.listener.OnImportListener;
import com.baidu.idl.main.facesdk.registerlibrary.user.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.ImportFileManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.ShareManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.FileUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.ToastUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.CircularProgressView;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.TipDialog;


/**
 * 批量导入
 * Created by v_liujialu01 on 2019/5/27.
 */

public class BatchImportActivity extends BaseActivity implements View.OnClickListener, OnImportListener,
        TipDialog.OnTipDialogClickListener {

    // view
    private TextView mButtonImport;             // 导入数据的按钮
    private RelativeLayout mRelativeImport;   // 显示进度的布局
    private View mViewBg;                      // 灰色背景
    private RelativeLayout mRelativeShow;      // 显示已上传文件

    // import
    private CircularProgressView mProgressBar;
    private TextView mTextImportFinish;   // 已处理
    private TextView mTextImportSuccess;  // 成功
    private TextView mTextImportFailure;  // 失败
    private TextView mTextImportTitle;
    private TextView mTextFinishTitle;
    private TextView mTextProgress;
    private Button mBtnDialogClose;

    private TipDialog mTipDialog;

    private Context mContext;
    private volatile boolean mImporting;   // 导入状态，是否正在导入


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_batch_imports);
        mContext = this;
        initView();
        initData();
    }

    private void initListener() {
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceSDKManager.getInstance().initModel(this, new SdkInitListener() {
                @Override
                public void initStart() {
                }

                @Override
                public void initLicenseSuccess() {
                }

                @Override
                public void initLicenseFail(int errorCode, String msg) {
                }

                @Override
                public void initModelSuccess() {
                    FaceSDKManager.initModelSuccess = true;
                    ToastUtils.toast(BatchImportActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(BatchImportActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
        // 释放
        ImportFileManager.getInstance().setIsNeedImport(false);
        ImportFileManager.getInstance().release();
    }

    private void initView() {
        mTipDialog = new TipDialog(mContext);
        mTipDialog.setOnTipDialogClickListener(this);
        ImageView imageBack = findViewById(R.id.image_import_back);
        imageBack.setOnClickListener(this);
        mButtonImport = findViewById(R.id.button_import);
        mButtonImport.setOnClickListener(this);
        mRelativeImport = findViewById(R.id.relative_progress);
        mProgressBar = findViewById(R.id.progress_bar);
        mTextImportFinish = findViewById(R.id.text_import_finish);
        mTextImportSuccess = findViewById(R.id.text_import_success);
        mTextImportFailure = findViewById(R.id.text_import_failure);
        mTextImportTitle = findViewById(R.id.text_progress_title);
        mTextFinishTitle = findViewById(R.id.text_title_finish);
        mTextProgress = findViewById(R.id.text_progress);
        mViewBg = findViewById(R.id.view_bg);
        mBtnDialogClose = findViewById(R.id.button_prog_close);
        mBtnDialogClose.setOnClickListener(this);
        mRelativeShow = findViewById(R.id.linear_show);
        ImageView imageClose = findViewById(R.id.image_delete);
        imageClose.setOnClickListener(this);
    }

    private void initData() {
        // 判断是否存在face.db文件
        if (ShareManager.getInstance(mContext).getDBState()) {
            mRelativeShow.setVisibility(View.VISIBLE);
            mButtonImport.setText("重新导入");
        }
        ImportFileManager.getInstance().setOnImportListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.button_import) {   // 点击导入数据按钮
            if (!mImporting) {
                if (!FileUtils.isSdCardAvailable()) {
                    ToastUtils.toast(mContext, "请插入SD卡");
                    return;
                }

                if ("重新导入".equals(mButtonImport.getText())) {
                    mTipDialog.show();
                    mTipDialog.setTextTitle("重新导入");
                    mTipDialog.setTextMessage("旧人脸数据将被覆盖，确认重新导入？");
                    mTipDialog.setTextConfirm("导入");
                    mTipDialog.setCancelable(false);
                    return;
                }

                mImporting = true;
                // 开始导入
                ImportFileManager.getInstance().batchImport();
            }
        } else if (id == R.id.image_import_back) {
            finish();
        } else if (id == R.id.button_prog_close) {
            animatorTranslateDown(mRelativeImport);
        } else if (id == R.id.image_delete) {    // 删除导入的库
            mTipDialog.show();
            mTipDialog.setTextTitle("确定删除");
            mTipDialog.setTextMessage("删除后人脸库将被清空，确认删除？");
            mTipDialog.setTextConfirm("删除");
            mTipDialog.setCancelable(false);
        }
    }

    /**
     * 解压完毕，显示导入进度View
     */
    @Override
    public void showProgressView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mViewBg == null || mRelativeImport == null
                        || mTextImportTitle == null || mTextFinishTitle == null) {
                    return;
                }
                animatorTranslateUp(mRelativeImport);
                mTextImportTitle.setVisibility(View.VISIBLE);
                mTextFinishTitle.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 正在导入，实时更新导入状态
     */
    @Override
    public void onImporting(final int totalCount, final int successCount, final int failureCount,
                            final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressBar == null || mTextImportFinish == null
                        || mTextImportSuccess == null || mTextImportFailure == null) {
                    return;
                }
                // mBtnDialogClose.setEnabled(false);    // 设置进度条“关闭”不可点击
                mProgressBar.setProgress((int) (progress * 100));
                mTextImportFinish.setText("数据总数：" + totalCount);
                mTextImportSuccess.setText("导入成功：" + successCount);
                mTextImportFailure.setText("导入失败：" + failureCount);
                mTextProgress.setText("" + ((int) (progress * 100)));
            }
        });
    }

    /**
     * 导入结束，显示导入结果
     */
    @Override
    public void endImport(final int totalCount, final int successCount, final int failureCount) {
        // 数据变化，更新内存
        FaceApi.getInstance().initDatabases(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTextImportTitle == null || mTextFinishTitle == null) {
                    return;
                }
                mBtnDialogClose.setEnabled(true);    // 设置进度条“关闭”可点击
                mTextImportTitle.setVisibility(View.INVISIBLE);
                mTextFinishTitle.setVisibility(View.VISIBLE);
                mTextImportFinish.setText("数据总数：" + totalCount);
                mTextImportSuccess.setText("导入成功：" + successCount);
                mTextImportFailure.setText("导入失败：" + failureCount);
                mTextImportFailure.setTextColor(Color.parseColor("#F34B56"));
                mRelativeShow.setVisibility(View.VISIBLE);
                mButtonImport.setText("重新导入");
                // 设置数据库状态
                ShareManager.getInstance(mContext).setDBState(true);
                mImporting = false;
            }
        });
    }

    /**
     * 提示导入过程中的错误信息
     */
    @Override
    public void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (message == null) {
                    return;
                }
                ToastUtils.toast(mContext, message);
                mImporting = false;
            }
        });
    }

    /**
     * 从底部向上出现的动画
     * @param view
     */
    private void animatorTranslateUp(View view) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationY",
                view.getHeight(), 0);
        translateAnimator.setDuration(300);
        translateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mViewBg.setVisibility(View.VISIBLE);
                mRelativeImport.setVisibility(View.VISIBLE);
            }
        });
        translateAnimator.start();
    }

    /**
     * 从上向下的动画
     * @param view
     */
    private void animatorTranslateDown(View view) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(view, "translationY",
                0, view.getHeight());
        translateAnimator.setDuration(300);
        translateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mViewBg.setVisibility(View.GONE);
                mRelativeImport.setVisibility(View.GONE);
                ImportFileManager.getInstance().setIsNeedImport(false);
                ImportFileManager.getInstance().release();
            }
        });
        translateAnimator.start();
    }

    @Override
    public void onCancel() {
        if (mTipDialog != null) {
            mTipDialog.dismiss();
        }
    }

    @Override
    public void onConfirm(String tipType) {
        if (mTipDialog != null) {
            mTipDialog.dismiss();
        }
        // 根据对话框的内容来判断操作
        if ("重新导入".equals(tipType)) {
            if (!mImporting) {
                if (!FileUtils.isSdCardAvailable()) {
                    ToastUtils.toast(mContext, "请插入SD卡");
                    return;
                }
                mImporting = true;
                // 开始导入
                ImportFileManager.getInstance().setIsNeedImport(true);
                ImportFileManager.getInstance().batchImport();
            }
        } else if ("确定删除".equals(tipType)) {
            // 清空用户表
            DBManager.getInstance().clearTable();
            // 设置数据库状态
            ShareManager.getInstance(mContext).setDBState(false);
            mRelativeShow.setVisibility(View.GONE);
            mButtonImport.setText("导入数据");
            mImporting = false;
        }
    }
}
