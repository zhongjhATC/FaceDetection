package com.baidu.idl.main.facesdk.identifylibrary.testimony;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.identifylibrary.BaseActivity;
import com.baidu.idl.main.facesdk.identifylibrary.R;
import com.baidu.idl.main.facesdk.identifylibrary.callback.CameraDataCallback;
import com.baidu.idl.main.facesdk.identifylibrary.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.identifylibrary.camera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.identifylibrary.camera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.identifylibrary.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.identifylibrary.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.identifylibrary.model.LivenessModel;
import com.baidu.idl.main.facesdk.identifylibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.identifylibrary.setting.IdentifySettingActivity;
import com.baidu.idl.main.facesdk.identifylibrary.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.DensityUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.identifylibrary.utils.ImageUtils;
import com.baidu.idl.main.facesdk.identifylibrary.utils.ToastUtils;
import com.baidu.idl.main.facesdk.identifylibrary.view.PreviewTexture;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

import java.io.FileNotFoundException;

import static android.content.ContentValues.TAG;

public class FaceIRTestimonyActivity extends BaseActivity implements View.OnClickListener {
    private static final int PICK_PHOTO_FRIST = 100;
    private static final int PICK_VIDEO_FRIST = 101;

    private volatile boolean firstFeatureFinished = false;
    private volatile boolean secondFeatureFinished = false;

    private byte[] firstFeature = new byte[512];
    private byte[] secondFeature = new byte[512];

    private Context mContext;
    private RelativeLayout livenessRl;
    private RectF rectF;
    private Paint paint;
    private Paint paintBg;
    // 摄像头个数
    private int mCameraNum;
    // RGB+IR 控件
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;
    private AutoTexturePreviewView mPreviewView;
    private ImageView testImageview;
    private TextureView mDrawDetectFaceView;
    private ImageView testimonyPreviewLineIv;
    private ImageView testimonyDevelopmentLineIv;
    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGH = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    // 判断摄像头数据源
    private int camemra1DataMean;
    private int camemra2DataMean;
    private volatile boolean camemra1IsRgb = false;
    // 摄像头采集数据
    private volatile byte[] rgbData;
    private volatile byte[] irData;
    private RelativeLayout livenessAgainRl;
    private ImageView livenessAddIv;
    private TextView livenessUpdateTv;
    private ImageView livenessShowIv;
    private ImageView hintShowIv;
    private TextView tv_nir_live_score;
    private RelativeLayout livenessTipsFailRl;
    private TextView livenessTipsFailTv;
    private TextView livenessTipsPleaseFailTv;
    private TextView tv_nir_live_time;
    private TextureView irTexture;
    float score = 0;
    private TextView testimonyDevelopmentTv;
    private TextView testimonyPreviewTv;

