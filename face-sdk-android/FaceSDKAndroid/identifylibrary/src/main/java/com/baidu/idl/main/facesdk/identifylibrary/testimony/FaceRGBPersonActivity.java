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
import com.baidu.idl.main.facesdk.identifylibrary.callback.FaceFeatureCallBack;
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
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

import java.io.FileNotFoundException;

public class FaceRGBPersonActivity extends BaseActivity implements View.OnClickListener {
    private Context mContext;
    private ImageView testimony_backIv;
    private ImageView testimony_settingIv;
    private ImageView testimony_addIv;

    private static final int PICK_PHOTO_FRIST = 100;
    private static final int PICK_VIDEO_FRIST = 101;

    private byte[] firstFeature = new byte[512];
    private byte[] secondFeature = new byte[512];

    private ImageView testimony_developmentLineIv;
    private TextView testimony_developmentTv;
    private ImageView testimony_previewLineIv;
    private TextView testimony_previewTv;
    private RelativeLayout testimony_rl;
    private RelativeLayout testimony_showRl;
    private ImageView testimony_showImg;
    private TextView testimony_showAgainTv;
    private TextView testimony_upload_filesTv;

    private volatile boolean firstFeatureFinished = false;
    private volatile boolean secondFeatureFinished = false;
    private RelativeLayout testimony_tips_failRl;

    private AutoTexturePreviewView mPreviewView;
    // RGB摄像头图像宽和高
    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int mWidth = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int mHeight = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    private int mLiveType;
    private TextureView mDrawDetectFaceView;
    private Paint paint;
    private RectF rectF;

