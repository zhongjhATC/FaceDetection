package com.zjh.facedetection.ui.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.idl.face.platform.ui.utils.BrightnessUtils;
import com.baidu.idl.face.platform.ui.utils.VolumeUtils;
import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.zjh.facedetection.BR;
import com.zjh.facedetection.R;
import com.zjh.facedetection.constants.FilePaths;
import com.zjh.facedetection.databinding.ActivityMainBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhongjh.mvvmrapid.base.ui.BaseActivity;
import com.zhongjh.mvvmrapid.utils.PermissionsUtil;
import com.zhongjh.mvvmrapid.utils.ToastUtils;
import com.zhongjh.mvvmrapid.utils.WindowUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 首页
 * <p>
 * 百度相关文档：
 * https://ai.baidu.com/sdk#asr
 *
 * @author zhongjh
 * @date 2021/4/15
 */
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * 询问是否需要帮忙的dialog
     */
    MaterialDialog mDialogHelp;
    MaterialDialog mDialogRecord;

    /**
     * 记录视频的播放点，切换后台后可以恢复播放
     */
    int mPlayingPos = 0;

    /**
     * 监听系统音量广播
     */
    protected BroadcastReceiver mVolumeReceiver;

    /**
     * 播放的图片文件名称
     */
    String mImageName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowUtil.setFullScreen(MainActivity.this, false);
        super.onCreate(savedInstanceState);
        setScreenBright();
        initPermissions();
    }

    /**
     * 销毁时需要释放识别资源。
     */
    @Override
    protected void onDestroy() {
        // 如果之前调用过myRecognizer.loadOfflineEngine()， release()里会自动调用释放离线资源
        // 基于DEMO5.1 卸载离线资源(离线时使用) release()方法中封装了卸载离线资源的过程
        // 基于DEMO的5.2 退出事件管理器
        if (viewModel.mRecognizer != null) {
            viewModel.mRecognizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 暂停视频
        mPlayingPos = binding.videoView.getCurrentPosition();
        binding.videoView.stopPlayback();
        // 暂停系统音量
        VolumeUtils.unRegisterVolumeReceiver(this, mVolumeReceiver);
        mVolumeReceiver = null;

        // 关闭所有窗口
        if (mDialogHelp != null && mDialogHelp.isShowing()) {
            mDialogHelp.dismiss();
            viewModel.setHelp(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重置系统音量
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mVolumeReceiver = VolumeUtils.registerVolumeReceiver(this, viewModel);
        // 如果是视频正在播放中跑到后台，现在跑到前台就重置视频重新播放，因为视频可能被删了，所以要重新设置
        if (mPlayingPos > 0) {
            viewModel.playVideo();
            mPlayingPos = 0;
        }
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public boolean isStartAutoHideSoftKeyboard() {
        return false;
    }

    @Override
    public void initParam() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {
        viewModel.mUiChange.needHelp.observe(this, aVoid -> needHelp());
        viewModel.mUiChange.noNeedHelp.observe(this, aVoid -> noNeedHelp());
        viewModel.mUiChange.initFaceView.observe(this,
                surfaceView ->
                        binding.flDetectSurface.addView(surfaceView));
        viewModel.mUiChange.initFaceAuthorizationFailure.observe(this,
                errorModel ->
                        runOnUiThread(() ->
                                ToastUtils.showShort("初始化失败 = " + errorModel.getErrCode() + ", " + errorModel.getErrMsg())));

        viewModel.mUiChange.getBestImage.observe(this,
                this::getBestImage);

        viewModel.mUiChange.initVideo.observe(this,
                aVoid -> initVideo());

        viewModel.mUiChange.playVideo.observe(this, path -> {
            playVideo(path);
            showVideo();
        });

        viewModel.mUiChange.setImageName.observe(this,
                imageName -> mImageName = imageName);

        viewModel.mUiChange.showImgScreen.observe(this,
                aVoid -> showImgScreen());

        viewModel.mUiChange.showRecordDialog.observe(this,
                aVoid -> showRecordDialog());

        viewModel.mUiChange.onPreviewFrame.observe(this, bytes -> {



        });
    }

    /**
     * 设置屏幕亮度
     */
    private void setScreenBright() {
        int currentBright = BrightnessUtils.getScreenBrightness(this);
        BrightnessUtils.setBrightness(this, currentBright + 100);
    }

    /**
     * 初始化获取最大宽高
     */
    private DisplayMetrics initDisplayWidthHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = MainActivity.this.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        return dm;
    }

    /**
     * 请求权限
     */
    private void initPermissions() {
        // 判断权限
        final RxPermissions rxPermissions = new RxPermissions(MainActivity.this);

        viewModel.accept(rxPermissions
                .requestEachCombined(Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .subscribe(permission -> PermissionsUtil.managePermissions(permission, MainActivity.this, new PermissionsUtil.OnPermissions() {
                    @Override
                    public void onGranted() {
                        DisplayMetrics dm = initDisplayWidthHeight();
                        viewModel.init(dm.widthPixels, dm.heightPixels + BarUtils.getStatusBarHeight());
                    }

                    @Override
                    public void onShouldShowRequestPermissionRationale() {
                        super.onShouldShowRequestPermissionRationale();
                        ToastUtils.showShort("拒绝权限不能启动该程序");
                    }
                })));
    }

    /**
     * 初始化视频
     */
    private void initVideo() {
        binding.videoView.setMediaController(null);
        binding.videoView.setOnCompletionListener(mp -> viewModel.playImageScreen());
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            // 播放视频报错，直接显示图片
            viewModel.playImageScreen();
            return true;
        });
    }

    /**
     * 播放视频
     *
     * @param path 地址
     */
    private void playVideo(String path) {
        binding.videoView.setVideoPath(path);
        binding.videoView.start();
    }

    /**
     * 显示视频，其他隐藏，因为全屏原因显示视频，隐藏图片的时候有个300毫秒的空白期，所以就不隐藏图片了
     */
    private void showVideo() {
        Log.d(TAG, "showVideo");
        binding.videoView.setVisibility(View.GONE);
        binding.imgScreen.setVisibility(View.GONE);
    }

    /**
     * 显示图片，其他隐藏
     */
    private void showImgScreen() {
        Log.d(TAG, "showImgScreen");
        Glide.with(binding.imgScreen.getContext())
                .load(FilePaths.createImageFile(getApplicationContext(), mImageName))
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .into(binding.imgScreen);
        binding.imgScreen.setVisibility(View.GONE);
        binding.videoView.setVisibility(View.GONE);
    }

    /**
     * 检测到人脸
     *
     * @param bmp 人脸数据源
     */
    private void getBestImage(Bitmap bmp) {
        Glide.with(binding.imgFace.getContext()).load(bmp)
                .centerCrop()
                .into(binding.imgFace);
        showNeedHelpDialog();
        viewModel.startVoiceRecognition();
    }

    /**
     * 弹出是否需要帮助提示
     */
    private void showNeedHelpDialog() {
        mDialogHelp = new MaterialDialog(MainActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
        mDialogHelp.setTitle("标题");
        mDialogHelp.message(-1, "你需要帮助吗", null);
        mDialogHelp.positiveButton(-1, "需要", materialDialog -> {
            needHelp();
            return null;
        });
        mDialogHelp.negativeButton(-1, "不需要", materialDialog -> {
            noNeedHelp();
            return null;
        });
        mDialogHelp.getWindow().getAttributes().windowAnimations = R.style.DialogStyle;
        mDialogHelp.cancelable(false);
        mDialogHelp.show();
    }

    /**
     * 需要帮助
     */
    private void needHelp() {
        if (mDialogHelp != null && mDialogHelp.isShowing()) {
            ToastUtils.showShort("需要");
            mDialogHelp.dismiss();
            // 停止语音识别，语音识别结束后，会判断是否需要判断继续录音
            viewModel.setHelp(true);
            viewModel.stopVoiceRecognition();
        }
    }

    /**
     * 不需要帮助
     */
    private void noNeedHelp() {
        if (mDialogHelp != null && mDialogHelp.isShowing()) {
            ToastUtils.showShort("不需要");
            viewModel.stopVoiceRecognition();
            mDialogHelp.dismiss();
            viewModel.faceDetectReset();
            // 停止语音识别，语音识别结束后，会判断是否需要判断继续录音
            viewModel.setHelp(false);
            viewModel.stopVoiceRecognition();
        }
    }

    /**
     * 弹出你正在录音提示
     */
    private void showRecordDialog() {
        mDialogRecord = new MaterialDialog(MainActivity.this, MaterialDialog.getDEFAULT_BEHAVIOR());
        mDialogRecord.setTitle("标题");
        mDialogRecord.message(-1, "你正在录音...", null);
        mDialogRecord.positiveButton(-1, "结束", materialDialog -> {
            viewModel.stopRecord();
            return null;
        });
        mDialogRecord.getWindow().getAttributes().windowAnimations = R.style.DialogStyle;
        mDialogRecord.cancelable(false);
        mDialogRecord.show();
    }
}
