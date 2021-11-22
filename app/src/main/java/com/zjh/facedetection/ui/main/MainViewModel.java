package com.zjh.facedetection.ui.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.RecogResult;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceEnvironment;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.LivenessTypeEnum;
import com.baidu.idl.face.platform.listener.IInitCallback;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.ui.utils.VolumeUtils;
import com.baidu.idl.face.platform.utils.DensityUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.zjh.facedetection.constants.FaceConst;
import com.zjh.facedetection.constants.FilePaths;
import com.zjh.facedetection.manager.QualityConfigManager;
import com.zjh.facedetection.model.ErrorModel;
import com.zjh.facedetection.model.QualityConfig;
import com.zjh.facedetection.utils.BitmapUtils;
import com.zjh.facedetection.utils.SharedPreferencesUtil;
import com.zjh.facedetection.voice.recognition.AllRecogParams;
import com.zjh.facedetection.voice.recognition.CommonRecogParams;
import com.tencent.mmkv.MMKV;
import com.zhongjh.mvvmrapid.base.viewmodel.BaseViewModel;
import com.zhongjh.mvvmrapid.bus.event.SingleLiveEvent;
import com.zhongjh.mvvmrapid.utils.ToastUtils;
import com.zhongjh.retrofitdownloadlib.http.DownloadHelper;
import com.zhongjh.retrofitdownloadlib.http.DownloadListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.zjh.facedetection.constants.Constants.STR_NEED;
import static com.zjh.facedetection.constants.Constants.STR_NO_NEED;
import static com.zjh.facedetection.constants.FaceConst.IMAGE_TAG;
import static com.zjh.facedetection.constants.FaceConst.VIDEO_TAG;

/**
 * 首页
 *
 * @author zhongjh
 * @date 2021/4/15
 */
