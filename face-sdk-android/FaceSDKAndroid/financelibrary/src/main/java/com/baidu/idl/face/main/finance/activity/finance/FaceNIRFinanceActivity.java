package com.baidu.idl.face.main.finance.activity.finance;

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
import android.util.Log;
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
import com.baidu.idl.face.main.finance.setting.FinanceSettingActivity;
import com.baidu.idl.face.main.finance.manager.FinanceFaceSDKManager;
import com.baidu.idl.face.main.finance.model.LivenessModel;
import com.baidu.idl.face.main.finance.model.SingleBaseConfig;
import com.baidu.idl.face.main.finance.model.User;
import com.baidu.idl.face.main.finance.utils.BitmapUtils;
import com.baidu.idl.face.main.finance.utils.DensityUtils;
import com.baidu.idl.face.main.finance.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.face.main.finance.utils.TestPopWindow;
import com.baidu.idl.face.main.finance.utils.ToastUtils;
import com.baidu.idl.face.main.finance.view.PreviewTexture;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.financelibrary.R;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;


public class FaceNIRFinanceActivity extends FinanceBaseActivity implements View.OnClickListener {

    private static final String TAG = "face-rgb-ir";
    // ???????????????????????????????????????????????????640*480??? 1280*720
    private static final int PREFER_WIDTH = 640;
    private static final int PERFER_HEIGH = 480;

    private Context mContext;

    // ??????????????????
    private TextureView mDrawDetectFaceView;
    private ImageView mFaceDetectImageView;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;

    // ??????????????????
    private TextView mTvIr;
    private TextView mTvIrScore;

    private TextView mTvAllTime;


    // RGB+IR ??????
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;

    // textureView??????????????????????????????
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private TextureView irPreviewView;

    // ???????????????
    private int mCameraNum;
    // ?????????????????????
    private volatile byte[] rgbData;
    private volatile byte[] irData;
    // ???????????????
    private Paint paint;
    private RectF rectF;

    private RelativeLayout relativeLayout;

    // ????????????????????????????????????x?????????y????????????width
    private float[] pointXY = new float[4];
    private boolean isCheck = false;
    private boolean isCompareCheck = false;
    private TextView preText;
    private TextView deveLop;
    private RelativeLayout preViewRelativeLayout;
    private RelativeLayout deveLopRelativeLayout;
    private TextView detectSurfaceText;
    private ImageView isRgbCheckImage;
    private ImageView isNirCheckImage;
    private View preView;
    private View developView;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private Paint paintBg;
    private RelativeLayout progressLayout;
    private TextView preToastText;
    private ImageView progressBarView;
    private TextView nirSurfaceText;
    private RelativeLayout payHintRl;
    private boolean payHint = false;
    private boolean isTime = true;
    private boolean isNeedCamera = true;
    private long searshTime;
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
        setContentView(R.layout.activity_face_nir_finance);

        initView();

