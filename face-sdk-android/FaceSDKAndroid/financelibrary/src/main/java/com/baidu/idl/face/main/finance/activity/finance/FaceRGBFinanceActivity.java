package com.baidu.idl.face.main.finance.activity.finance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.finance.activity.FinanceBaseActivity;
import com.baidu.idl.face.main.finance.callback.CameraDataCallback;
import com.baidu.idl.face.main.finance.callback.FaceDetectCallBack;
import com.baidu.idl.face.main.finance.camera.AutoTexturePreviewView;
import com.baidu.idl.face.main.finance.camera.CameraPreviewManager;
import com.baidu.idl.face.main.finance.listener.SdkInitListener;
import com.baidu.idl.face.main.finance.manager.FinanceFaceSDKManager;
import com.baidu.idl.face.main.finance.model.LivenessModel;
import com.baidu.idl.face.main.finance.model.SingleBaseConfig;
import com.baidu.idl.face.main.finance.model.User;
import com.baidu.idl.face.main.finance.setting.FinanceSettingActivity;
import com.baidu.idl.face.main.finance.utils.BitmapUtils;
import com.baidu.idl.face.main.finance.utils.DensityUtils;
import com.baidu.idl.face.main.finance.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.face.main.finance.utils.TestPopWindow;
import com.baidu.idl.face.main.finance.utils.ToastUtils;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.financelibrary.R;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;


public class FaceRGBFinanceActivity extends FinanceBaseActivity implements View.OnClickListener {

    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGH = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    private Context mContext;
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private RelativeLayout relativeLayout;
    private boolean isCheck = false;
    private boolean isTime = true;
    private long searshTime;
    private boolean isCompareCheck = false;
    private boolean isNeedCamera = true;
    private TextureView mDrawDetectFaceView;
    private RectF rectF;
    private Paint paint;
    private Paint paintBg;
    // 包含适配屏幕后后的人脸的x坐标，y坐标，和width
    private float[] pointXY = new float[4];

    private TextView preText;
    private ImageView previewView;
    private RelativeLayout preViewRelativeLayout;

