package com.baidu.idl.main.facesdk.gazelibrary.gaze;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.gazelibrary.BaseActivity;
import com.baidu.idl.main.facesdk.gazelibrary.R;
import com.baidu.idl.main.facesdk.gazelibrary.callback.CameraDataCallback;
import com.baidu.idl.main.facesdk.gazelibrary.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.gazelibrary.camera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.gazelibrary.camera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.gazelibrary.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.gazelibrary.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.gazelibrary.model.LivenessModel;
import com.baidu.idl.main.facesdk.gazelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.gazelibrary.setting.GazeSettingActivity;
import com.baidu.idl.main.facesdk.gazelibrary.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.gazelibrary.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.gazelibrary.utils.ToastUtils;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * author : shangrong
 * date : 2020-02-13 11:12
 * description :
 */
public class FaceGazeActivity extends BaseActivity {

    // RGB摄像头图像宽和高
    private static final int RGB_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int RGB_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();
    private AutoTexturePreviewView autoTexturePreviewView;
    private TextureView mDrawDetectFaceView;
    private Paint paint;
    private Paint paintBg;
    private RectF rectF;
    private ImageView faceGaze;
    private BDFaceGazeInfo bdFaceGazeInfo;
    private ExecutorService es3 = Executors.newSingleThreadExecutor();
    private Future future3;
    private TextView gazeResult;
    private LinearLayout gazeRelease;
    private LinearLayout gazeDebugResult;
    private TextView gazeDebugText;
    private TextView previewText;
    private TextView developText;
    private TextView gfDetectCost;
    private TextView gazeDetectCost;
    private ImageView previewView;
    private ImageView developView;
    private ImageView btnBack;
    private ImageView btnSetting;
    private TextView gaRGBScore;
    private TextView gaRGBCost;
    private TextView homeBaiduTv;
    private ImageView gazeIsRGBLive;
    private RelativeLayout gazeMiddleLiner;
    private boolean isRelease = false;
    private RelativeLayout gazeReleaseResult;
    private int liveCheckMode;
    private int mLiveType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_face_gazelibrary);
        init();
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
                    ToastUtils.toast(FaceGazeActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(FaceGazeActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    public void init() {
        liveCheckMode = SingleBaseConfig.getBaseConfig().getType();
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        gazeMiddleLiner = findViewById(R.id.gazeMiddleLiner);
        homeBaiduTv = findViewById(R.id.home_baiduTv);
        autoTexturePreviewView = findViewById(R.id.fa_auto);
        mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);
        faceGaze = findViewById(R.id.face_gaze);
        gazeResult = findViewById(R.id.gazeResult);
        gazeRelease = findViewById(R.id.gazeRelease);
        gazeDebugResult = findViewById(R.id.gazeDebugResult);
        gazeDebugText = findViewById(R.id.gazeDebugText);
        previewText = findViewById(R.id.preview_text);
        developText = findViewById(R.id.develop_text);
        gfDetectCost = findViewById(R.id.gfDetectCost);
        gazeDetectCost = findViewById(R.id.gazeDetectCost);
        previewView = findViewById(R.id.preview_view);
        developView = findViewById(R.id.develop_view);
        btnBack = findViewById(R.id.btn_back);
        btnSetting = findViewById(R.id.btn_setting);
        gaRGBScore = findViewById(R.id.gaRGBScore);
        gaRGBCost = findViewById(R.id.gaRGBCost);
        gazeIsRGBLive = findViewById(R.id.gazeIsRGBLive);
        gazeReleaseResult = findViewById(R.id.gazeReleaseResult);

        // 活体状态
        mLiveType = SingleBaseConfig.getBaseConfig().getType();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FaceSDKManager.initModelSuccess) {
                    Toast.makeText(FaceGazeActivity.this, "SDK正在加载模型，请稍后再试",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                finish();
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FaceSDKManager.initModelSuccess) {
                    Toast.makeText(FaceGazeActivity.this, "SDK正在加载模型，请稍后再试",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(FaceGazeActivity.this, GazeSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        previewText.setTextColor(Color.parseColor("#FFFFFF"));
        previewText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRelease = false;
                homeBaiduTv.setVisibility(View.VISIBLE);
                previewText.setTextColor(Color.parseColor("#FFFFFF"));
                developText.setTextColor(Color.parseColor("#d3d3d3"));
                previewView.setVisibility(View.VISIBLE);
                developView.setVisibility(View.GONE);
                gazeDebugResult.setVisibility(View.VISIBLE);
                gazeRelease.setVisibility(View.GONE);
            }
        });

        developText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRelease = true;
                homeBaiduTv.setVisibility(View.GONE);
                developText.setTextColor(Color.parseColor("#FFFFFF"));
                previewText.setTextColor(Color.parseColor("#d3d3d3"));
                previewView.setVisibility(View.GONE);
                developView.setVisibility(View.VISIBLE);
                gazeDebugResult.setVisibility(View.GONE);
                gazeRelease.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraPreview();
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
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);

        CameraPreviewManager.getInstance().startPreview(this, autoTexturePreviewView,
                RGB_WIDTH, RGB_HEIGHT, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] rgbData, Camera camera, int srcWidth, int srcHeight) {
                        dealRgb(rgbData);
                    }
                });
    }


    private void dealRgb(byte[] rgbData) {
        if (rgbData != null) {
            FaceSDKManager.getInstance().onAttrDetectCheck(rgbData, null, null, RGB_HEIGHT,
                    RGB_WIDTH, liveCheckMode, new FaceDetectCallBack() {
                        @Override
                        public void onFaceDetectCallback(final LivenessModel livenessModel) {

                            // 检测结果输出
                            if (future3 != null && !future3.isDone()) {
                                return;
                            }
                            future3 = es3.submit(new Runnable() {
                                @Override
                                public void run() {
                                    long endTime = 0;
                                    if (livenessModel != null) {
                                        long startTime = System.currentTimeMillis();
                                        bdFaceGazeInfo = FaceSDKManager.getInstance().gazeDetect(livenessModel);
                                        endTime = System.currentTimeMillis() - startTime;
                                    }
                                    showResult(livenessModel, bdFaceGazeInfo, endTime);
                                }
                            });
                        }

                        @Override
                        public void onTip(int code, String msg) {

                        }

                        @Override
                        public void onFaceDetectDarwCallback(LivenessModel livenessModel) {
                            showFrame(livenessModel);
                        }
                    });
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
                        autoTexturePreviewView, model.getBdFaceImageInstance());

                paint.setColor(Color.parseColor("#00baf2"));
                paintBg.setColor(Color.parseColor("#00baf2"));

                paint.setStyle(Paint.Style.STROKE);
                paintBg.setStyle(Paint.Style.STROKE);
                // 画笔粗细
                paint.setStrokeWidth(8);
                paint.setAntiAlias(true);
                paintBg.setStrokeWidth(13);
                paintBg.setAntiAlias(true);
                paintBg.setAlpha(90);

                if (faceInfo.width > faceInfo.height) {
                    if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                        canvas.drawCircle(autoTexturePreviewView.getWidth() - rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2 - 8, paintBg);
                        canvas.drawCircle(autoTexturePreviewView.getWidth() - rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2, paint);
                    } else {
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2 - 8, paintBg);
                        canvas.drawCircle(rectF.centerX(), rectF.centerY(),
                                rectF.width() / 2, paint);
                    }

                } else {
                    if (!SingleBaseConfig.getBaseConfig().getRgbRevert()) {
                        canvas.drawCircle(autoTexturePreviewView.getWidth() - rectF.centerX(), rectF.centerY(),
                                rectF.height() / 2 - 8, paintBg);
                        canvas.drawCircle(autoTexturePreviewView.getWidth() - rectF.centerX(), rectF.centerY(),
                                rectF.height() / 2, paint);
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

    private void showDetectImage(final Bitmap roundBitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                faceGaze.setVisibility(View.VISIBLE);
                faceGaze.setImageBitmap(roundBitmap);
            }
        });
    }

    // bitmap转成圆角
    private Bitmap bimapRound(Bitmap mBitmap, float index) {
        Bitmap bitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        // 设置矩形大小
        Rect rect = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        RectF rectf = new RectF(rect);

        // 相当于清屏
        canvas.drawARGB(0, 0, 0, 0);
        // 画圆角
        canvas.drawRoundRect(rectf, index, index, paint);
        // 取两层绘制，显示上层
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        // 把原生的图片放到这个画布上，使之带有画布的效果
        canvas.drawBitmap(mBitmap, rect, rect, paint);
        return bitmap;

    }

    public void showResult(final LivenessModel livenessModel, final BDFaceGazeInfo bdFaceGazeInfo, final long time) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livenessModel != null) {
                    if (!isRelease) {
                        gazeDebugResult.setVisibility(View.VISIBLE);
                    } else {
                        gazeReleaseResult.setVisibility(View.VISIBLE);
                    }
                    gazeMiddleLiner.setVisibility(View.VISIBLE);
                    if (liveCheckMode != 0) {
                        gazeIsRGBLive.setVisibility(View.VISIBLE);
                    }
                    if (mLiveType == 0) {
                        gazeIsRGBLive.setVisibility(View.GONE);

                    } else {
                        gazeIsRGBLive.setVisibility(View.VISIBLE);
                        if (livenessModel.getRgbLivenessScore() > SingleBaseConfig.getBaseConfig().getRgbLiveScore()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap successBitmap = BitmapFactory.decodeResource(getResources(),
                                            R.mipmap.ic_icon_develop_success);
                                    gazeIsRGBLive.setImageBitmap(successBitmap);
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bitmap failBitmap = BitmapFactory.decodeResource(getResources(),
                                            R.mipmap.ic_icon_develop_fail);
                                    gazeIsRGBLive.setImageBitmap(failBitmap);
                                }
                            });
                        }
                    }
                    gfDetectCost.setText("检测耗时：" + livenessModel.getRgbDetectDuration() + "ms");
                    gazeDetectCost.setText("注意力检测耗时：" + time + "ms");
                    gaRGBScore.setText("活体检测得分：" + livenessModel.getRgbLivenessScore());
                    gaRGBCost.setText("活体检测耗时：" + livenessModel.getRgbLivenessDuration() + "ms");
                    gazeDebugText.setText("左眼" + identifyLeftGaze(bdFaceGazeInfo)
                            + " 右眼" + identifyRightGaze(bdFaceGazeInfo));
                    gazeResult.setText("左眼" + identifyLeftGaze(bdFaceGazeInfo)
                            + " 右眼" + identifyRightGaze(bdFaceGazeInfo));
                    showDetectImage(BitmapUtils.getInstaceBmp(livenessModel.getBdFaceImageInstance()));
                } else {
                    if (!isRelease) {
                        gazeDebugResult.setVisibility(View.GONE);
                    } else {
                        gazeReleaseResult.setVisibility(View.GONE);
                    }
                    gazeIsRGBLive.setVisibility(View.GONE);
                    gfDetectCost.setText("检测耗时：" + "0ms");
                    gazeDetectCost.setText("注意力检测耗时：" + "0ms");
                    gaRGBScore.setText("活体检测得分：" + "0");
                    gaRGBCost.setText("活体检测耗时：" + "0ms");
//                    gazeDebugText.setText("未检测到人脸");
                    gazeMiddleLiner.setVisibility(View.GONE);
                    gazeResult.setText("未检测到人脸");
                    Bitmap normalBitmap = BitmapFactory.decodeResource(getResources(),
                            R.mipmap.ic_image_video);
                    showDetectImage(normalBitmap);
                }
            }
        });
    }

    public String identifyLeftGaze(BDFaceGazeInfo bdFaceGazeInfo) {
        String result = "";
        boolean isLeftFront = false;
        if (bdFaceGazeInfo != null) {
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_DOWN
            ) {
                result = "向下";
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_LEFT
            ) {
                result = "向左";
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_FRONT
            ) {
                result = "向前";
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_RIGHT
            ) {
                result = "向右";
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_UP
            ) {
                result = "向上";
            }
            if (bdFaceGazeInfo.leftEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_EYE_CLOSE
            ) {
                result = "闭眼";
            }
        }
        return result;
    }

    public String identifyRightGaze(BDFaceGazeInfo bdFaceGazeInfo) {
        String result = "";
        boolean isRightFront = false;
        if (bdFaceGazeInfo != null) {
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_DOWN
            ) {
                result = "向下";
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_LEFT
            ) {
                result = "向左";
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_FRONT
            ) {
                result = "向前";
                isRightFront = true;
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_RIGHT
            ) {
                result = "向右";
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_UP
            ) {
                result = "向上";
            }
            if (bdFaceGazeInfo.rightEyeGaze ==
                    BDFaceSDKCommon.BDFaceGazeDirection.BDFACE_GACE_DIRECTION_EYE_CLOSE
            ) {
                result = "闭眼";
            }
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraPreviewManager.getInstance().stopPreview();
    }

}