        // ????????????
        int displayWidth = DensityUtils.getDisplayWidth(mContext);
        // ????????????
        int displayHeight = DensityUtils.getDisplayHeight(mContext);
        // ?????????????????????????????????
        if (displayHeight < displayWidth) {
            // ?????????
            int height = displayHeight;
            // ?????????
            int width = (int) (displayHeight * ((9.0f / 16.0f)));
            // ????????????????????????
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
            // ??????????????????
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
                    ToastUtils.toast(mContext, "?????????????????????????????????");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FinanceFaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(mContext, "??????????????????????????????????????????");
                    }
                }
            });
        }
    }

    /**
     * ??????Debug View
     */
    private void initView() {

        // ??????????????????
        relativeLayout = findViewById(R.id.all_relative);
        // ????????????
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);
        // ???????????????RGB ????????????
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoCameraPreviewView.setVisibility(View.VISIBLE);
        mAutoCameraPreviewView.isDraw = true;

        // ??????
        ImageView mButReturn = findViewById(R.id.btn_back);
        mButReturn.setOnClickListener(this);
        // ??????
        ImageView mBtSetting = findViewById(R.id.btn_setting);
        mBtSetting.setOnClickListener(this);
        // ***************????????????*************
        // ?????????
        preText = findViewById(R.id.preview_text);
        preText.setOnClickListener(this);
        preText.setTextColor(Color.parseColor("#ffffff"));
        preView = findViewById(R.id.preview_view);
        // ????????????
        preViewRelativeLayout = findViewById(R.id.yvlan_relativeLayout);
        preToastText = findViewById(R.id.pre_toast_text);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarView = findViewById(R.id.progress_bar_view);
        // ?????????????????????
        payHintRl = findViewById(R.id.pay_hintRl);

        // ***************????????????*************
        // ?????????
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        deveLop.setTextColor(Color.parseColor("#a9a9a9"));
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        // ????????????
        deveLopRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        detectSurfaceText = findViewById(R.id.detect_surface_text);
        detectSurfaceText.setVisibility(View.GONE);
        nirSurfaceText = findViewById(R.id.nir_surface_text);
        nirSurfaceText.setVisibility(View.GONE);

        isRgbCheckImage = findViewById(R.id.is_check_image);
        isNirCheckImage = findViewById(R.id.nir_is_check_image);
        // ???????????????RGB ????????????
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        // ??????RGB ????????????
        mFaceDetectImageView = findViewById(R.id.face_detect_image_view);
        mFaceDetectImageView.setVisibility(View.GONE);
        // ???????????????IR ????????????
        irPreviewView = findViewById(R.id.ir_camera_preview_view);
        if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == 1) {
            irPreviewView.setRotationY(180);
        }
        // Ir??????
        mTvIr = findViewById(R.id.tv_nir_live_time);
        mTvIrScore = findViewById(R.id.tv_nir_live_score);
        // ????????????
        mTvDetect = findViewById(R.id.tv_detect_time);
        // RGB??????
        mTvLive = findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = findViewById(R.id.tv_rgb_live_score);
        // ?????????
        mTvAllTime = findViewById(R.id.tv_all_time);
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        layoutCompareStatus.setVisibility(View.GONE);
        textCompareStatus = findViewById(R.id.text_compare_status);

        // ?????????????????????
        financeQualityTestFailed = findViewById(R.id.finance_quality_test_failed);
        qualityTestTimeTv = findViewById(R.id.quality_test_timeTv);
        qualityDetectedTv = findViewById(R.id.quality_detectedTv);
        qualityShelteredPart = findViewById(R.id.quality_sheltered_part);
        qualityRetestDetectBtn = findViewById(R.id.quality_retest_detectBtn);
        qualityRetestDetectBtn.setOnClickListener(this);
        qualityDetectRegImageItem = findViewById(R.id.quality_detect_reg_image_item);

        // ????????????
        financeFailedInVivoTest = findViewById(R.id.finance_failed_in_vivo_test);
        byLivingDetectionRgb = findViewById(R.id.by_living_detection_rgb);
        byLivingDetectionNir = findViewById(R.id.by_living_detection_nir);
        byLivingDetectionDepth = findViewById(R.id.by_living_detection_depth);
        byLivingTetectionTime = findViewById(R.id.by_living_detection_time);
        byLivingDetectionFrames = findViewById(R.id.by_living_detection_Frames);
        Button byLivingDetectionBtn = findViewById(R.id.by_living_detection_btn);
        byLivingDetectionBtn.setOnClickListener(this);
        detectRegImageItem = findViewById(R.id.detect_reg_image_item);

        // ???????????????
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
                // ????????????
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
                    }, 500);  // ??????1?????????
                } else {
                    // ????????????
                    finish();
                    testPopWindow.closePopupWindow();
                }
            }
        });

        // ????????????
        mCameraNum = Camera.getNumberOfCameras();
        if (mCameraNum < 2) {
            Toast.makeText(this, "????????????2????????????", Toast.LENGTH_LONG).show();
            return;
        } else {

            mPreview = new PreviewTexture[mCameraNum];
            mCamera = new Camera[mCameraNum];
            mPreview[1] = new PreviewTexture(this, irPreviewView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraNum < 2) {
            Toast.makeText(this, "????????????2????????????", Toast.LENGTH_LONG).show();
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
        // TODO ??? ????????????
        // ?????????????????????
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        // ?????????????????????
        //  CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK);
        // ??????USB?????????
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);

        CameraPreviewManager.getInstance().startPreview(this, mAutoCameraPreviewView,
                PREFER_WIDTH, PERFER_HEIGH, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        // ???????????????????????????????????????
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

        CameraPreviewManager.getInstance().stopPreview();

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
        if (!isNeedCamera) {
            return;
        }
        if (rgbData != null && irData != null) {
            FinanceFaceSDKManager.getInstance().onDetectCheck(rgbData, irData, null,
                    PERFER_HEIGH, PREFER_WIDTH, 2, new FaceDetectCallBack() {
                        @Override
                        public void onFaceDetectCallback(LivenessModel livenessModel) {
                            // ????????????
                            if (mAutoCameraPreviewView.isDraw) {
                                // ????????????
                                checkCloseDebugResult(livenessModel);
                            } else {
                                // ????????????
                                checkOpenDebugResult(livenessModel);
                            }
                        }

                        @Override
                        public void onTip(int code, String msg) {

                        }

                        @Override
                        public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                            // ???????????????
                            if (!mAutoCameraPreviewView.isDraw) {
                                showFrame(livenessModel);
                            }

                        }
                    });
            rgbData = null;
            irData = null;
        }
    }

    // ***************????????????????????????*************
    private void checkCloseDebugResult(final LivenessModel livenessModel) {
        // ?????????????????????UI??????
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
                        preToastText.setText("???????????????????????????");
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
                        }, 500);  // ??????1?????????
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
                    preToastText.setText("???????????????????????????");
                    progressBarView.setImageResource(R.mipmap.ic_loading_grey);
                    return;
                }
