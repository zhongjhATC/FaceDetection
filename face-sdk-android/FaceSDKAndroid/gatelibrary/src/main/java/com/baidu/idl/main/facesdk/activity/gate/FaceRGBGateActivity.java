package com.baidu.idl.main.facesdk.activity.gate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.activity.BaseActivity;
import com.baidu.idl.main.facesdk.api.GateFaceApi;
import com.baidu.idl.main.facesdk.callback.CameraDataCallback;
import com.baidu.idl.main.facesdk.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.gatecamera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.gatecamera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.gatelibrary.R;
import com.baidu.idl.main.facesdk.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.LivenessModel;
import com.baidu.idl.main.facesdk.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.model.User;
import com.baidu.idl.main.facesdk.setting.GateSettingActivity;
import com.baidu.idl.main.facesdk.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.utils.DensityUtils;
import com.baidu.idl.main.facesdk.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.utils.ToastUtils;


public class FaceRGBGateActivity extends BaseActivity implements View.OnClickListener {

    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGH = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    private Context mContext;

    private TextureView mDrawDetectFaceView;
    private ImageView mFaceDetectImageView;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;
    private TextView mTvFeature;
    private TextView mTvAll;
    private TextView mTvAllTime;
    private TextView mNum;

    private RectF rectF;
    private Paint paint;
    private RelativeLayout relativeLayout;
    private int mLiveType;
    private float mRgbLiveScore;