    private RelativeLayout personButtomLl;
    private TextView person_baiduTv;
    private ImageView testImageview;
    private Paint paintBg;
    private TextView tv_rgb_live_time;
    private TextView tv_rgb_live_score;
    private RelativeLayout kaifa_relativeLayout;
    private TextView hintAdainIv;
    private ImageView hintShowIv;
    private TextView testimonyTipsFailTv;
    private TextView testimonyTipsPleaseFailTv;
    float score = 0;
    // 定义一个变量判断是预览模式还是开发模式
    boolean isDevelopment = false;
    private RelativeLayout testRelativeLayout;
    private View view;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private ImageView person_kaifaIv;
    private TextView tv_feature_time;
    private TextView tv_feature_search_time;
    private TextView tv_all_time;
    private ImageView developmentAddIv;
    private RelativeLayout hintShowRl;
    private RelativeLayout developmentAddRl;
    private float mRgbLiveScore;
    private ImageView testimonyTipsFailIv;
    // 判断是否有人脸
    private boolean isFace = false;
    private float rgbLivenessScore = 0.0f;
    // 特征比对
    private long endCompareTime;
    // 特征提取
    private long featureTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_face_rgb_identifylibrary);
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
            testimony_rl.setLayoutParams(params);
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
                    ToastUtils.toast(FaceRGBPersonActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(FaceRGBPersonActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    private void initView() {
        // 获取整个布局
        testimony_rl = findViewById(R.id.testimony_Rl);
        // 画人脸框
        paint = new Paint();
        rectF = new RectF();
        paintBg = new Paint();
        mDrawDetectFaceView = findViewById(R.id.texture_view_draw);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);
        // 单目摄像头RGB 图像预览
        mPreviewView = findViewById(R.id.auto_rgb_preview_view);
        // 返回
        testimony_backIv = findViewById(R.id.btn_back);
        testimony_backIv.setOnClickListener(this);
        // 设置
        testimony_settingIv = findViewById(R.id.btn_setting);
        testimony_settingIv.setOnClickListener(this);
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // buttom
        personButtomLl = findViewById(R.id.person_buttomLl);
        // 百度大脑技术支持
        person_baiduTv = findViewById(R.id.person_baiduTv);
        // 送检RGB 图像回显
        testImageview = findViewById(R.id.test_rgb_view);
        testRelativeLayout = findViewById(R.id.test_rgb_rl);
        testRelativeLayout.setVisibility(View.GONE);
        person_kaifaIv = findViewById(R.id.person_kaifaIv);
        view = findViewById(R.id.mongolia_view);

        // ****************预览模式****************
        testimony_previewTv = findViewById(R.id.preview_text);
        testimony_previewTv.setOnClickListener(this);
        testimony_previewLineIv = findViewById(R.id.preview_view);
        // 添加图库图片  +号添加
        testimony_addIv = findViewById(R.id.testimony_addIv);
        testimony_addIv.setOnClickListener(this);
        testimony_showRl = findViewById(R.id.testimony_showRl);
        testimony_showImg = findViewById(R.id.testimony_showImg);
        testimony_showAgainTv = findViewById(R.id.testimony_showAgainTv);
        testimony_showAgainTv.setOnClickListener(this);
        testimony_upload_filesTv = findViewById(R.id.testimony_upload_filesTv);
        // 失败提示
        testimony_tips_failRl = findViewById(R.id.testimony_tips_failRl);
        testimonyTipsFailTv = findViewById(R.id.testimony_tips_failTv);
        testimonyTipsPleaseFailTv = findViewById(R.id.testimony_tips_please_failTv);
        testimonyTipsFailIv = findViewById(R.id.testimony_tips_failIv);

        // ****************开发模式****************
        testimony_developmentTv = findViewById(R.id.develop_text);
        testimony_developmentTv.setOnClickListener(this);
        testimony_developmentLineIv = findViewById(R.id.develop_view);
        // 相似度分数
        tv_rgb_live_time = findViewById(R.id.tv_rgb_live_time);
        // 活体检测耗时
        tv_rgb_live_score = findViewById(R.id.tv_rgb_live_score);
        // 特征抽取耗时
        tv_feature_time = findViewById(R.id.tv_feature_time);
        // 特征比对耗时
        tv_feature_search_time = findViewById(R.id.tv_feature_search_time);
        // 总耗时
        tv_all_time = findViewById(R.id.tv_all_time);
        // 重新上传
        hintAdainIv = findViewById(R.id.hint_adainTv);
        hintAdainIv.setOnClickListener(this);
        // 图片展示
        hintShowIv = findViewById(R.id.hint_showIv);
        kaifa_relativeLayout = findViewById(R.id.kaifa_relativeLayout);
        // 提示
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        textCompareStatus = findViewById(R.id.text_compare_status);
        // 上传图片
        developmentAddIv = findViewById(R.id.Development_addIv);
        developmentAddIv.setOnClickListener(this);
        hintShowRl = findViewById(R.id.hint_showRl);
        developmentAddRl = findViewById(R.id.Development_addRl);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreview();
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
        } else if (id == R.id.btn_setting) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            // 跳转设置页面
            startActivity(new Intent(mContext, IdentifySettingActivity.class));
            finish();
            // 上传图片
        } else if (id == R.id.testimony_addIv) {
            secondFeatureFinished = false;
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_PHOTO_FRIST);
            // 开发模式
        } else if (id == R.id.develop_text) {
            isDevelopment = true;
            if (testimony_showImg.getDrawable() != null || hintShowIv.getDrawable() != null) {
                testimony_tips_failRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.VISIBLE);
            } else {
                testimony_tips_failRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.GONE);
            }
            // title显隐
            testimony_developmentLineIv.setVisibility(View.VISIBLE);
            testimony_previewLineIv.setVisibility(View.GONE);
            testimony_developmentTv.setTextColor(getResources().getColor(R.color.white));
            testimony_previewTv.setTextColor(Color.parseColor("#FF999999"));
            // 百度大脑技术支持隐藏
            person_baiduTv.setVisibility(View.GONE);
            // 预览模式显示buttom隐藏
            personButtomLl.setVisibility(View.GONE);
            // 开发模式显示buttom显示
            kaifa_relativeLayout.setVisibility(View.VISIBLE);
            // RGB 检测图片测试
            testRelativeLayout.setVisibility(View.VISIBLE);
            // 预览模式
        } else if (id == R.id.preview_text) {
            isDevelopment = false;
            if (testimony_showImg.getDrawable() != null || hintShowIv.getDrawable() != null) {
                testimony_tips_failRl.setVisibility(View.VISIBLE);
                layoutCompareStatus.setVisibility(View.GONE);
            } else {
                testimony_tips_failRl.setVisibility(View.GONE);
                layoutCompareStatus.setVisibility(View.GONE);
            }
            // title显隐
            testimony_developmentLineIv.setVisibility(View.GONE);
            testimony_previewLineIv.setVisibility(View.VISIBLE);
            testimony_developmentTv.setTextColor(Color.parseColor("#FF999999"));
            testimony_previewTv.setTextColor(getResources().getColor(R.color.white));
            // 百度大脑技术支持显示
            person_baiduTv.setVisibility(View.VISIBLE);
            // RGB 检测图片测试
            testRelativeLayout.setVisibility(View.GONE);
            // 预览模式显示buttom显示
            personButtomLl.setVisibility(View.VISIBLE);
            // 开发模式显示buttom隐藏
            kaifa_relativeLayout.setVisibility(View.GONE);
            // 预览模式重新上传
        } else if (id == R.id.testimony_showAgainTv) {
            secondFeatureFinished = false;
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent2, PICK_PHOTO_FRIST);
            // 上传图片
        } else if (id == R.id.hint_adainTv) {
            secondFeatureFinished = false;
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent1, PICK_PHOTO_FRIST);
            // 开发模式重新上传
        } else if (id == R.id.Development_addIv) {
            secondFeatureFinished = false;
            Intent intent3 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent3, PICK_PHOTO_FRIST);
        }
    }

    /**
     * 摄像头图像预览
     */
    private void startCameraPreview() {
        // 设置前置摄像头
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        // 设置后置摄像头
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK);
        // 设置USB摄像头
        // TODO ： 临时放置
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        CameraPreviewManager.getInstance().startPreview(this, mPreviewView, mWidth, mHeight, new CameraDataCallback() {
            @Override
            public void onGetCameraData(final byte[] data, Camera camera, final int width, final int height) {
                // 预览模式或者开发模式上传图片成功开始
                if (testimony_showImg.getDrawable() != null || hintShowIv.getDrawable() != null) {
                    firstFeatureFinished = false;
                    // rgb回显图显示
//                    testImageview.setVisibility(View.VISIBLE);
                    // 拿到相机帧数据
                    // 摄像头预览数据进行人脸检测
                    FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                            height, width, mLiveType, new FaceDetectCallBack() {
                                @Override
                                public void onFaceDetectCallback(final LivenessModel livenessModel) {
                                    // 预览模式
                                    checkCloseDebugResult(livenessModel);
                                    // 开发模式
                                    checkOpenDebugResult(livenessModel);
                                }

                                @Override
                                public void onTip(int code, final String msg) {

                                }

                                @Override
                                public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                    // 人脸框显示
                                    showFrame(livenessModel);
                                }
                            });


                } else {
                    // 如果开发模式或者预览模式没上传图片则显示蒙层
                    testImageview.setImageResource(R.mipmap.ic_image_video);
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0.85f, 0.0f);
                    animator.setDuration(3000);
                    view.setBackgroundColor(Color.parseColor("#ffffff"));
                    animator.start();
                }