//                preToastText.setTextColor(Color.parseColor("#FFFFFF"));
//                preToastText.setText("???????????????...");
                progressBarView.setImageResource(R.mipmap.ic_loading_blue);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count) {
                            count = false;
                            payHint(livenessModel);
                        }
                    }
                }, 1 * 1000);  // ??????1?????????

            }
        });
    }

    // ***************????????????????????????*************
    private void checkOpenDebugResult(final LivenessModel livenessModel) {

        // ?????????????????????UI??????
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel == null) {
                    layoutCompareStatus.setVisibility(View.GONE);
                    isNirCheckImage.setVisibility(View.GONE);
                    isRgbCheckImage.setVisibility(View.GONE);
                    mFaceDetectImageView.setImageResource(R.mipmap.ic_image_video);
                    mTvDetect.setText(String.format("???????????????%s ms", 0));
                    mTvLive.setText(String.format("RGB?????????????????? ???%s ms", 0));
                    mTvLiveScore.setText(String.format("RGB?????????????????? ???%s", false));
                    mTvIr.setText(String.format("NIR?????????????????? ???%s ms", 0));
                    mTvIrScore.setText(String.format("NIR?????????????????? ???%s", false));
                    mTvAllTime.setText(String.format("????????? ???%s ms", 0));

                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                        textCompareStatus.setText("??????????????????");
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
                            textCompareStatus.setText("?????????????????????");
                        }
                    }
                }

                if (livenessModel.isNIRLiveStatus()) {
                    if (isCheck) {
                        isNirCheckImage.setVisibility(View.VISIBLE);
                        isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }

                } else {
                    if (isCheck) {
                        isNirCheckImage.setVisibility(View.VISIBLE);
                        isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                }
                if (livenessModel.isRGBLiveStatus()) {
                    if (isCheck) {
                        isRgbCheckImage.setVisibility(View.VISIBLE);
                        isRgbCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }
                } else {
                    if (isCheck) {
                        isRgbCheckImage.setVisibility(View.VISIBLE);
                        isRgbCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                }

                if (livenessModel.isRGBLiveStatus() && livenessModel.isNIRLiveStatus()) {

                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                        textCompareStatus.setText("??????????????????");
                    }

                } else {
                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                        textCompareStatus.setText("?????????????????????");

                    }
                }

                mTvDetect.setText(String.format("???????????? ???%s ms", livenessModel.getRgbDetectDuration()));
                mTvLive.setText(String.format("RGB?????????????????? ???%s ms", livenessModel.getRgbLivenessDuration()));
                mTvLiveScore.setText(String.format("RGB?????????????????? ???%s", livenessModel.isRGBLiveStatus()));
                mTvIr.setText(String.format("NIR?????????????????? ???%s ms", livenessModel.getIrLivenessDuration()));
                mTvIrScore.setText(String.format("NIR?????????????????? ???%s", livenessModel.isNIRLiveStatus()));
                mTvAllTime.setText(String.format("????????? ???%s ms", livenessModel.getAllDetectDuration()));
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        // ??????
        if (id == R.id.btn_back) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
            // ??????
        } else if (id == R.id.btn_setting) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(new Intent(mContext, FinanceSettingActivity.class));
            finish();
        } else if (id == R.id.preview_text) {
            if (payHintRl.getVisibility() == View.VISIBLE) {
                return;
            }
            isCheck = false;
            isCompareCheck = false;
            mAutoCameraPreviewView.isDraw = true;
            mIsPayHint = true;
            count = true;
            irPreviewView.setAlpha(0);
            isRgbCheckImage.setVisibility(View.GONE);
            isNirCheckImage.setVisibility(View.GONE);
            mFaceDetectImageView.setVisibility(View.GONE);
            detectSurfaceText.setVisibility(View.GONE);
            layoutCompareStatus.setVisibility(View.GONE);
            nirSurfaceText.setVisibility(View.GONE);
            developView.setVisibility(View.GONE);
            deveLopRelativeLayout.setVisibility(View.GONE);
            mDrawDetectFaceView.setVisibility(View.GONE);

            progressLayout.setVisibility(View.VISIBLE);
            preToastText.setVisibility(View.VISIBLE);
            deveLop.setTextColor(Color.parseColor("#a9a9a9"));
            preText.setTextColor(Color.parseColor("#ffffff"));
            preView.setVisibility(View.VISIBLE);
            preViewRelativeLayout.setVisibility(View.VISIBLE);
            progressBarView.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
        } else if (id == R.id.develop_text) {
            isNeedCamera = true;
            mIsPayHint = false;
            isCheck = true;
            isCompareCheck = true;
            mAutoCameraPreviewView.isDraw = false;
            count = false;
            irPreviewView.setAlpha(1);
            mDrawDetectFaceView.setVisibility(View.VISIBLE);
            isRgbCheckImage.setVisibility(View.VISIBLE);
            isNirCheckImage.setVisibility(View.VISIBLE);
            mFaceDetectImageView.setVisibility(View.VISIBLE);
            detectSurfaceText.setVisibility(View.VISIBLE);
            nirSurfaceText.setVisibility(View.VISIBLE);
            developView.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.VISIBLE);

            testPopWindow.closePopupWindow();
            financeQualityTestFailed.setVisibility(View.GONE);
            financeFailedInVivoTest.setVisibility(View.GONE);
            financeByLivingDetection.setVisibility(View.GONE);

            deveLop.setTextColor(Color.parseColor("#ffffff"));
            preText.setTextColor(Color.parseColor("#a9a9a9"));
            preView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.GONE);
            preToastText.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            progressBarView.setVisibility(View.GONE);
            payHintRl.setVisibility(View.GONE);
        } else if (id == R.id.quality_retest_detectBtn) {
            financeQualityTestFailed.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    count = true;
                    if (count) {
                        isNeedCamera = true;
                    }
                }
            }, 1 * 1000);  // ??????1?????????
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
            }, 1 * 1000);  // ??????1?????????
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
            }, 1 * 1000);  // ??????1?????????
        }
    }

    /**
     * ???????????????
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
                    // ??????canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mDrawDetectFaceView.unlockCanvasAndPost(canvas);
                    return;
                }
                FaceInfo[] faceInfos = model.getTrackFaceInfo();
                if (faceInfos == null || faceInfos.length == 0) {
                    // ??????canvas
                    canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    mDrawDetectFaceView.unlockCanvasAndPost(canvas);
                    return;
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                FaceInfo faceInfo = faceInfos[0];

                rectF.set(FaceOnDrawTexturViewUtil.getFaceRectTwo(faceInfo));
                // ??????????????????????????????????????????????????????????????????
                FaceOnDrawTexturViewUtil.mapFromOriginalRect(rectF,
                        mAutoCameraPreviewView, model.getBdFaceImageInstance());
                // ???????????????
                FaceOnDrawTexturViewUtil.drawFaceColor(mUser, paint, paintBg, model);
                // ???????????????
                FaceOnDrawTexturViewUtil.drawCircle(canvas, mAutoCameraPreviewView,
                        rectF, paint, paintBg, faceInfo);
                // ??????canvas
                mDrawDetectFaceView.unlockCanvasAndPost(canvas);
            }
        });
    }


    private void payHint(final LivenessModel livenessModel) {
        if (livenessModel == null && mIsPayHint) {
            if (!this.isFinishing()) {
                testPopWindow.showPopupWindow(FaceNIRFinanceActivity.this.getWindow().getDecorView());
                isNeedCamera = false;
            }
        }

        if (mIsPayHint && livenessModel != null) {
            BDFaceImageInstance bdFaceImageInstance = livenessModel.getBdFaceImageInstance();
            Bitmap instaceBmp = BitmapUtils.getInstaceBmp(bdFaceImageInstance);
            testPopWindow.closePopupWindow();
            progressLayout.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.VISIBLE);

//            if (SingleBaseConfig.getBaseConfig().isQualityControl()) {
//                if (livenessModel.getListOcclusion() == null && livenessModel.getListOcclusion().size() <= 0) {
//                    qualityShelteredPart.setVisibility(View.GONE);
//                }
//                if (livenessModel.getListDetected() == null && livenessModel.getListDetected().size() <= 0) {
//                    qualityDetectedTv.setVisibility(View.GONE);
//                }
//            }

            if (livenessModel.getListDetected() != null && livenessModel.getListDetected().size() > 0 ||
                    livenessModel.getListOcclusion() != null && livenessModel.getListOcclusion().size() > 0) {
                if (mIsPayHint) {
                    qualityDetectRegImageItem.setImageBitmap(instaceBmp);
                    financeQualityTestFailed.setVisibility(View.VISIBLE);
                    isNeedCamera = false;
                    count = false;
//                    preToastText.setVisibility(View.GONE);

                    qualityTestTimeTv.setText("???????????????" + livenessModel.getAllDetectDuration() + " ms");
                    StringBuffer stringBufferDetected = new StringBuffer();
                    StringBuffer stringBufferOcclusion = new StringBuffer();

                    for (int i = 0; i < livenessModel.getListDetected().size(); i++) {
                        if (i == livenessModel.getListDetected().size() - 1) {
                            stringBufferDetected.append(livenessModel.getListDetected().get(i));
                        } else {
                            stringBufferDetected.append(livenessModel.getListDetected().get(i) + "???");
                        }

                    }

                    for (int i = 0; i < livenessModel.getListOcclusion().size(); i++) {
                        if (i == livenessModel.getListOcclusion().size() - 1) {
                            stringBufferOcclusion.append(livenessModel.getListOcclusion().get(i));
                        } else {
                            stringBufferOcclusion.append(livenessModel.getListOcclusion().get(i) + "???");
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

                    qualityShelteredPart.setText("???????????????" + stringBufferOcclusion.toString());
                    qualityDetectedTv.setText("????????????" + stringBufferDetected.toString());
                }
            } else {
                if (livenessModel.isRGBLiveStatus() && livenessModel.isNIRLiveStatus()) {
                    financeByLivingDetection.setVisibility(View.VISIBLE);
                    financeFailedInVivoTest.setVisibility(View.GONE);
                    detectRegImageItem.setImageBitmap(instaceBmp);
                    isNeedCamera = false;
                    byLivingDetectionRgb.setText("RGB?????????????????????" + livenessModel.getRgbLivenessDuration() + " ms");
                    byLivingDetectionNir.setText("NIR?????????????????????" + livenessModel.getIrLivenessDuration() + " ms");
                    byLivingDetectionDepth.setText("Depth?????????????????????" + livenessModel.getDepthtLivenessDuration() + " ms");
                    byLivingTetectionTime.setText("????????????????????????" + livenessModel.getAllDetectDuration() + " ms");
                    byLivingDetectionFrames.setText("?????????????????????" +
                            SingleBaseConfig.getBaseConfig().getFramesThreshold() + " ???");
                } else {
                    financeFailedInVivoTest.setVisibility(View.VISIBLE);
                    financeByLivingDetection.setVisibility(View.GONE);
                    noDetectRegImageItem.setImageBitmap(instaceBmp);
                    isNeedCamera = false;
                    failedInVivoTestRgb.setText("RGB?????????????????????" + livenessModel.isRGBLiveStatus());
                    failedInVivoTestNir.setText("NIR?????????????????????" + livenessModel.isNIRLiveStatus());
                    failedInVivoTestDepth.setText("Depth?????????????????????" + livenessModel.isDepthLiveStatus());
                    failedInVivoTestTime.setText("????????????????????????" + livenessModel.getAllDetectDuration() + " ms");
                    failedInVivoTestFrames.setText("?????????????????????" +
                            SingleBaseConfig.getBaseConfig().getFramesThreshold() + " ???");
                }
            }
        }
    }
}