    // 定义一个变量判断是预览模式还是开发模式
    boolean isDevelopment = false;
    private RelativeLayout livenessButtomLl;
    private RelativeLayout kaifaRelativeLayout;
    private RelativeLayout test_nir_rl;
    private TextView hintAdainTv;
    private TextView livenessBaiduTv;
    private View view;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private ImageView test_nir_iv;
    private ImageView test_rgb_iv;
    private TextView tv_feature_time;
    private TextView tv_feature_search_time;
    private TextView tv_all_time;
    private RelativeLayout hintShowRl;
    private RelativeLayout developmentAddRl;
    private float rgbLiveScore;
    private float nirLiveScore;
    // 判断是否有人脸
    private boolean isFace = false;
    private ImageView livenessTipsFailIv;
    private float nirLivenessScore = 0.0f;
    private float rgbLivenessScore = 0.0f;
    // 特征比对
    private long endCompareTime;
    // 特征提取
    private long featureTime;
    // rgb
    private RelativeLayout testRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_face_ir_identifylibrary);
        mContext = this;
        initView();

        // 屏幕的宽
        int displayWidth = DensityUtils.getDisplayWidth(mContext);
        // 屏幕的高
        int displayHeight = DensityUtils.getDisplayHeight(mContext);
        // 当屏幕的宽大于屏幕宽时
        if (displayHeight < displayWidth) {
            // 获取高
            int height = displayHeight;
            // 获取宽
            int width = (int) (displayHeight * ((9.0f / 16.0f)));
            // 设置布局的宽和高
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            // 设置布局居中
            params.gravity = Gravity.CENTER;
            livenessRl.setLayoutParams(params);
        }
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
                    ToastUtils.toast(FaceIRTestimonyActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(FaceIRTestimonyActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    private void initView() {
        // 获取整个布局
        livenessRl = findViewById(R.id.liveness_Rl);
        // 画人脸框
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        // AutoTexturePreviewView
        mPreviewView = findViewById(R.id.detect_ir_image_view);
        // 双目摄像头IR 图像预览
        irTexture = findViewById(R.id.texture_preview_ir);
        if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == 1) {
            irTexture.setRotationY(180);
        }
        // 不需要屏幕自动变黑
        mDrawDetectFaceView = findViewById(R.id.texture_view_draw);
        mDrawDetectFaceView.setKeepScreenOn(true);
        mDrawDetectFaceView.setOpaque(false);
        // 百度
        livenessBaiduTv = findViewById(R.id.liveness_baiduTv);
        // view
        view = findViewById(R.id.mongolia_view);
        // RGB 阈值
        rgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // Live 阈值
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        /* title */
        // 返回
        ImageView testimony_backIv = findViewById(R.id.btn_back);
        testimony_backIv.setOnClickListener(this);
        // 预览模式
        testimonyPreviewTv = findViewById(R.id.preview_text);
        testimonyPreviewTv.setOnClickListener(this);
        testimonyPreviewLineIv = findViewById(R.id.preview_view);
        // 开发模式
        testimonyDevelopmentTv = findViewById(R.id.develop_text);
        testimonyDevelopmentTv.setOnClickListener(this);
        testimonyDevelopmentLineIv = findViewById(R.id.develop_view);
        // 设置
        ImageView testimonySettingIv = findViewById(R.id.btn_setting);
        testimonySettingIv.setOnClickListener(this);

        // ****************开发模式****************
        // RGB
        testImageview = findViewById(R.id.test_rgb_ir_view);
        test_rgb_iv = findViewById(R.id.test_rgb_iv);
        testRelativeLayout = findViewById(R.id.test_rgb_rl);
        testRelativeLayout.setVisibility(View.GONE);
        // 图片显示
        hintShowIv = findViewById(R.id.hint_showIv);
        // 重新上传
        hintAdainTv = findViewById(R.id.hint_adainTv);
        hintAdainTv.setOnClickListener(this);
        hintShowRl = findViewById(R.id.hint_showRl);
        // 上传图片
        ImageView DevelopmentAddIv = findViewById(R.id.Development_addIv);
        DevelopmentAddIv.setOnClickListener(this);
        developmentAddRl = findViewById(R.id.Development_addRl);
        // nir
        test_nir_rl = findViewById(R.id.test_nir_Rl);
        test_nir_rl.setVisibility(View.GONE);
        test_nir_iv = findViewById(R.id.test_nir_iv);
        // 提示
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        textCompareStatus = findViewById(R.id.text_compare_status);
        // 相似度分数
        tv_nir_live_time = findViewById(R.id.tv_rgb_live_time);
        // 活体检测耗时
        tv_nir_live_score = findViewById(R.id.tv_rgb_live_score);
        // 特征抽取耗时
        tv_feature_time = findViewById(R.id.tv_feature_time);
        // 特征比对耗时
        tv_feature_search_time = findViewById(R.id.tv_feature_search_time);
        // 总耗时
        tv_all_time = findViewById(R.id.tv_all_time);

        // ****************预览模式****************
        // 未通过提示
        livenessTipsFailRl = findViewById(R.id.testimony_tips_failRl);
        livenessTipsFailTv = findViewById(R.id.testimony_tips_failTv);
        livenessTipsPleaseFailTv = findViewById(R.id.testimony_tips_please_failTv);
        livenessTipsFailIv = findViewById(R.id.testimony_tips_failIv);
        // 预览模式buttom
        livenessButtomLl = findViewById(R.id.person_buttomLl);
        kaifaRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        livenessAddIv = findViewById(R.id.testimony_addIv);
        livenessAddIv.setOnClickListener(this);
        livenessUpdateTv = findViewById(R.id.testimony_upload_filesTv);
        livenessAgainRl = findViewById(R.id.testimony_showRl);
        livenessShowIv = findViewById(R.id.testimony_showImg);
        TextView livenessAgainTv = findViewById(R.id.testimony_showAgainTv);
        livenessAgainTv.setOnClickListener(this);

        // 双摄像头
        mCameraNum = Camera.getNumberOfCameras();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        } else {
            mPreview = new PreviewTexture[mCameraNum];
            mCamera = new Camera[mCameraNum];
            mPreview[1] = new PreviewTexture(this, irTexture);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                startTestCloseDebugRegisterFunction();
                mCamera[1] = Camera.open(1);
                mPreview[1].setCamera(mCamera[1], PREFER_WIDTH, PERFER_HEIGH);
                mCamera[1].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        dealIr(data);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void startTestCloseDebugRegisterFunction() {
        // 设置前置摄像头
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        // 设置后置摄像头
        //  CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK);
        // 设置USB摄像头
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);

        CameraPreviewManager.getInstance().startPreview(this, mPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        // 摄像头预览数据进行人脸检测
                        dealRgb(data);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
//      CameraPreviewManager.getInstance().stopPreview();
        if (mCameraNum >= 2) {
            for (int i = 0; i < mCameraNum; i++) {
                if (mCameraNum >= 2) {
                    if (mCamera[i] != null) {
                        mCamera[i].setPreviewCallback(null);
                        mCamera[i].stopPreview();
                        mPreview[i].release();
                        mCamera[i].release();
                        mCamera[i] = null;
                    }
                }
            }
        }
        super.onPause();
    }

    private void dealRgb(byte[] data) {
        rgbData = data;
        checkData();
    }

    private void dealIr(byte[] data) {
        irData = data;
        checkData();
    }

    private synchronized void checkData() {
        if (rgbData != null && irData != null) {
            if (livenessShowIv.getDrawable() != null || hintShowIv.getDrawable() != null) {
                firstFeatureFinished = false;
                FaceSDKManager.getInstance().onDetectCheck(rgbData, irData, null,
                        PERFER_HEIGH, PREFER_WIDTH, 2, new FaceDetectCallBack() {
                            @Override
                            public void onFaceDetectCallback(final LivenessModel livenessModel) {
                                // 预览模式
                                checkCloseDebugResult(livenessModel);
                                // 开发模式
                                checkOpenDebugResult(livenessModel);
                            }

                            @Override
                            public void onTip(int code, String msg) {

                            }

                            @Override
                            public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                showFrame(livenessModel);
                            }
                        });

                rgbData = null;
                irData = null;
            } else {
                testImageview.setImageResource(R.mipmap.ic_image_video);
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0.85f, 0.0f);
                animator.setDuration(3000);
                view.setBackgroundColor(Color.parseColor("#ffffff"));
                animator.start();
            }
        }
    }

    // 预览模式
    private void checkCloseDebugResult(final LivenessModel livenessModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null) {
                    livenessTipsFailRl.setVisibility(View.GONE);

                    if (testimonyPreviewLineIv.getVisibility() == View.VISIBLE) {
                        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0.85f, 0.0f);
                        animator.setDuration(3000);
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                view.setBackgroundColor(Color.parseColor("#ffffff"));
                            }
                        });
                        animator.start();
                    }

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            long startCompareTime = System.currentTimeMillis();
                            score = FaceSDKManager.getInstance().getFaceFeature().featureCompare(
                                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,
                                    livenessModel.getFeature() , secondFeature, true);
                            endCompareTime = System.currentTimeMillis() - startCompareTime;
                            if (isDevelopment == false) {
                                layoutCompareStatus.setVisibility(View.GONE);
                                livenessTipsFailRl.setVisibility(View.VISIBLE);
                                if (isFace == true) {
                                    livenessTipsFailTv.setText("上传图片不包含人脸");
                                    livenessTipsFailTv.setTextColor(Color.parseColor("#FFFEC133"));
                                    livenessTipsPleaseFailTv.setText("无法进行人证比对");
                                    livenessTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                } else {
                                    rgbLivenessScore = livenessModel.getRgbLivenessScore();
                                    nirLivenessScore = livenessModel.getIrLivenessScore();
                                    if (rgbLivenessScore > rgbLiveScore && nirLivenessScore >
                                            nirLiveScore) {
                                        if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                                            livenessTipsFailTv.setText("人证核验通过");
                                            livenessTipsFailTv.setTextColor(
                                                    Color.parseColor("#FF00BAF2"));
                                            livenessTipsPleaseFailTv.setText("识别成功");
                                            livenessTipsFailIv.setImageResource(R.mipmap.tips_success);
                                        } else {
                                            livenessTipsFailTv.setText("人证核验未通过");
                                            livenessTipsFailTv.setTextColor(
                                                    Color.parseColor("#FFFEC133"));
                                            livenessTipsPleaseFailTv.setText("请上传正面人脸照片");
                                            livenessTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                        }
                                    } else {
                                        livenessTipsFailTv.setText("人证核验未通过");
                                        livenessTipsFailTv.setTextColor(Color.parseColor("#FFFEC133"));
                                        livenessTipsPleaseFailTv.setText("请上传正面人脸照片");
                                        livenessTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    // 开发模式
    private void checkOpenDebugResult(final LivenessModel model) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (model != null) {
                    BDFaceImageInstance image = model.getBdFaceImageInstance();
                    if (image != null) {
                        testImageview.setImageBitmap(BitmapUtils.getInstaceBmp(image));
                    }

                    tv_nir_live_time.setText(String.format("相似度分数：%s", score));
                    tv_nir_live_score.setText(String.format("活体检测耗时：%s ms", model.getIrLivenessDuration()));

                    //  比较两个人脸
                    if (firstFeature == null || secondFeature == null) {
                        return;
                    }

                    if (rgbLivenessScore < rgbLiveScore || nirLivenessScore < nirLiveScore) {
                        tv_feature_time.setText(String.format("特征抽取耗时：%s ms", 0));
                        tv_feature_search_time.setText(String.format("特征比对耗时：%s ms", 0));
                        long l = model.getRgbDetectDuration() + model.getIrLivenessDuration();
                        tv_all_time.setText(String.format("总耗时：%s ms", l));
                    } else {
                        tv_feature_time.setText(String.format("特征抽取耗时：%s ms", featureTime));
                        tv_feature_search_time.setText(String.format("特征比对耗时：%s ms",
                                endCompareTime));
                        long l = model.getRgbDetectDuration() + model.
                                getIrLivenessDuration() + featureTime + endCompareTime;
                        tv_all_time.setText(String.format("总耗时：%s ms", l));
                    }

                    if (isDevelopment) {
                        livenessTipsFailRl.setVisibility(View.GONE);
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        rgbLivenessScore = model.getRgbLivenessScore();
                        nirLivenessScore = model.getIrLivenessScore();
                        if (nirLivenessScore < nirLiveScore) {
                            test_nir_iv.setVisibility(View.VISIBLE);
                            test_nir_iv.setImageResource(R.mipmap.ic_icon_develop_fail);
                        } else {
                            test_nir_iv.setVisibility(View.VISIBLE);
                            test_nir_iv.setImageResource(R.mipmap.ic_icon_develop_success);
                        }
                        if (rgbLivenessScore < rgbLiveScore) {
                            test_rgb_iv.setVisibility(View.VISIBLE);
                            test_rgb_iv.setImageResource(R.mipmap.ic_icon_develop_fail);
                        } else {
                            test_rgb_iv.setVisibility(View.VISIBLE);
                            test_rgb_iv.setImageResource(R.mipmap.ic_icon_develop_success);
                        }
                    } else {
                        test_rgb_iv.setVisibility(View.VISIBLE);
                        test_rgb_iv.setImageResource(R.mipmap.ic_icon_develop_success);
                    }

                    if (rgbLivenessScore > rgbLiveScore && nirLivenessScore > nirLiveScore) {
                        if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                            textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                            textCompareStatus.setText("比对成功");
                        } else {
                            textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                            textCompareStatus.setText("比对失败");
                        }
                    } else {
                        textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                        textCompareStatus.setText("比对失败");
                    }

                } else {
                    layoutCompareStatus.setVisibility(View.GONE);
                    test_nir_iv.setVisibility(View.GONE);
                    test_rgb_iv.setVisibility(View.GONE);
                    // 开发模式
                    testImageview.setImageResource(R.mipmap.ic_image_video);
                    tv_nir_live_time.setText(String.format("相似度分数：%s", 0));
                    tv_nir_live_score.setText(String.format("活体检测耗时：%s ms", 0));
                    tv_feature_time.setText(String.format("特征抽取耗时：%s ms", 0));
                    tv_feature_search_time.setText(String.format("特征比对耗时：%s ms", 0));
                    tv_all_time.setText(String.format("总耗时：%s ms", 0));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_back) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
            // 预览模式
        } else if (id == R.id.preview_text) {
            isDevelopment = false;
            if (livenessShowIv.getDrawable() != null || hintShowIv.getDrawable() != null) {
                livenessTipsFailRl.setVisibility(View.VISIBLE);
                layoutCompareStatus.setVisibility(View.GONE);
            } else {
                livenessTipsFailRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.GONE);
            }
            testimonyPreviewLineIv.setVisibility(View.VISIBLE);
            testimonyDevelopmentLineIv.setVisibility(View.GONE);
            testimonyDevelopmentTv.setTextColor(Color.parseColor("#FF999999"));
            testimonyPreviewTv.setTextColor(getResources().getColor(R.color.white));
            test_nir_rl.setVisibility(View.GONE);
            livenessButtomLl.setVisibility(View.VISIBLE);
            kaifaRelativeLayout.setVisibility(View.GONE);
            livenessBaiduTv.setVisibility(View.VISIBLE);