    private TextView deveLop;
    private RelativeLayout deveLopRelativeLayout;
    private ImageView developView;
    private TextView preToastText;
    private TextView detectSurfaceText;
    private ImageView isCheckImage;
    private ImageView mFaceDetectImageView;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;
    private TextView mTvAllTime;
    private RelativeLayout progressLayout;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private ImageView progressBarView;
    private RelativeLayout payHintRl;
    private boolean mIsPayHint = true;
    private User mUser;
    private boolean count = true;
    private RelativeLayout financeQualityTestFailed;
    private TextView qualityTestTimeTv;
    private TextView qualityDetectedTv;
    private TextView qualityShelteredPart;
    private Button qualityRetestDetectBtn;
    private RelativeLayout financeByLivingDetection;
    private RelativeLayout financeFailedInVivoTest;
    private TextView failedInVivoTestRgb;
    private TextView failedInVivoTestNir;
    private TextView failedInVivoTestDepth;
    private TextView failedInVivoTestTime;
    private TextView failedInVivoTestFrames;
    private TextView byLivingDetectionRgb;
    private TextView byLivingDetectionNir;
    private TextView byLivingDetectionDepth;
    private TextView byLivingTetectionTime;
    private TextView byLivingDetectionFrames;
    private ImageView qualityDetectRegImageItem;
    private ImageView noDetectRegImageItem;
    private ImageView detectRegImageItem;
    private TestPopWindow testPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initListener();
        setContentView(R.layout.activity_face_rgb_finance);

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
        if (FinanceFaceSDKManager.initStatus != FinanceFaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {

            FinanceFaceSDKManager.getInstance().initModel(mContext, new SdkInitListener() {
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
                    FinanceFaceSDKManager.initModelSuccess = true;
                    ToastUtils.toast(mContext, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FinanceFaceSDKManager.initModelSuccess = false;
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
        // 单目摄像头RGB 图像预览
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoCameraPreviewView.isDraw = true;
        // 画人脸框
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);

        // 返回
        ImageView mButReturn = findViewById(R.id.btn_back);
        mButReturn.setOnClickListener(this);
        // 设置
        ImageView mBtSetting = findViewById(R.id.btn_setting);
        mBtSetting.setOnClickListener(this);

        // ***************预览模式*************
        // 导航栏
        preText = findViewById(R.id.preview_text);
        preText.setOnClickListener(this);
        preText.setTextColor(Color.parseColor("#ffffff"));
        previewView = findViewById(R.id.preview_view);
        // 信息展示
        preViewRelativeLayout = findViewById(R.id.yvlan_relativeLayout);
        preToastText = findViewById(R.id.pre_toast_text);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarView = findViewById(R.id.progress_bar_view);
        // 预览模式下提示
        payHintRl = findViewById(R.id.pay_hintRl);

        // ***************开发模式*************
        // 导航栏
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        deveLop.setTextColor(Color.parseColor("#a9a9a9"));
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        // 信息展示
        deveLopRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        isCheckImage = findViewById(R.id.is_check_image);
        detectSurfaceText = findViewById(R.id.detect_surface_text);
        detectSurfaceText.setVisibility(View.GONE);
        // 送检RGB 图像回显
        mFaceDetectImageView = findViewById(R.id.face_detect_image_view);
        mFaceDetectImageView.setVisibility(View.GONE);
        // 检测耗时
        mTvDetect = findViewById(R.id.tv_detect_time);
        // RGB活体
        mTvLive = findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = findViewById(R.id.tv_rgb_live_score);
        // 总耗时
        mTvAllTime = findViewById(R.id.tv_all_time);
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        layoutCompareStatus.setVisibility(View.GONE);
        textCompareStatus = findViewById(R.id.text_compare_status);

        // 质量检测未通过
        financeQualityTestFailed = findViewById(R.id.finance_quality_test_failed);
        qualityTestTimeTv = findViewById(R.id.quality_test_timeTv);
        qualityDetectedTv = findViewById(R.id.quality_detectedTv);
        qualityShelteredPart = findViewById(R.id.quality_sheltered_part);
        qualityRetestDetectBtn = findViewById(R.id.quality_retest_detectBtn);
        qualityRetestDetectBtn.setOnClickListener(this);
        qualityDetectRegImageItem = findViewById(R.id.quality_detect_reg_image_item);


        // 活体通过
        financeFailedInVivoTest = findViewById(R.id.finance_failed_in_vivo_test);
        byLivingDetectionRgb = findViewById(R.id.by_living_detection_rgb);
        byLivingDetectionNir = findViewById(R.id.by_living_detection_nir);
        byLivingDetectionDepth = findViewById(R.id.by_living_detection_depth);
        byLivingTetectionTime = findViewById(R.id.by_living_detection_time);
        byLivingDetectionFrames = findViewById(R.id.by_living_detection_Frames);
        Button byLivingDetectionBtn = findViewById(R.id.by_living_detection_btn);
        byLivingDetectionBtn.setOnClickListener(this);
        detectRegImageItem = findViewById(R.id.detect_reg_image_item);

        // 活体未通过
        financeByLivingDetection = findViewById(R.id.finance_by_living_detection);
        failedInVivoTestRgb = findViewById(R.id.failed_in_vivo_test_rgb);
        failedInVivoTestNir = findViewById(R.id.failed_in_vivo_test_nir);
        failedInVivoTestDepth = findViewById(R.id.failed_in_vivo_test_depth);
        failedInVivoTestTime = findViewById(R.id.failed_in_vivo_test_time);
        failedInVivoTestFrames = findViewById(R.id.failed_in_vivo_test_Frames);
        Button failed_in_vivo_testBtn = findViewById(R.id.failed_in_vivo_testBtn);
        failed_in_vivo_testBtn.setOnClickListener(this);
        noDetectRegImageItem = findViewById(R.id.no_detect_reg_image_item);

        testPopWindow = new TestPopWindow(this,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        testPopWindow.setmOnClickFinance(new TestPopWindow.OnClickFinance() {
            @Override
            public void rester(boolean isReTest) {
                // 重新检测
                if (isReTest) {
                    testPopWindow.closePopupWindow();
                    progressLayout.setVisibility(View.VISIBLE);
                    payHintRl.setVisibility(View.GONE);
                    count = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (count) {
                                isNeedCamera = true;
                            }
                        }
                    }, 500);  // 延迟1秒执行
                    // 返回首页
                } else {
                    testPopWindow.closePopupWindow();
                    finish();
//                    financeNoDetect.setVisibility(View.GONE);
                }
            }
        });
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
                        if (isNeedCamera) {
                            FinanceFaceSDKManager.getInstance().onDetectCheck(data, null, null,
                                    height, width, 1, new FaceDetectCallBack() {
                                        @Override
                                        public void onFaceDetectCallback(LivenessModel livenessModel) {
                                            if (mAutoCameraPreviewView.isDraw) {
                                                // 预览模式
                                                checkCloseDebugResult(livenessModel);
                                            } else {
                                                // 开发模式
                                                checkOpenDebugResult(livenessModel);
                                            }
                                        }

                                        @Override
                                        public void onTip(int code, String msg) {
                                        }

                                        @Override
                                        public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                                            // 绘制人脸框
                                            if (!mAutoCameraPreviewView.isDraw) {
                                                showFrame(livenessModel);
                                            }


                                        }
                                    });
                        }
                    }
                });
    }

    // ***************预览模式结果输出*************
    private void checkCloseDebugResult(final LivenessModel livenessModel) {
        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null || livenessModel.getFaceInfo() == null) {
                    if (isTime) {
                        isTime = false;
                        searshTime = System.currentTimeMillis();
                    }
                    long endSearchTime = System.currentTimeMillis() - searshTime;
                    if (endSearchTime < 3000) {
                        preToastText.setTextColor(Color.parseColor("#FFFFFF"));
                        preToastText.setText("保持面部在取景框内");
                        progressBarView.setImageResource(R.mipmap.ic_loading_grey);
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (count) {
                                    count = false;
                                    payHint(null);
                                }
                            }
                        }, 500);  // 延迟1秒执行
                    }
                    return;
                }

                isTime = true;
                pointXY[0] = livenessModel.getFaceInfo().centerX;
                pointXY[1] = livenessModel.getFaceInfo().centerY;
                pointXY[2] = livenessModel.getFaceInfo().width;
                pointXY[3] = livenessModel.getFaceInfo().width;
                FaceOnDrawTexturViewUtil.converttPointXY(pointXY, mAutoCameraPreviewView,
                        livenessModel.getBdFaceImageInstance(), livenessModel.getFaceInfo().width);
                float leftLimitX = AutoTexturePreviewView.circleX - AutoTexturePreviewView.circleRadius;
                float rightLimitX = AutoTexturePreviewView.circleX + AutoTexturePreviewView.circleRadius;
                float topLimitY = AutoTexturePreviewView.circleY - AutoTexturePreviewView.circleRadius;
                float bottomLimitY = AutoTexturePreviewView.circleY + AutoTexturePreviewView.circleRadius;
                if (pointXY[0] - pointXY[2] / 2 < leftLimitX
                        || pointXY[0] + pointXY[2] / 2 > rightLimitX
                        || pointXY[1] - pointXY[3] / 2 < topLimitY
                        || pointXY[1] + pointXY[3] / 2 > bottomLimitY) {
                    preToastText.setTextColor(Color.parseColor("#FFFFFF"));
                    preToastText.setText("保持面部在取景框内");
                    progressBarView.setImageResource(R.mipmap.ic_loading_grey);
                    return;
                }
                progressBarView.setImageResource(R.mipmap.ic_loading_blue);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count) {
                            count = false;
                            payHint(livenessModel);
                        }
                    }
                }, 2 * 500);  // 延迟1秒执行
            }
        });
    }

    //  ***************开发模式结果输出*************
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
                    mTvLiveScore.setText(String.format("RGB活体检测结果 ：%s", false));
                    mTvAllTime.setText(String.format("总耗时 ：%s ms", 0));

                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                        textCompareStatus.setText("未检测到人脸");
                    }
                    return;
                }

                BDFaceImageInstance image = livenessModel.getBdFaceImageInstance();
                if (image != null) {
                    mFaceDetectImageView.setImageBitmap(BitmapUtils.getInstaceBmp(image));
                    image.destory();
                }

                if (SingleBaseConfig.getBaseConfig().isQualityControl()) {
                    if (livenessModel.getListDetected().size() != 0 && livenessModel.getListOcclusion().size() != 0) {
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                            textCompareStatus.setText("未通过质量检测");
                        }
                    }
                }

                if (livenessModel.isRGBLiveStatus()) {
                    if (isCheck) {
                        isCheckImage.setVisibility(View.VISIBLE);
                        isCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }
                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                        textCompareStatus.setText("通过活体检测");
                    }
                } else {
                    if (isCheck) {
                        isCheckImage.setVisibility(View.VISIBLE);
                        isCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                        textCompareStatus.setText("未通过活体检测");
                    }
                }
                mTvDetect.setText(String.format("检测耗时 ：%s ms", livenessModel.getRgbDetectDuration()));
                mTvLive.setText(String.format("RGB活体检测耗时 ：%s ms", livenessModel.getRgbLivenessDuration()));
                mTvLiveScore.setText(String.format("RGB活体检测结果 ：%s", livenessModel.isRGBLiveStatus()));
                mTvAllTime.setText(String.format("总耗时 ：%s ms", livenessModel.getAllDetectDuration()));
            }
        });
    }


    @Override
    public void onClick(View v) {
        // 返回
        int id = v.getId();
        if (id == R.id.btn_back) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
        } else if (id == R.id.btn_setting) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(mContext, FinanceSettingActivity.class));
            finish();
        } else if (id == R.id.preview_text) {
            if (payHintRl.getVisibility() == View.VISIBLE) {
                return;
            }
            mAutoCameraPreviewView.isDraw = true;
            previewView.setVisibility(View.VISIBLE);
            preText.setTextColor(Color.parseColor("#ffffff"));
            preViewRelativeLayout.setVisibility(View.VISIBLE);
            preToastText.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.VISIBLE);
            progressBarView.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            layoutCompareStatus.setVisibility(View.GONE);
            developView.setVisibility(View.GONE);
            deveLop.setTextColor(Color.parseColor("#a9a9a9"));
            deveLopRelativeLayout.setVisibility(View.GONE);
            detectSurfaceText.setVisibility(View.GONE);
            detectSurfaceText.setVisibility(View.GONE);
            mFaceDetectImageView.setVisibility(View.GONE);
            isCheckImage.setVisibility(View.GONE);
            mDrawDetectFaceView.setVisibility(View.GONE);
            isCompareCheck = false;
            isCheck = false;
            mIsPayHint = true;
            count = true;
        } else if (id == R.id.develop_text) {
            isNeedCamera = true;
            mIsPayHint = false;
            mAutoCameraPreviewView.isDraw = false;
            previewView.setVisibility(View.GONE);
            preText.setTextColor(Color.parseColor("#a9a9a9"));
            preViewRelativeLayout.setVisibility(View.GONE);
            preToastText.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            progressBarView.setVisibility(View.GONE);
            payHintRl.setVisibility(View.GONE);

            testPopWindow.closePopupWindow();

            financeQualityTestFailed.setVisibility(View.GONE);
            financeFailedInVivoTest.setVisibility(View.GONE);
            financeByLivingDetection.setVisibility(View.GONE);

            developView.setVisibility(View.VISIBLE);
            deveLop.setTextColor(Color.parseColor("#ffffff"));
            deveLopRelativeLayout.setVisibility(View.VISIBLE);
            detectSurfaceText.setVisibility(View.VISIBLE);
            mFaceDetectImageView.setVisibility(View.VISIBLE);
            isCheckImage.setVisibility(View.VISIBLE);
            mDrawDetectFaceView.setVisibility(View.VISIBLE);
            isCompareCheck = true;
            isCheck = true;
            count = false;
        } else if (id == R.id.quality_retest_detectBtn) {
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            financeQualityTestFailed.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = true;
                    if (count) {
                        isNeedCamera = true;
                    }
                }
            }, 1 * 1000);  // 延迟1秒执行
        } else if (id == R.id.failed_in_vivo_testBtn) {
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            financeFailedInVivoTest.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = true;
                    if (count) {
                        isNeedCamera = true;
                    }
                }
            }, 1 * 1000);  // 延迟1秒执行
        } else if (id == R.id.by_living_detection_btn) {
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            financeByLivingDetection.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = true;
                    if (count) {
                        isNeedCamera = true;
                    }
                }
            }, 1 * 1000);  // 延迟1秒执行
        }
    }


    @SuppressLint("NewApi")
    private void payHint(final LivenessModel livenessModel) {
        if (livenessModel == null && mIsPayHint) {
            progressLayout.setVisibility(View.GONE);
            if (!this.isFinishing()) {
                testPopWindow.showPopupWindow(FaceRGBFinanceActivity.this.getWindow().getDecorView());
                isNeedCamera = false;
            }
        }

        if (mIsPayHint && livenessModel != null) {
            BDFaceImageInstance bdFaceImageInstance = livenessModel.getBdFaceImageInstance();
            Bitmap instaceBmp = BitmapUtils.getInstaceBmp(bdFaceImageInstance);
            testPopWindow.closePopupWindow();
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.VISIBLE);
            if (livenessModel.getListDetected() != null && livenessModel.getListDetected().size() > 0 ||
                    livenessModel.getListOcclusion() != null && livenessModel.getListOcclusion().size() > 0) {
                if (mIsPayHint) {
                    qualityDetectRegImageItem.setImageBitmap(instaceBmp);
                    financeQualityTestFailed.setVisibility(View.VISIBLE);
                    isNeedCamera = false;
                    count = false;

                    qualityTestTimeTv.setText("检测耗时：" + livenessModel.getAllDetectDuration() + " ms");
                    StringBuffer stringBufferDetected = new StringBuffer();
                    StringBuffer stringBufferOcclusion = new StringBuffer();
                    for (int i = 0; i < livenessModel.getListDetected().size(); i++) {
                        if (i == livenessModel.getListDetected().size() - 1) {
                            stringBufferDetected.append(livenessModel.getListDetected().get(i));
                        } else {
                            stringBufferDetected.append(livenessModel.getListDetected().get(i) + "、");
                        }

                    }

                    for (int i = 0; i < livenessModel.getListOcclusion().size(); i++) {
                        if (i == livenessModel.getListOcclusion().size() - 1) {
                            stringBufferOcclusion.append(livenessModel.getListOcclusion().get(i));
                        } else {
                            stringBufferOcclusion.append(livenessModel.getListOcclusion().get(i) + "、");
                        }

                    }

                    if (stringBufferDetected.toString() == "") {
                        qualityDetectedTv.setVisibility(View.GONE);
                    } else {
                        qualityDetectedTv.setVisibility(View.VISIBLE);
                    }
                    if (stringBufferOcclusion.toString() == "") {
                        qualityShelteredPart.setVisibility(View.GONE);
                    } else {
                        qualityShelteredPart.setVisibility(View.VISIBLE);
                    }

                    qualityDetectedTv.setText("检测到：" + stringBufferDetected.toString());
                    qualityShelteredPart.setText("遮挡部位：" + stringBufferOcclusion.toString());

                }
            } else {
                if (livenessModel.isRGBLiveStatus()) {
                    financeByLivingDetection.setVisibility(View.VISIBLE);
                    financeFailedInVivoTest.setVisibility(View.GONE);
                    detectRegImageItem.setImageBitmap(instaceBmp);
                    isNeedCamera = false;
                    byLivingDetectionRgb.setText("RGB活体检测耗时：" + livenessModel.getRgbLivenessDuration() + " ms");
                    byLivingDetectionNir.setText("NIR活体检测耗时：" + livenessModel.getIrLivenessDuration() + " ms");
                    byLivingDetectionDepth.setText("Depth活体检测耗时：" + livenessModel.getDepthtLivenessDuration() + " ms");
                    byLivingTetectionTime.setText("活体检测总耗时：" + livenessModel.getAllDetectDuration() + " ms");
                    byLivingDetectionFrames.setText("活体检测帧数：" +
                            SingleBaseConfig.getBaseConfig().getFramesThreshold() + " 帧");
                } else {
                    financeFailedInVivoTest.setVisibility(View.VISIBLE);
                    financeByLivingDetection.setVisibility(View.GONE);
                    noDetectRegImageItem.setImageBitmap(instaceBmp);
                    isNeedCamera = false;
                    failedInVivoTestRgb.setText("RGB活体检测结果：" + livenessModel.isRGBLiveStatus());
                    failedInVivoTestNir.setText("NIR活体检测结果：" + livenessModel.isNIRLiveStatus());
                    failedInVivoTestDepth.setText("Depth活体检测结果：" + livenessModel.isDepthLiveStatus());
                    failedInVivoTestTime.setText("活体检测总耗时：" + livenessModel.getAllDetectDuration() + " ms");
                    failedInVivoTestFrames.setText("活体检测帧数：" +
                            SingleBaseConfig.getBaseConfig().getFramesThreshold() + " 帧");
                }
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