public class MainViewModel extends BaseViewModel implements
        VolumeUtils.VolumeCallback {

    Application mApplication;

    /**
     * 相机类,里面包含检测人脸FaceDetect类
     */
    FaceCamera mFaceCamera;
    /**
     * 录音类
     */
    RecordManager mRecordManager;

    /**
     * 人脸图片
     */
    Bitmap mFaceBitmap;

    /**
     * 百度语音识别控制器，使用MyRecognizer控制识别的流程
     */
    protected MyRecognizer mRecognizer;

    /**
     * 动作活体条目集合
     */
    public static List<LivenessTypeEnum> livenessList = new ArrayList<>();

    /**
     * 录音的总长度时间，如果用户提前结束就关闭它，Activity销毁时也关闭它
     */
    private Disposable mDisposableRecord;
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;

    /**
     * 播放的视频文件名称
     */
    String mVideoName;

    /**
     * 是否需要帮助
     */
    private boolean isHelp;

    public void setHelp(boolean help) {
        isHelp = help;
    }

    /**
     * 正在下载视频,只有走到最后面删除完视频冗余文件才算是结束下载流程
     */
    private boolean mIsDownloadInVideo;

    /**
     * 正在下载图片,只有走到最后面删除完图片冗余文件才算是结束下载流程
     */
    private boolean mIsDownloadInImage;

    /**
     * 界面发生改变的观察者
     */
    public UiChangeObservable mUiChange = new UiChangeObservable();

    /**
     * 初始化下载video
     */
    private final DownloadHelper mDownloadHelperVideo = new DownloadHelper(new DownloadListener() {
        @Override
        public void onStartDownload() {
            mIsDownloadInVideo = true;
        }

        @Override
        public void onProgress(int i) {
            Log.d(VIDEO_TAG, "onProgress: " + i);
        }

        @Override
        public void onFinishDownload(File file) {
            // 当前下载的文件名称
            String videoDownLoadName = file.getName();
            Log.d(VIDEO_TAG, "onFinishDownload: " + videoDownLoadName);
            // 删除文件 1.被观察者
            Observable<String> observable = Observable.create(emitter -> {
                MMKV kv = MMKV.defaultMMKV();
                // 当前屏保的文件名称
                String videoScreenInName = "";
                if (kv != null) {
                    videoScreenInName = kv.decodeString(FilePaths.VIDEO_NAME_SCREEN_IN);
                }
                // 循环文件
                File videoDir = FilePaths.videoDir(mApplication);
                if (videoDir != null && videoDir.listFiles() != null) {
                    for (File videoFile : Objects.requireNonNull(videoDir.listFiles())) {
                        // 判断名字是否相符合, 不删当前屏保和当前下载
                        if (!videoFile.getName().equals(videoScreenInName) && !videoFile.getName().equals(videoDownLoadName)) {
                            videoFile.delete();
                        }
                    }
                }

                // 存储播放文件名、下载文件名称防止下次下载删除
                if (kv != null) {
                    kv.putString(FilePaths.VIDEO_NAME_SCREEN_IN, videoDownLoadName);
                    kv.putString(FilePaths.VIDEO_DOWN_LOAD, videoDownLoadName);
                }
                emitter.onNext(videoDownLoadName);
                //发送完成
                emitter.onComplete();
            });

            // 2.观察者
            Observer<String> observer = new Observer<String>() {

                @Override
                public void onSubscribe(@NotNull Disposable d) {
                    accept(d);
                }

                // 接收发送的消息
                @Override
                public void onNext(@NotNull String videoDownLoadName) {
                    mVideoName = videoDownLoadName;
                    mIsDownloadInVideo = false;
                }

                // 接受异常通知
                @Override
                public void onError(@NotNull Throwable e) {
                    mIsDownloadInVideo = false;
                }

                // 接受发送完毕通知
                @Override
                public void onComplete() {

                }
            };

            // 3.观察者订阅被观察者
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }

        @Override
        public void onFail(Throwable throwable) {
            mIsDownloadInVideo = false;
            Log.d(VIDEO_TAG, "onFail: " + throwable.getMessage());
        }
    });

    /**
     * 初始化下载图片
     */
    private final DownloadHelper mDownloadHelperImage = new DownloadHelper(new DownloadListener() {
        @Override
        public void onStartDownload() {
            mIsDownloadInImage = true;
        }

        @Override
        public void onProgress(int i) {
            Log.d(IMAGE_TAG, "onProgress: " + i);
        }

        @Override
        public void onFinishDownload(File file) {
            // 当前下载的文件名称
            String imageDownLoadName = file.getName();
            Log.d(IMAGE_TAG, "onFinishDownload: " + imageDownLoadName);
            // 删除文件 1.被观察者
            Observable<String> observable = Observable.create(emitter -> {
                MMKV kv = MMKV.defaultMMKV();
                // 当前屏保的文件名称
                String imageScreenInName = "";
                if (kv != null) {
                    imageScreenInName = kv.decodeString(FilePaths.IMAGE_NAME_SCREEN_IN);
                }
                // 循环文件
                File imageDir = FilePaths.imageDir(mApplication);
                if (imageDir != null && imageDir.listFiles() != null) {
                    for (File imageFile : Objects.requireNonNull(imageDir.listFiles())) {
                        // 判断名字是否相符合, 不删当前屏保和当前下载
                        if (!imageFile.getName().equals(imageScreenInName) && !imageFile.getName().equals(imageDownLoadName)) {
                            imageFile.delete();
                        }
                    }
                }

                // 存储播放文件名、下载文件名称防止下次下载删除
                if (kv != null) {
                    kv.putString(FilePaths.IMAGE_NAME_SCREEN_IN, imageDownLoadName);
                    kv.putString(FilePaths.IMAGE_DOWN_LOAD, imageDownLoadName);
                }
                emitter.onNext(imageDownLoadName);
                //发送完成
                emitter.onComplete();
            });

            // 2.观察者
            Observer<String> observer = new Observer<String>() {

                @Override
                public void onSubscribe(@NotNull Disposable d) {
                    accept(d);
                }

                // 接收发送的消息
                @Override
                public void onNext(@NotNull String imageDownLoadName) {
                    // 通知ui
                    mUiChange.setImageName.setValue(imageDownLoadName);
                    mIsDownloadInImage = false;
                }

                // 接受异常通知
                @Override
                public void onError(@NotNull Throwable e) {
                    mIsDownloadInImage = false;
                }

                // 接受发送完毕通知
                @Override
                public void onComplete() {

                }
            };

            // 3.观察者订阅被观察者
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        }

        @Override
        public void onFail(Throwable throwable) {
            mIsDownloadInImage = false;
            Log.d(IMAGE_TAG, "onFail: " + throwable.getMessage());
        }
    });

    /**
     * 通知UI事件
     */
    public static class UiChangeObservable {
        // 不需要
        public SingleLiveEvent<Void> noNeedHelp = new SingleLiveEvent<>();

        // 需要
        public SingleLiveEvent<Void> needHelp = new SingleLiveEvent<>();

        // 人脸授权失败
        public SingleLiveEvent<ErrorModel> initFaceAuthorizationFailure = new SingleLiveEvent<>();

        // 检测到人脸
        public SingleLiveEvent<Bitmap> getBestImage = new SingleLiveEvent<>();

        // 初始化摄像view
        public SingleLiveEvent<SurfaceView> initFaceView = new SingleLiveEvent<>();

        // 初始化video
        public SingleLiveEvent<Void> initVideo = new SingleLiveEvent<>();

        // 播放video
        public SingleLiveEvent<String> playVideo = new SingleLiveEvent<>();

        // 显示图片
        public SingleLiveEvent<Void> showImgScreen = new SingleLiveEvent<>();

        // 设置image的文件名称
        public SingleLiveEvent<String> setImageName = new SingleLiveEvent<>();

        // 显示正在录音
        public SingleLiveEvent<Void> showRecordDialog = new SingleLiveEvent<>();

        // 录制每一帧的事件
        public SingleLiveEvent<byte[]> onPreviewFrame = new SingleLiveEvent<>();
    }

    public MainViewModel(@NonNull Application application) {
        super(application);
        mApplication = application;
    }

    /**
     * 初始化,view会在权限通过后，通知初始化
     *
     * @param displayWidth  屏幕宽度
     * @param displayHeight 屏幕高度包括状态栏
     */
    public void init(int displayWidth, int displayHeight) {
        mDisplayWidth = displayWidth;
        mDisplayHeight = displayHeight;
        initVoiceRecognition();
        initFaceAuthorization();
        mUiChange.initVideo.call();
        playVideo();
        downloadScreen();
    }

    @Override
    public void onPause() {
        if (mFaceCamera != null &&
                mFaceCamera.mFaceDetect != null &&
                mFaceCamera.mFaceDetect.mDetectStrategy != null) {
            mFaceCamera.mFaceDetect.mDetectStrategy.reset();
        }
        super.onPause();
        if (mFaceCamera != null &&
                mFaceCamera.mFaceDetect != null) {
            mFaceCamera.mFaceDetect.onPause();
        }
        stopPreview();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 重置摄像
        if (mFaceCamera != null) {
            mFaceCamera.startPreview();
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposableRecord != null) {
            mDisposableRecord.dispose();
            mDisposableRecord = null;
        }
        mDownloadHelperVideo.dispose();
        mDownloadHelperImage.dispose();
    }

    // region 识别语音

    /**
     * 初始化语音
     */
    private void initVoiceRecognition() {
        // DEMO集成步骤 1.1 1.3 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例,并注册输出事件
        mRecognizer = new MyRecognizer(mApplication, new IRecogListener() {
            @Override
            public void onAsrReady() {

            }

            @Override
            public void onAsrBegin() {

            }

            @Override
            public void onAsrEnd() {

            }

            @Override
            public void onAsrPartialResult(String[] results, RecogResult recogResult) {

            }

            @Override
            public void onAsrOnlineNluResult(String nluResult) {

            }

            @Override
            public void onAsrFinalResult(String[] results, RecogResult recogResult) {
                if (results.length > 0 && results[0] != null) {
                    ToastUtils.showShort(results[0] + "");
                    // 语音内容
                    if (results[0].contains(STR_NO_NEED)) {
                        // 不需要
                        mUiChange.noNeedHelp.call();
                    } else if (results[0].contains(STR_NEED)) {
                        // 需要
                        mUiChange.needHelp.call();
                    }
                }
            }

            @Override
            public void onAsrFinish(RecogResult recogResult) {

            }

            @Override
            public void onAsrFinishError(int errorCode, int subErrorCode, String descMessage, RecogResult recogResult) {

            }

            @Override
            public void onAsrLongFinish() {

            }

            @Override
            public void onAsrVolume(int volumePercent, int volume) {

            }

            @Override
            public void onAsrAudio(byte[] data, int offset, int length) {

            }

            @Override
            public void onAsrExit() {
                // 结束后判断是否需要帮助
                if (isHelp) {
                    // 重置是否需要帮助
                    isHelp = false;
                    startRecord();
                    mUiChange.showRecordDialog.call();
                }
            }

            @Override
            public void onOfflineLoaded() {

            }

            @Override
            public void onOfflineUnLoaded() {

            }
        });
    }

    /**
     * 开始识别语音
     * 基于DEMO集成2.1, 2.2 设置识别参数并发送开始事件
     */
    public void startVoiceRecognition() {
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        final Map<String, Object> params = fetchParams();
        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        mRecognizer.start(params);
    }

    /**
     * 停止识别语音
     */
    public void stopVoiceRecognition() {
        mRecognizer.cancel();
    }

    /**
     * @return 创建构建语音识别的所需参数
     */
    protected Map<String, Object> fetchParams() {
        /*
         * Api的参数类，仅仅用于生成调用START的json字符串，本身与SDK的调用无关
         */
        CommonRecogParams apiParams = new AllRecogParams();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mApplication);
        //  上面的获取是为了生成下面的Map， 自己集成时可以忽略
        //  集成时不需要上面的代码，只需要params参数。
        return apiParams.fetch(sp);
    }

    @Override
    public void volumeChanged() {
//        if (mFaceCamera != null && mFaceCamera.mFaceDetect != null) {
//            try {
//                AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//                if (am != null) {
//                    int cv = am.getStreamVolume(AudioManager.STREAM_MUSIC);
//                    mFaceCamera.mFaceDetect.mIsEnableSound = cv > 0;
//                    mFaceCamera.mFaceDetect.mSoundView.setImageResource(mIsEnableSound
//                            ? com.baidu.idl.face.platform.ui.R.mipmap.icon_titlebar_voice2 : com.baidu.idl.face.platform.ui.R.mipmap.icon_titlebar_voice1);
//                    if (mIDetectStrategy != null) {
//                        mIDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);
//                    }
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
    }

    // endregion 识别语音

    // region 人脸检测

    /**
     * 初始化人脸授权
     */
    private void initFaceAuthorization() {
        boolean success = initFaceConfig();
        if (!success) {
            ToastUtils.showShort("初始化失败 = json配置文件解析出错");
            return;
        }
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(mApplication, "zhongjh-face-android",
                "idl-license.faceexample-face-android-1", new IInitCallback() {
                    @Override
                    public void initSuccess() {
                        ThreadUtils.runOnUiThread(() -> {
                            ToastUtils.showShort("初始化成功");
                            initFaceDetect();
                            initFaceView();
                            initRecordManager();
                        });
                    }

                    @Override
                    public void initFailure(final int errCode, final String errMsg) {
                        ThreadUtils.runOnUiThread(() ->
                                mUiChange.initFaceAuthorizationFailure.setValue(new ErrorModel(errCode, errMsg)));

                    }
                });
    }

    /**
     * 初始化人脸配置
     */
    private boolean initFaceConfig() {
        FaceConfig config = FaceSDKManager.getInstance().getFaceConfig();
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 质量等级（0：正常、1：宽松、2：严格、3：自定义）
        // 获取保存的质量等级
        SharedPreferencesUtil util = new SharedPreferencesUtil(mApplication);
        int qualityLevel = (int) util.getSharedPreference(FaceConst.KEY_QUALITY_LEVEL_SAVE, -1);
        if (qualityLevel == -1) {
            qualityLevel = FaceConst.QUALITY_HIGH;
        }
        // 根据质量等级获取相应的质量值（注：第二个参数要与质量等级的set方法参数一致）
        QualityConfigManager manager = QualityConfigManager.getInstance();
        manager.readQualityFile(mApplication, qualityLevel);
        QualityConfig qualityConfig = manager.getConfig();
        if (qualityConfig == null) {
            return false;
        }
        // 设置模糊度阈值
        config.setBlurnessValue(qualityConfig.getBlur());
        // 设置最小光照阈值（范围0-255）
        config.setBrightnessValue(qualityConfig.getMinIllum());
        // 设置最大光照阈值（范围0-255）
        config.setBrightnessMaxValue(qualityConfig.getMaxIllum());
        // 设置左眼遮挡阈值
        config.setOcclusionLeftEyeValue(qualityConfig.getLeftEyeOcclusion());
        // 设置右眼遮挡阈值
        config.setOcclusionRightEyeValue(qualityConfig.getRightEyeOcclusion());
        // 设置鼻子遮挡阈值
        config.setOcclusionNoseValue(qualityConfig.getNoseOcclusion());
        // 设置嘴巴遮挡阈值
        config.setOcclusionMouthValue(qualityConfig.getMouseOcclusion());
        // 设置左脸颊遮挡阈值
        config.setOcclusionLeftContourValue(qualityConfig.getLeftContourOcclusion());
        // 设置右脸颊遮挡阈值
        config.setOcclusionRightContourValue(qualityConfig.getRightContourOcclusion());
        // 设置下巴遮挡阈值
        config.setOcclusionChinValue(qualityConfig.getChinOcclusion());
        // 设置人脸姿态角阈值
        config.setHeadPitchValue(qualityConfig.getPitch());
        config.setHeadYawValue(qualityConfig.getYaw());
        config.setHeadRollValue(qualityConfig.getRoll());
        // 设置可检测的最小人脸阈值
        config.setMinFaceSize(FaceEnvironment.VALUE_MIN_FACE_SIZE);
        // 设置可检测到人脸的阈值
        config.setNotFaceValue(0.6F);
        // 设置闭眼阈值
        config.setEyeClosedValue(FaceEnvironment.VALUE_CLOSE_EYES);
        // 设置图片缓存数量
        config.setCacheImageNum(FaceEnvironment.VALUE_CACHE_IMAGE_NUM);
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight
        config.setLivenessTypeList(livenessList);
        // 设置动作活体是否随机
        config.setLivenessRandom(false);
        // 设置开启提示音
        config.setSound(true);
        // 原图缩放系数
        config.setScale(FaceEnvironment.VALUE_SCALE);
        // 抠图宽高的设定，为了保证好的抠图效果，建议高宽比是4：3
        config.setCropHeight(FaceEnvironment.VALUE_CROP_HEIGHT);
        config.setCropWidth(FaceEnvironment.VALUE_CROP_WIDTH);
        // 抠图人脸框与背景比例
        config.setEnlargeRatio(FaceEnvironment.VALUE_CROP_ENLARGERATIO);
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.setSecType(FaceEnvironment.VALUE_SEC_TYPE);
        // 检测超时设置
        config.setTimeDetectModule(Integer.MAX_VALUE);
        // 检测框远近比率
        config.setFaceFarRatio(FaceEnvironment.VALUE_FAR_RATIO);
        config.setFaceClosedRatio(FaceEnvironment.VALUE_CLOSED_RATIO);
        FaceSDKManager.getInstance().setFaceConfig(config);
        return true;
    }

    /**
     * 初始化FaceDetect
     * 里面包含开启摄像头等功能和包含获取人脸回调
     */
    private void initFaceDetect() {
        mFaceCamera = new FaceCamera(mApplication, mDisplayWidth, mDisplayHeight,mUiChange);
        // 检测到人脸的回调
        mFaceCamera.mFaceDetect.setCallback(new FaceDetect.Callback() {
            @Override
            public void getBestImage(ImageInfo imageInfo) {
                // 显示人脸
                mFaceBitmap = BitmapUtils.base64ToBitmap(imageInfo.getBase64());
                mFaceBitmap = FaceSDKManager.getInstance().scaleImage(mFaceBitmap,
                        DensityUtils.dip2px(mApplication, 97),
                        DensityUtils.dip2px(mApplication, 97));
                mUiChange.getBestImage.setValue(mFaceBitmap);
            }

            @Override
            public void getNoFaceMessage(String message) {
                ToastUtils.showShort(message);
            }

            @Override
            public void getFaceMessage(String message) {

            }
        });
    }

    /**
     * 初始化摄像相关view
     */
    private void initFaceView() {
        FrameLayout.LayoutParams cameraFl = new FrameLayout.LayoutParams(
                (int) (mFaceCamera.mDisplayWidth * 1F),
                (int) (mFaceCamera.mDisplayHeight * 1F),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        mFaceCamera.mSurfaceView.setLayoutParams(cameraFl);
        mUiChange.initFaceView.setValue(mFaceCamera.mSurfaceView);
    }

    /**
     * 停止摄像
     */
    protected void stopPreview() {
        if (mFaceCamera != null && mFaceCamera.mCamera != null) {
            try {
                mFaceCamera.mCamera.setErrorCallback(null);
                mFaceCamera.mCamera.setPreviewCallback(null);
                mFaceCamera.mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mFaceCamera.mCamera);
                mFaceCamera.mCamera = null;
            }
        }
        if (mFaceCamera != null && mFaceCamera.mSurfaceHolder != null) {
            mFaceCamera.mSurfaceHolder.removeCallback(mFaceCamera);
        }
        if (mFaceCamera != null && mFaceCamera.mFaceDetect != null
                && mFaceCamera.mFaceDetect.mDetectStrategy != null) {
            mFaceCamera.mFaceDetect.mDetectStrategy.reset();
            mFaceCamera.mFaceDetect.mDetectStrategy = null;
        }
    }

    /**
     * 2秒后重新启动人脸检测
     */
    public void faceDetectReset() {
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        accept(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Long aLong) {
                        mFaceCamera.mFaceDetect.reset();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // endregion

    // region 录音

    /**
     * 初始化录音
     */
    private void initRecordManager() {
        mRecordManager = new RecordManager(mApplication);
        mRecordManager.setAudioCallback(new RecordManager.AudioCallback() {
            @Override
            public void stopRecord() {
                ToastUtils.showShort("录音完成");
                updateRecordDetail();
            }

            @Override
            public void recordError(String message) {
                ToastUtils.showShort(message);
            }
        });
    }

    /**
     * 用户需要帮助：开始录音
     */
    public void startRecord() {
        mRecordManager.startRecord();
        timingStop();
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        mRecordManager.stopRecord();
        deleteRecordFile();
        if (mDisposableRecord != null) {
            mDisposableRecord.dispose();
            mDisposableRecord = null;
        }
        // 重新开启人脸识别
        faceDetectReset();
    }

    /**
     * 定时停止录音
     */
    private void timingStop() {
        Observable.timer(2, TimeUnit.MINUTES)
                .doOnNext(aLong -> stopRecord())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        mDisposableRecord = d;
                    }

                    @Override
                    public void onNext(@NotNull Long value) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 删除所有录音文件，除了最后一个
     */
    private void deleteRecordFile() {
        // 删除文件 1.被观察者
        Observable<Boolean> observable = Observable.create(emitter -> {
            try {
                File recordDir = FilePaths.recordDir(mApplication);
                if (recordDir != null && recordDir.listFiles() != null) {
                    for (File recordFile : Objects.requireNonNull(recordDir.listFiles())) {
                        // 判断名字是否相符合, 不删当前屏保和当前下载
                        if (!recordFile.getName().equals(mRecordManager.recordName)) {
                            recordFile.delete();
                        }
                    }
                }
                emitter.onNext(true);
            } catch (Exception ex) {
                emitter.onError(ex);
            } finally {
                // 发送完成
                emitter.onComplete();
            }
        });

        // 2.观察者
        Observer<Boolean> observer = new Observer<Boolean>() {

            @Override
            public void onSubscribe(@NotNull Disposable d) {
                accept(d);
            }

            // 接收发送的消息
            @Override
            public void onNext(@NotNull Boolean isDelete) {

            }

            // 接受异常通知
            @Override
            public void onError(@NotNull Throwable e) {

            }

            // 接受发送完毕通知
            @Override
            public void onComplete() {

            }
        };

        // 3.观察者订阅被观察者
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    // endregion

    // region 播放屏保

    /**
     * 播放本地视频直到结束
     */
    public void playVideo() {
        if (TextUtils.isEmpty(mVideoName)) {
            // mVideoName如果是null的就是初次播放
            MMKV kv = MMKV.defaultMMKV();
            // 获取缓存的屏保视频
            mVideoName = kv.decodeString(FilePaths.VIDEO_NAME_SCREEN_IN);
            playVideo(mVideoName);
        } else {
            playVideo(mVideoName);
        }
    }

    /**
     * 播放本地视频直到结束
     *
     * @param videoName 视频文件名称
     */
    public void playVideo(String videoName) {
        if (TextUtils.isEmpty(videoName)) {
            playImageScreen();
        }
        // 判断文件是否存在
        File file = FilePaths.createVideoFile(mApplication, videoName);
        if (file.exists()) {
            // 文件存在
            mUiChange.playVideo.setValue(file.getPath());
            Log.d(VIDEO_TAG, "binding.videoView.start(): " + file.getPath());
        } else {
            // 如果没有视频，则显示图片
            playImageScreen();
        }
    }

    /**
     * 播放图片xx秒
     */
    public void playImageScreen() {
        mUiChange.showImgScreen.call();
        Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                        accept(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Long aLong) {
                        playVideo();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    // endregion

    // region 下载屏保

    /**
     * 下载屏保
     * 每隔30分钟下载
     * 下载文件以当前时间戳文件新增覆盖，然后video赋新值
     * <p>
     * 1. 记录当前屏保的视频文件名称，下载好的视频文件名称
     * 2. 下载好了，添加，视频设置地址
     * 3. 删除所有除了下载好的，当前屏保的视频文件
     * <p>
     * 测试一直播放一直下载会不会有什么问题
     * https://img.huoyunji.com/audio_20190221105823_Android_28360
     * https://img.huoyunji.com/video_20190221105749_Android_31228
     */
    public void downloadScreen() {
        List<String> urlVideos = new ArrayList<>();
        urlVideos.add("https://www.w3school.com.cn/example/html5/mov_bbb.mp4");
        urlVideos.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        urlVideos.add("http://vjs.zencdn.net/v/oceans.mp4");
        urlVideos.add("http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4");
        urlVideos.add("https://img.huoyunji.com/video_20190221105749_Android_31228");
        List<String> urlImages = new ArrayList<>();
        urlImages.add("https://img.huoyunji.com/photo_20190221105726_Android_15181?imageMogr2/auto-orient/thumbnail/!280x280r/gravity/Center/crop/280x280/format/jpg/interlace/1/blur/1x0/quality/90");
        urlImages.add("https://img.huoyunji.com/photo_20190221105418_Android_47466?imageMogr2/auto-orient/thumbnail/!280x280r/gravity/Center/crop/280x280/format/jpg/interlace/1/blur/1x0/quality/90");
        urlImages.add("https://img.huoyunji.com/photo_20190221105418_Android_47466?imageMogr2/auto-orient/thumbnail/!280x280r/gravity/Center/crop/280x280/format/jpg/interlace/1/blur/1x0/quality/90");
        urlImages.add("https://img.huoyunji.com/photo_20190221105418_Android_47466?imageMogr2/auto-orient/thumbnail/!280x280r/gravity/Center/crop/280x280/format/jpg/interlace/1/blur/1x0/quality/90");
        urlImages.add("https://img.huoyunji.com/photo_20190221105418_Android_47466?imageMogr2/auto-orient/thumbnail/!280x280r/gravity/Center/crop/280x280/format/jpg/interlace/1/blur/1x0/quality/90");
        Observable.interval(0, 1, TimeUnit.MINUTES)
                .doOnNext(aLong -> {
                    if (mIsDownloadInVideo) {
                        // 如果下载中就直接返回
                        return;
                    }
                    int index = (int) (Math.random() * urlVideos.size());
                    Log.d(VIDEO_TAG, "下载 ：" + index);
                    downloadVideo(urlVideos.get(index));
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        accept(d);
                    }

                    @Override
                    public void onNext(@NotNull Long value) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        Observable.interval(0, 1, TimeUnit.MINUTES)
                .doOnNext(aLong -> {
                    if (mIsDownloadInImage) {
                        // 如果下载中就直接返回
                        return;
                    }
                    int index = (int) (Math.random() * urlImages.size());
                    Log.d(IMAGE_TAG, "下载 ：" + index);
                    downloadImage(urlImages.get(index));
                })
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        accept(d);
                    }

                    @Override
                    public void onNext(@NotNull Long value) {

                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * 下载视频
     */
    public void downloadVideo(String url) {
        if (mIsDownloadInVideo) {
            return;
        }
        // 文件名
        String fileName = System.currentTimeMillis() + ".mp4";
        // 创建文件夹
        File videoDir = FilePaths.videoDir(mApplication);
        // 判断id是否存在
        mDownloadHelperVideo.downloadFile(url, videoDir.getPath(), fileName);
    }

    /**
     * 下载图片
     */
    public void downloadImage(String url) {
        if (mIsDownloadInImage) {
            return;
        }
        // 文件名
        String fileName = System.currentTimeMillis() + ".jpg";
        // 创建文件夹
        File imageDir = FilePaths.imageDir(mApplication);
        // 判断id是否存在
        mDownloadHelperImage.downloadFile(url, imageDir.getPath(), fileName);
    }

    // endregion

    /**
     * 上传记录明细
     */
    private void updateRecordDetail() {
        Observable<String> observable = Observable.create(emitter -> {
            // 人脸图片 mFaceBitmap
            mFaceBitmap.recycle();
            mFaceBitmap = null;

            // 音频文件
            File file = new File(FilePaths.recordDir(mApplication).getPath() + File.separator + mRecordManager.recordName);

            emitter.onNext(file.getPath());
            //发送完成
            emitter.onComplete();
        });

        // 2.观察者
        Observer<String> observer = new Observer<String>() {

            @Override
            public void onSubscribe(@NotNull Disposable d) {
                accept(d);
            }

            // 接收发送的消息
            @Override
            public void onNext(@NotNull String path) {
                ToastUtils.showShort("上传成功，音频地址" + path);
            }

            // 接受异常通知
            @Override
            public void onError(@NotNull Throwable e) {
            }

            // 接受发送完毕通知
            @Override
            public void onComplete() {

            }
        };

        // 3.观察者订阅被观察者
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


}