    // 包含适配屏幕后后的人脸的x坐标，y坐标，和width
    private float[] pointXY = new float[3];
    private boolean requestToInner = false;
    private float mScreenRate = 50;
    private boolean isCheck = false;
    private boolean isCompareCheck = false;
    private TextView preText;
    private TextView deveLop;
    private RelativeLayout preViewRelativeLayout;
    private RelativeLayout deveLopRelativeLayout;
    private RelativeLayout textHuanying;
    private ImageView nameImage;
    private TextView nameText;
    private RelativeLayout userNameLayout;
    private TextView detectSurfaceText;
    private ImageView isCheckImage;
    private Paint paintBg;
    private View preView;
    private View developView;
    private View view;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private TextView logoText;
    private User mUser;
    private boolean isTime = true;
    private long startTime;
    private boolean detectCount;
    private AutoTexturePreviewView mAutoCameraPreviewView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initListener();
        FaceSDKManager.getInstance().initDataBases(this);
        setContentView(R.layout.activity_face_rgb_gate);
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
            relativeLayout.setLayoutParams(params);
        }

    }

    private void initListener() {
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceSDKManager.getInstance().initModel(mContext, new SdkInitListener() {
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
                    ToastUtils.toast(mContext, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(mContext, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    /**
     * View
     */
    private void initView() {
        // 获取整个布局
        relativeLayout = findViewById(R.id.all_relative);
        // 画人脸框
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);
        // 单目摄像头RGB 图像预览
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);

        logoText = findViewById(R.id.logo_text);
        logoText.setVisibility(View.VISIBLE);

        // 返回
        ImageView mButReturn = findViewById(R.id.btn_back);
        mButReturn.setOnClickListener(this);
        // 设置
        ImageView mBtSetting = findViewById(R.id.btn_setting);
        mBtSetting.setOnClickListener(this);
        // 预览模式
        preText = findViewById(R.id.preview_text);
        preText.setOnClickListener(this);
        preText.setTextColor(Color.parseColor("#ffffff"));
        preViewRelativeLayout = findViewById(R.id.yvlan_relativeLayout);
        preView = findViewById(R.id.preview_view);
        // 开发模式
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        deveLopRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        layoutCompareStatus.setVisibility(View.GONE);
        textCompareStatus = findViewById(R.id.text_compare_status);

        // ***************开发模式*************
        isCheckImage = findViewById(R.id.is_check_image);
        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();
        // 活体阈值
        mRgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // 送检RGB 图像回显
        mFaceDetectImageView = findViewById(R.id.face_detect_image_view);
        mFaceDetectImageView.setVisibility(View.GONE);
        // 存在底库的数量
        mNum = findViewById(R.id.tv_num);
        mNum.setText(String.format("底库 ： %s 个样本", GateFaceApi.getInstance().getmUserNum()));
        // 检测耗时
        mTvDetect = findViewById(R.id.tv_detect_time);
        // RGB活体
        mTvLive = findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = findViewById(R.id.tv_rgb_live_score);
        // 特征提取
        mTvFeature = findViewById(R.id.tv_feature_time);
        // 检索
        mTvAll = findViewById(R.id.tv_feature_search_time);
        // 总耗时
        mTvAllTime = findViewById(R.id.tv_all_time);


        // ***************预览模式*************
        textHuanying = findViewById(R.id.huanying_relative);
        userNameLayout = findViewById(R.id.user_name_layout);
        nameImage = findViewById(R.id.name_image);
        nameText = findViewById(R.id.name_text);
        detectSurfaceText = findViewById(R.id.detect_surface_text);
        mFaceDetectImageView.setVisibility(View.GONE);
        detectSurfaceText.setVisibility(View.GONE);
        view = findViewById(R.id.mongolia_view);
        view.setAlpha(0.85f);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTestOpenDebugRegisterFunction();
    }

    private void startTestOpenDebugRegisterFunction() {
        // TODO ： 临时放置
        //  CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        CameraPreviewManager.getInstance().startPreview(mContext, mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        // 摄像头预览数据进行人脸检测
                        FaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                height, width, mLiveType, new FaceDetectCallBack() {
                                    @Override
                                    public void onFaceDetectCallback(LivenessModel livenessModel) {
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
                                        // 绘制人脸框
                                        showFrame(livenessModel);


                                    }
                                });
                    }
                });
    }

    // ***************预览模式结果输出*************
    private void checkCloseDebugResult(final LivenessModel livenessModel) {
        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null) {
                    // 对背景色颜色进行改变，操作的属性为"alpha",此处必须这样写
                    // 不能全小写,后面设置的是对view的渐变
                    if (isTime) {
                        isTime = false;
                        startTime = System.currentTimeMillis();
                    }
                    detectCount = true;
                    long endTime = System.currentTimeMillis() - startTime;

                    if (endTime < 10000) {
                        textHuanying.setVisibility(View.VISIBLE);
                        userNameLayout.setVisibility(View.GONE);
                        return;
                    } else {
                        view.setVisibility(View.VISIBLE);
                    }

                    textHuanying.setVisibility(View.VISIBLE);
                    userNameLayout.setVisibility(View.GONE);
                    return;
                }
                isTime = true;
                if (detectCount) {
                    detectCount = false;
                    objectAnimator();
                } else {
                    view.setVisibility(View.GONE);
                }


                User user = livenessModel.getUser();
                if (user == null) {
                    mUser = null;
                    textHuanying.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    nameImage.setImageResource(R.mipmap.ic_tips_gate_fail);
                    nameText.setTextColor(Color.parseColor("#fec133"));
                    nameText.setText("抱歉 未能认出您");
                } else {
                    mUser = user;
                    textHuanying.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    nameImage.setImageResource(R.mipmap.ic_tips_gate_success);
                    nameText.setTextColor(Color.parseColor("#0dc6ff"));
                    nameText.setText(user.getUserName() + " 欢迎您");
                }

            }
        });
    }

    // ***************开发模式结果输出*************
    private void checkOpenDebugResult(final LivenessModel livenessModel) {

        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null) {
                    layoutCompareStatus.setVisibility(View.GONE);
                    isCheckImage.setVisibility(View.GONE);
                    mFaceDetectImageView.setImageResource(R.mipmap.ic_image_video);
                    mTvDetect.setText(String.format("检测耗时 ：%s ms", 0));
                    mTvLive.setText(String.format("RGB活体检测耗时 ：%s ms", 0));
                    mTvLiveScore.setText(String.format("RGB活体得分 ：%s", 0));
                    mTvFeature.setText(String.format("特征抽取耗时 ：%s ms", 0));
                    mTvAll.setText(String.format("特征比对耗时 ：%s ms", 0));
                    mTvAllTime.setText(String.format("总耗时 ：%s ms", 0));
                    return;
                }

                BDFaceImageInstance image = livenessModel.getBdFaceImageInstance();
                if (image != null) {
                    mFaceDetectImageView.setImageBitmap(BitmapUtils.getInstaceBmp(image));
                    image.destory();
                }
                if (mLiveType == 0) {
                    User user = livenessModel.getUser();
                    if (user == null) {
                        mUser = null;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
                            textCompareStatus.setText("识别未通过");
                        }

                    } else {
                        mUser = user;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#FF00BAF2"));
                            textCompareStatus.setText("识别通过");
                        }
                    }

                } else {
                    float rgbLivenessScore = livenessModel.getRgbLivenessScore();
                    if (rgbLivenessScore < mRgbLiveScore) {
                        if (isCheck) {
                            isCheckImage.setVisibility(View.VISIBLE);
                            isCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                        }

                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
                            textCompareStatus.setText("识别未通过");
                        }

                    } else {
                        if (isCheck) {
                            isCheckImage.setVisibility(View.VISIBLE);
                            isCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                        }
                        User user = livenessModel.getUser();
                        if (user == null) {
                            mUser = null;
                            if (isCompareCheck) {
                                layoutCompareStatus.setVisibility(View.VISIBLE);
                                textCompareStatus.setTextColor(Color.parseColor("#FFFEC133"));
                                textCompareStatus.setText("识别未通过");
                            }

                        } else {
                            mUser = user;
                            if (isCompareCheck) {
                                layoutCompareStatus.setVisibility(View.VISIBLE);
                                textCompareStatus.setTextColor(Color.parseColor("#FF00BAF2"));
                                textCompareStatus.setText("识别通过");
                            }

                        }
                    }
                }
                mTvDetect.setText(String.format("检测耗时 ：%s ms", livenessModel.getRgbDetectDuration()));
                mTvLive.setText(String.format("RGB活体检测耗时 ：%s ms", livenessModel.getRgbLivenessDuration()));
                mTvLiveScore.setText(String.format("RGB活体得分 ：%s", livenessModel.getRgbLivenessScore()));
                mTvFeature.setText(String.format("特征抽取耗时 ：%s ms", livenessModel.getFeatureDuration()));
                mTvAll.setText(String.format("特征比对耗时 ：%s ms", livenessModel.getCheckDuration()));
                mTvAllTime.setText(String.format("总耗时 ：%s ms", livenessModel.getAllDetectDuration()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();// 返回
        if (id == R.id.btn_back) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
            // 设置
        } else if (id == R.id.btn_setting) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(mContext, GateSettingActivity.class));
            finish();
        } else if (id == R.id.preview_text) {
            isCheckImage.setVisibility(View.GONE);
            mFaceDetectImageView.setVisibility(View.GONE);
            detectSurfaceText.setVisibility(View.GONE);
            layoutCompareStatus.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            deveLop.setTextColor(Color.parseColor("#a9a9a9"));
            preText.setTextColor(Color.parseColor("#ffffff"));
            preView.setVisibility(View.VISIBLE);
            developView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.GONE);
            logoText.setVisibility(View.VISIBLE);
            isCheck = false;
            isCompareCheck = false;
        } else if (id == R.id.develop_text) {
            isCheck = true;
            isCompareCheck = true;
            isCheckImage.setVisibility(View.VISIBLE);
            mFaceDetectImageView.setVisibility(View.VISIBLE);
            detectSurfaceText.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            deveLop.setTextColor(Color.parseColor("#ffffff"));
            preText.setTextColor(Color.parseColor("#a9a9a9"));
            preView.setVisibility(View.GONE);
            developView.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.VISIBLE);
            preViewRelativeLayout.setVisibility(View.GONE);
            logoText.setVisibility(View.GONE);
        }
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
                        mAutoCameraPreviewView, model.getBdFaceImageInstance());
                // 人脸框颜色
                FaceOnDrawTexturViewUtil.drawFaceColor(mUser, paint, paintBg, model);
                // 绘制人脸框
                FaceOnDrawTexturViewUtil.drawCircle(canvas, mAutoCameraPreviewView,
                        rectF, paint, paintBg, faceInfo);
                // 清空canvas
                mDrawDetectFaceView.unlockCanvasAndPost(canvas);
            }
        });
    }

    // 蒙层动画
    private void objectAnimator() {
        final ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 0.85f);
        animator.setDuration(500);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animator.cancel();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                view.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        });
        animator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