//                // 调试模式打开 显示实际送检图片的方向，SDK只检测人脸朝上的图
//                boolean isRGBDisplay = SingleBaseConfig.getBaseConfig().getDisplay();
//                if (isRGBDisplay) {
//                    showDetectImage(data);
//                }
            }
        });
    }

    /**
     * 显示检测的图片。用于调试，如果人脸sdk检测的人脸需要朝上，可以通过该图片判断。实际应用中可注释掉
     *
     * @param rgb
     */
    private void showDetectImage(byte[] rgb) {
        if (rgb == null) {
            return;
        }
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(rgb, mHeight,
                mWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                SingleBaseConfig.getBaseConfig().getDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorRGB());
        BDFaceImageInstance imageInstance = rgbInstance.getImage();
        final Bitmap bitmap = BitmapUtils.getInstaceBmp(imageInstance);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                testImageview.setVisibility(View.VISIBLE);
                testImageview.setImageBitmap(bitmap);
            }
        });
        // 流程结束销毁图片，开始下一帧图片检测，否则内存泄露
        rgbInstance.destory();
    }

    // 预览模式
    private void checkCloseDebugResult(final LivenessModel model) {
        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (model == null) {
                    // 提示隐藏
                    testimony_tips_failRl.setVisibility(View.GONE);
                    if (testimony_previewLineIv.getVisibility() == View.VISIBLE) {
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
                                    model.getFeature(), secondFeature, true);
                            // 特征比对
                            endCompareTime = System.currentTimeMillis() - startCompareTime;

                            if (isDevelopment == false) {
                                layoutCompareStatus.setVisibility(View.GONE);
                                testimony_tips_failRl.setVisibility(View.VISIBLE);
                                if (isFace == true) {
                                    testimonyTipsFailTv.setText("上传图片不包含人脸");
                                    testimonyTipsFailTv.setTextColor(Color.parseColor("#FFFEC133"));
                                    testimonyTipsPleaseFailTv.setText("无法进行人证比对");
                                    testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                } else {
                                    if (mLiveType == 0) {
                                        if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                                            testimonyTipsFailTv.setText("人证核验通过");
                                            testimonyTipsFailTv.setTextColor(
                                                    Color.parseColor("#FF00BAF2"));
                                            testimonyTipsPleaseFailTv.setText("识别成功");
                                            testimonyTipsFailIv.setImageResource(R.mipmap.tips_success);
                                        } else {
                                            testimonyTipsFailTv.setText("人证核验未通过");
                                            testimonyTipsFailTv.setTextColor(
                                                    Color.parseColor("#FFFEC133"));
                                            testimonyTipsPleaseFailTv.setText("请上传正面人脸照片");
                                            testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                        }
                                    } else {
                                        // 活体阈值判断显示
                                        rgbLivenessScore = model.getRgbLivenessScore();
                                        if (rgbLivenessScore < mRgbLiveScore) {
                                            testimonyTipsFailTv.setText("人证核验未通过");
                                            testimonyTipsFailTv.setTextColor(
                                                    Color.parseColor("#FFFEC133"));
                                            testimonyTipsPleaseFailTv.setText("请上传正面人脸照片");
                                            testimonyTipsFailIv.setImageResource(R.mipmap.tips_fail);
                                        } else {
                                            if (score > SingleBaseConfig.getBaseConfig()
                                                    .getIdThreshold()) {
                                                testimonyTipsFailTv.setText("人证核验通过");
                                                testimonyTipsFailTv.setTextColor(
                                                        Color.parseColor("#FF00BAF2"));
                                                testimonyTipsPleaseFailTv.setText("识别成功");
                                                testimonyTipsFailIv.setImageResource(
                                                        R.mipmap.tips_success);
                                            } else {
                                                testimonyTipsFailTv.setText("人证核验未通过");
                                                testimonyTipsFailTv.setTextColor(
                                                        Color.parseColor("#FFFEC133"));
                                                testimonyTipsPleaseFailTv.setText("请上传正面人脸照片");
                                                testimonyTipsFailIv.setImageResource(
                                                        R.mipmap.tips_fail);
                                            }
                                        }
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
                if (model == null) {
                    // 提示隐藏
                    layoutCompareStatus.setVisibility(View.GONE);
                    // 阈值
                    person_kaifaIv.setVisibility(View.GONE);
                    // 显示默认图片
                    testImageview.setImageResource(R.mipmap.ic_image_video);
                    // 默认值为0
                    tv_rgb_live_time.setText(String.format("相似度分数：%s", 0));
                    tv_rgb_live_score.setText(String.format("活体检测耗时：%s ms", 0));
                    tv_feature_time.setText(String.format("特征抽取耗时：%s ms", 0));
                    tv_feature_search_time.setText(String.format("特征比对耗时：%s ms", 0));
                    tv_all_time.setText(String.format("总耗时：%s ms", 0));
                } else {
                    // rgb回显图赋值显示
                    BDFaceImageInstance image = model.getBdFaceImageInstance();
                    if (image != null) {
                        testImageview.setImageBitmap(BitmapUtils.getInstaceBmp(image));
                    }
                    tv_rgb_live_time.setText(String.format("相似度分数：%s", score));
                    tv_rgb_live_score.setText(String.format("活体检测耗时：%s ms", model.getRgbLivenessDuration()));
                    int liveTypeValue = SingleBaseConfig.getBaseConfig().getType();
                    if (liveTypeValue == 1) {
                        tv_feature_time.setText(String.format("特征抽取耗时：%s ms", featureTime));
                        tv_feature_search_time.setText(String.format("特征比对耗时：%s ms", endCompareTime));
                    }

                    // 比较两个人脸
                    if (firstFeature == null || secondFeature == null) {
                        return;
                    }

                    if (isDevelopment) {
                        testimony_tips_failRl.setVisibility(View.GONE);
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        if (isFace == true) {
                            textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                            textCompareStatus.setText("比对失败");
                        } else {
                            if (mLiveType == 0) {
                                long l = model.getRgbDetectDuration() + model.
                                        getRgbLivenessDuration() + featureTime + endCompareTime;
                                tv_all_time.setText(String.format("总耗时：%s ms", l));
                                if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                                    textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                                    textCompareStatus.setText("比对成功");
                                } else {
                                    textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                                    textCompareStatus.setText("比对失败");
                                }
                            } else {
                                // 活体阈值判断显示
                                rgbLivenessScore = model.getRgbLivenessScore();
                                if (rgbLivenessScore < mRgbLiveScore) {
                                    tv_feature_time.setText(String.format("特征抽取耗时：%s ms", 0));
                                    tv_feature_search_time.setText(String.format("特征比对耗时：%s ms", 0));
                                    long l = model.getRgbDetectDuration() + model.getRgbLivenessDuration();
                                    tv_all_time.setText(String.format("总耗时：%s ms", l));
                                    person_kaifaIv.setVisibility(View.VISIBLE);
                                    person_kaifaIv.setImageResource(R.mipmap.ic_icon_develop_fail);
                                    textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                                    textCompareStatus.setText("比对失败");
                                } else {
                                    person_kaifaIv.setVisibility(View.VISIBLE);
                                    person_kaifaIv.setImageResource(R.mipmap.ic_icon_develop_success);
                                    tv_feature_time.setText(String.format("特征抽取耗时：%s ms", featureTime));
                                    tv_feature_search_time.setText(String.format("特征比对耗时：%s ms",
                                            endCompareTime));

                                    long l = model.getRgbDetectDuration() + model.
                                            getRgbLivenessDuration() + featureTime + endCompareTime;
                                    tv_all_time.setText(String.format("总耗时：%s ms", l));
                                    if (score > SingleBaseConfig.getBaseConfig().getIdThreshold()) {
                                        textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                                        textCompareStatus.setText("比对成功");
                                    } else {
                                        textCompareStatus.setTextColor(Color.parseColor("#FECD33"));
                                        textCompareStatus.setText("比对失败");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
            }
        });
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
                    float ret = FaceSDKManager.getInstance().personDetect(bitmap, secondFeature, this);
                    // 提取特征值
                    // 上传图片有人脸显示
                    hintShowIv.setVisibility(View.VISIBLE);
                    testimony_showImg.setVisibility(View.VISIBLE);
                    hintShowIv.setImageBitmap(bitmap);
                    testimony_showImg.setImageBitmap(bitmap);
                    if (ret != -1) {
                        isFace = false;
                        // 判断质量检测，针对模糊度、遮挡、角度
                        if (ret == 128) {
                            secondFeatureFinished = true;
                        }
                        if (ret == 128) {
                            toast("图片特征抽取成功");
                            hintShowRl.setVisibility(View.VISIBLE);
                            testimony_showRl.setVisibility(View.VISIBLE);
                            testimony_addIv.setVisibility(View.GONE);
                            testimony_upload_filesTv.setVisibility(View.GONE);
                            developmentAddRl.setVisibility(View.GONE);
                        } else {
                            ToastUtils.toast(mContext, "图片特征抽取失败");
                        }
                    } else {
                        isFace = true;
                        // 上传图片无人脸隐藏
                        hintShowIv.setVisibility(View.GONE);
                        testimony_showImg.setVisibility(View.GONE);
                        hintShowRl.setVisibility(View.VISIBLE);
                        testimony_showRl.setVisibility(View.VISIBLE);
                        testimony_addIv.setVisibility(View.GONE);
                        testimony_upload_filesTv.setVisibility(View.GONE);
                        developmentAddRl.setVisibility(View.GONE);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraPreviewManager.getInstance().stopPreview();
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
                int liveTypeValue = SingleBaseConfig.getBaseConfig().getType();
                if (liveTypeValue == 1) {
                    if (score < SingleBaseConfig.getBaseConfig().getIdThreshold()) {
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
                } else {
                    if (score < SingleBaseConfig.getBaseConfig().getIdThreshold()
                            || rgbLivenessScore < SingleBaseConfig.getBaseConfig()
                            .getRgbLiveScore()) {
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