//                test_nir_view.setVisibility(View.GONE);
            testRelativeLayout.setVisibility(View.GONE);
            irTexture.setAlpha(0);
            testImageview.setVisibility(View.GONE);
            // 开发模式
        } else if (id == R.id.develop_text) {
            if (livenessShowIv.getDrawable() != null || hintShowIv.getDrawable() != null) {
                livenessTipsFailRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.VISIBLE);
            } else {
                livenessTipsFailRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.GONE);
            }
            isDevelopment = true;
            testimonyPreviewLineIv.setVisibility(View.GONE);
            testimonyDevelopmentLineIv.setVisibility(View.VISIBLE);
            testimonyDevelopmentTv.setTextColor(getResources().getColor(R.color.white));
            testimonyPreviewTv.setTextColor(Color.parseColor("#FF999999"));
            test_nir_rl.setVisibility(View.VISIBLE);
            livenessButtomLl.setVisibility(View.GONE);
            kaifaRelativeLayout.setVisibility(View.VISIBLE);
            livenessBaiduTv.setVisibility(View.GONE);
            irTexture.setAlpha(1);
            testImageview.setVisibility(View.VISIBLE);
            testRelativeLayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.btn_setting) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(mContext, IdentifySettingActivity.class));
            finish();
        } else if (id == R.id.testimony_addIv) {
            secondFeatureFinished = false;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_PHOTO_FRIST);
        } else if (id == R.id.testimony_showAgainTv) {
            secondFeatureFinished = false;
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent2, PICK_PHOTO_FRIST);
        } else if (id == R.id.hint_adainTv) {
            secondFeatureFinished = false;
            Intent intent3 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent3, PICK_PHOTO_FRIST);
        } else if (id == R.id.Development_addIv) {
            secondFeatureFinished = false;
            Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent4, PICK_PHOTO_FRIST);
        }
    }


    private synchronized void rgbOrIr(int index, byte[] data) {
        byte[] tmp = new byte[PREFER_WIDTH * PERFER_HEIGH];
        try {
            System.arraycopy(data, 0, tmp, 0, PREFER_WIDTH * PERFER_HEIGH);
        } catch (NullPointerException e) {
            Log.e("qing", String.valueOf(e.getStackTrace()));
        }
        int count = 0;
        int total = 0;
        for (int i = 0; i < PREFER_WIDTH * PERFER_HEIGH; i = i + 10) {
            total += byteToInt(tmp[i]);
            count++;
        }

        if (count == 0) {
            return;
        }

        if (index == 0) {
            camemra1DataMean = total / count;
        } else {
            camemra2DataMean = total / count;
        }
        if (camemra1DataMean != 0 && camemra2DataMean != 0) {
            if (camemra1DataMean > camemra2DataMean) {
                camemra1IsRgb = true;
            } else {
                camemra1IsRgb = false;
            }
        }
    }

    public int byteToInt(byte b) {
        // Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    private void choiceRgbOrIrType(int index, byte[] data) {
        // camera1如果为rgb数据，调用dealRgb，否则为Ir数据，调用Ir
        if (index == 0) {
            if (camemra1IsRgb) {
                dealRgb(data);
            } else {
                dealIr(data);
            }
        } else {
            if (camemra1IsRgb) {
                dealIr(data);
            } else {
                dealRgb(data);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_FRIST && (data != null && data.getData() != null)) {
            Uri uri1 = ImageUtils.geturi(data, this);
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
                if (bitmap != null) {
                    // 提取特征值
//                    syncFeature(bitmap, secondFeature, 2, true);
                    float ret = FaceSDKManager.getInstance().personDetect(bitmap, secondFeature, this);
                    livenessShowIv.setVisibility(View.VISIBLE);
                    hintShowIv.setVisibility(View.VISIBLE);
                    livenessShowIv.setImageBitmap(bitmap);
                    hintShowIv.setImageBitmap(bitmap);
                    if (ret != -1) {
                        isFace = false;
                        // 判断质量检测，针对模糊度、遮挡、角度
                        if (ret == 128) {
                            secondFeatureFinished = true;
                        }
                        if (ret == 128) {
                            toast("图片特征抽取成功");
                            hintShowIv.setVisibility(View.VISIBLE);
                            livenessShowIv.setVisibility(View.VISIBLE);
                            hintShowRl.setVisibility(View.VISIBLE);
                            livenessAgainRl.setVisibility(View.VISIBLE);
                            livenessAddIv.setVisibility(View.GONE);
                            livenessUpdateTv.setVisibility(View.GONE);
                            developmentAddRl.setVisibility(View.GONE);
                        } else {
                            ToastUtils.toast(mContext, "图片特征抽取失败");
                        }
                    } else {
                        isFace = true;
                        isFace = true;
                        // 上传图片无人脸隐藏
                        livenessShowIv.setVisibility(View.GONE);
                        hintShowIv.setVisibility(View.GONE);
                        livenessAddIv.setVisibility(View.GONE);
                        livenessUpdateTv.setVisibility(View.GONE);
                        livenessAgainRl.setVisibility(View.VISIBLE);
                        hintShowIv.setVisibility(View.GONE);
                        livenessShowIv.setVisibility(View.GONE);
                        hintShowRl.setVisibility(View.VISIBLE);
                        developmentAddRl.setVisibility(View.GONE);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * bitmap -提取特征值
     *
     * @param bitmap
     * @param feature
     * @param index
     */
    private void syncFeature(final Bitmap bitmap, final byte[] feature, final int index, boolean isFromPhotoLibrary) {
        float ret = -1;
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);

        FaceInfo[] faceInfos = null;
        int count = 10;
        // 现在人脸检测加入了防止多线程重入判定，假如之前线程人脸检测未完成，本次人脸检测有可能失败，需要多试几次
        while (count != 0) {
            faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                    .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
            count--;
            if (faceInfos != null) {
                break;
            } else {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // 检测结果判断
        if (faceInfos != null && faceInfos.length > 0) {
            isFace = false;
            // 上传图片有人脸显示
            livenessShowIv.setVisibility(View.VISIBLE);
            hintShowIv.setVisibility(View.VISIBLE);
            // 判断质量检测，针对模糊度、遮挡、角度
            ret = FaceSDKManager.getInstance().getFaceFeature().feature(BDFaceSDKCommon.FeatureType.
                    BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
            if (ret == 128 && index == 2) {
                secondFeatureFinished = true;
            }
            if (ret == 128) {
                toast("图片" + index + "特征抽取成功");
                hintShowIv.setVisibility(View.VISIBLE);
                livenessShowIv.setVisibility(View.VISIBLE);
                hintShowRl.setVisibility(View.VISIBLE);
                livenessAgainRl.setVisibility(View.VISIBLE);
                livenessAddIv.setVisibility(View.GONE);
                livenessUpdateTv.setVisibility(View.GONE);
                developmentAddRl.setVisibility(View.GONE);
            } else {
                toast("图片二特征抽取失败");
            }
        } else {
            isFace = true;
            // 上传图片无人脸隐藏
            livenessShowIv.setVisibility(View.GONE);
            hintShowIv.setVisibility(View.GONE);
            livenessAddIv.setVisibility(View.GONE);
            livenessUpdateTv.setVisibility(View.GONE);
            livenessAgainRl.setVisibility(View.VISIBLE);
            hintShowIv.setVisibility(View.GONE);
            livenessShowIv.setVisibility(View.GONE);
            hintShowRl.setVisibility(View.VISIBLE);
            developmentAddRl.setVisibility(View.GONE);
        }
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 绘制人脸框
     */
    private void showFrame(final LivenessModel model) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Canvas canvas = mDrawDetectFaceView.lockCanvas();
                if (canvas == null) {
                    mDrawDetectFaceView.unlockCanvasAndPost(canvas);
                    return;
                }
                if (model == null) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mDrawDetectFaceView.unlockCanvasAndPost(canvas);
                    return;
                }
                FaceInfo[] faceInfos = model.getTrackFaceInfo();
                if (faceInfos == null || faceInfos.length == 0) {
                    // 清空canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mDrawDetectFaceView.unlockCanvasAndPost(canvas);
                    return;
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                FaceInfo faceInfo = faceInfos[0];

                rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));
                // 检测图片的坐标和显示的坐标不一样，需要转换。
                FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF,
                        mPreviewView, model.getBdFaceImageInstance());
                if (score < SingleBaseConfig.getBaseConfig().getIdThreshold()
                        || rgbLivenessScore < SingleBaseConfig.getBaseConfig().getRgbLiveScore()
                        || nirLivenessScore < SingleBaseConfig.getBaseConfig().getNirLiveScore()) {
                    paint.setColor(Color.parseColor("#FEC133"));
                    paintBg.setColor(Color.parseColor("#FEC133"));
                } else {
                    if (isFace == true) {
                        paint.setColor(Color.parseColor("#FEC133"));
                        paintBg.setColor(Color.parseColor("#FEC133"));
                    } else {
                        paint.setColor(Color.parseColor("#00baf2"));
                        paintBg.setColor(Color.parseColor("#00baf2"));
                    }
                }
                paint.setStyle(Paint.Style.STROKE);
                paintBg.setStyle(Paint.Style.STROKE);
                // 画笔粗细
                paint.setStrokeWidth(8);
                // 设置线条等图形的抗锯齿
                paint.setAntiAlias(true);
                paintBg.setStrokeWidth(13);
                paintBg.setAlpha(90);
                // 设置线条等图形的抗锯齿
                paintBg.setAntiAlias(true);
                if (faceInfo.width > faceInfo.height) {
                    if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                        canvas.drawCircle(mPreviewView.getWidth() - rectF.centerX(),
                                rectF.centerY(), rectF.width() / 2 - 8, paintBg);
                        canvas.drawCircle(mPreviewView.getWidth() - rectF.centerX(),
                                rectF.centerY(), rectF.width() / 2, paint);
                    } else {
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2 - 8, paintBg);
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2, paint);
                    }

                } else {
                    if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                        canvas.drawCircle(mPreviewView.getWidth() - rectF.centerX(),
                                rectF.centerY(), rectF.height() / 2 - 8, paintBg);
                        canvas.drawCircle(mPreviewView.getWidth() - rectF.centerX(),
                                rectF.centerY(), rectF.height() / 2, paint);
                    } else {
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.height() / 2 - 8, paintBg);
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.height() / 2, paint);
                    }
                }
                // 清空canvas
                mDrawDetectFaceView.unlockCanvasAndPost(canvas);
            }
        });
    }
}
