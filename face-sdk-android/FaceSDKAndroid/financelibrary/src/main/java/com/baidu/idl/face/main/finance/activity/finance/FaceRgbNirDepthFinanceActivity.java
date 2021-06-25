package com.baidu.idl.face.main.finance.activity.finance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.finance.activity.BaseOrbbecActivity;
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
import com.baidu.idl.main.facesdk.utils.PreferencesUtil;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.ImageRegistrationMode;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.SensorType;
import org.openni.VideoFrameRef;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;
import org.openni.android.OpenNIView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class FaceRgbNirDepthFinanceActivity extends BaseOrbbecActivity implements OpenNIHelper.DeviceOpenListener,
        ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final int DEPTH_NEED_PERMISSION = 33;

    // RGB摄像头图像宽和高
    private static final int RGB_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int RGB_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();

    // NIR摄像头图像宽和高
    private static final int PREFER_WIDTH = 640;
    private static final int PERFER_HEIGH = 480;

    // Depth摄像头图像宽和高
    private static final int DEPTH_WIDTH = SingleBaseConfig.getBaseConfig().getDepthWidth();
    private static final int DEPTH_HEIGHT = SingleBaseConfig.getBaseConfig().getDepthHeight();

    private Context mContext;

    // 调试页面控件
    private TextureView mDrawDetectFaceView;
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private ImageView mFaceDetectImageView;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;

    // 深度数据显示
    private TextView mTvDepth;
    private TextView mTvDepthScore;
    private TextView mTvAllTime;

    // 显示Depth图
    private OpenNIView mDepthGLView;

    // 设备初始化状态标记
    private boolean initOk = false;
    // 摄像头驱动
    private Device mDevice;
    private Thread thread;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream mDepthStream;

    private Object sync = new Object();
    // 循环取深度图像数据
    private boolean exit = false;

    // 当前摄像头类型
    private static int cameraType;

    // 摄像头采集数据
    private volatile byte[] rgbData;
    private volatile byte[] depthData;
    private volatile byte[] irData;

    // 人脸框绘制
    private RectF rectF;
    private Paint paint;
    private Paint paintBg;

    private RelativeLayout relativeLayout;

    // 包含适配屏幕后后的人脸的x坐标，y坐标，和width
    private float[] pointXY = new float[4];
    private boolean isCheck = false;
    private boolean isCompareCheck = false;
    private TextView preText;
    private TextView deveLop;
    private RelativeLayout preViewRelativeLayout;
    private RelativeLayout deveLopRelativeLayout;
    private ImageView isRgbCheckImage;
    private ImageView isDepthCheckImage;
    private View preView;
    private View developView;
    private TextView detectSurfaceText;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private TextView depthSurfaceText;
    private TextView preToastText;
    private RelativeLayout progressLayout;
    private ImageView progressBarView;

    private RelativeLayout payHintRl;
    private boolean payHint = false;
    private boolean isTime = true;
    private long searshTime;
    private boolean isNeedCamera = true;
    private boolean mIsPayHint = true;
    private User mUser;
    private boolean count = true;

    private LinearLayout rgbSurfaceLl;
    private LinearLayout nirSurfaceLl;
    private LinearLayout depthSurfaceLl;
    private TextureView irPreviewView;

    private static final String TAG = "face-rgb-ir";

    // 摄像头个数
    private int mCameraNum;

    // RGB+IR 控件
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;
    private TextView nirSurfaceText;
    private ImageView isNirCheckImage;

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

    // 深度数据显示
    private TextView mTvIr;
    private TextView mTvIrScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initListener();
        setContentView(R.layout.activity_face_rgb_nir_depth_finance);
        PreferencesUtil.initPrefs(this);
        cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
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
     * 开启Debug View
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
        mAutoCameraPreviewView.isDraw = true;


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
        preView = findViewById(R.id.preview_view);
        // 信息展示
        preViewRelativeLayout = findViewById(R.id.yvlan_relativeLayout);
        preToastText = findViewById(R.id.pre_toast_text);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarView = findViewById(R.id.progress_bar_view);
        // 预览模式下提示
        payHintRl = findViewById(R.id.pay_hintRl);

        // 开发模式
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        deveLopRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        layoutCompareStatus.setVisibility(View.GONE);
        textCompareStatus = findViewById(R.id.text_compare_status);

        nirSurfaceText = findViewById(R.id.nir_surface_textTv);
        nirSurfaceText.setVisibility(View.GONE);
        isNirCheckImage = findViewById(R.id.nir_is_check_image_Iv);

        // ***************开发模式*************
        detectSurfaceText = findViewById(R.id.detect_surface_text);
        detectSurfaceText.setVisibility(View.GONE);
        depthSurfaceText = findViewById(R.id.depth_surface_text);
        depthSurfaceText.setVisibility(View.GONE);
        isRgbCheckImage = findViewById(R.id.is_check_image);
        isDepthCheckImage = findViewById(R.id.depth_is_check_image);
        // 送检RGB 图像回显
        mFaceDetectImageView = findViewById(R.id.face_detect_image_view);
        mFaceDetectImageView.setVisibility(View.INVISIBLE);
        // 深度摄像头数据回显
        mDepthGLView = findViewById(R.id.depth_camera_preview_view);
        mDepthGLView.setVisibility(View.INVISIBLE);

        // 检测耗时
        mTvDetect = findViewById(R.id.tv_detect_time);
        // RGB活体
        mTvLive = findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = findViewById(R.id.tv_rgb_live_score);
        // depth活体
        mTvDepth = findViewById(R.id.tv_depth_live_time);
        mTvDepthScore = findViewById(R.id.tv_depth_live_score);

        // Ir活体
        mTvIr = findViewById(R.id.tv_nir_live_time);
        mTvIrScore = findViewById(R.id.tv_nir_live_score);
        // 总耗时
        mTvAllTime = findViewById(R.id.tv_all_time);


        rgbSurfaceLl = findViewById(R.id.rgb_surface_Ll);
        nirSurfaceLl = findViewById(R.id.nir_surface_Ll);
        depthSurfaceLl = findViewById(R.id.depth_surface_Ll);
        rgbSurfaceLl.setVisibility(View.GONE);
        nirSurfaceLl.setVisibility(View.GONE);
        depthSurfaceLl.setVisibility(View.GONE);
        // 双目摄像头IR 图像预览
        irPreviewView = findViewById(R.id.ir_camera_preview_view);
        if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == 1) {
            irPreviewView.setRotationY(180);
        }

        // 双摄像头
        mCameraNum = Camera.getNumberOfCameras();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        } else {
            mPreview = new PreviewTexture[mCameraNum];
            mCamera = new Camera[mCameraNum];
            mPreview[1] = new PreviewTexture(this, irPreviewView);
        }
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (count) {
                                isNeedCamera = true;
                            }
                        }
                    }, 500);  // 延迟1秒执行
                } else {
                    // 返回首页
                    finish();
                    testPopWindow.closePopupWindow();
                }
            }
        });
    }

    /**
     * 在device 启动时候初始化USB 驱动
     *
     * @param device
     */
    private void initUsbDevice(UsbDevice device) {

        List<DeviceInfo> opennilist = OpenNI.enumerateDevices();
        if (opennilist.size() <= 0) {
            Toast.makeText(this, " openni enumerateDevices 0 devices", Toast.LENGTH_LONG).show();
            return;
        }
        this.mDevice = null;
        // Find mDevice ID
        for (int i = 0; i < opennilist.size(); i++) {
            if (opennilist.get(i).getUsbProductId() == device.getProductId()) {
                this.mDevice = Device.open();
                break;
            }
        }

        if (this.mDevice == null) {
            Toast.makeText(this, " openni open devices failed: " + device.getDeviceName(),
                    Toast.LENGTH_LONG).show();
            return;
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
        CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_USB);

        CameraPreviewManager.getInstance().startPreview(this, mAutoCameraPreviewView,
                RGB_WIDTH, RGB_HEIGHT, new CameraDataCallback() {
                    @Override
                    public void onGetCameraData(byte[] rgbData, Camera camera, int srcWidth, int srcHeight) {
                        dealRgb(rgbData);
                    }
                });
    }

    @Override
    public void onDeviceOpened(UsbDevice usbDevice) {
        initUsbDevice(usbDevice);
        mDepthStream = VideoStream.create(this.mDevice, SensorType.DEPTH);
        if (mDepthStream != null) {
            List<VideoMode> mVideoModes = mDepthStream.getSensorInfo().getSupportedVideoModes();
            for (VideoMode mode : mVideoModes) {
                int x = mode.getResolutionX();
                int y = mode.getResolutionY();
                int fps = mode.getFps();
                if (cameraType == 1) {
                    if (x == DEPTH_HEIGHT && y == DEPTH_WIDTH && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                        mDepthStream.setVideoMode(mode);
                        this.mDevice.setImageRegistrationMode(ImageRegistrationMode.DEPTH_TO_COLOR);
                        break;
                    }
                } else {
                    if (x == DEPTH_WIDTH && y == DEPTH_HEIGHT && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                        mDepthStream.setVideoMode(mode);
                        this.mDevice.setImageRegistrationMode(ImageRegistrationMode.DEPTH_TO_COLOR);
                        break;
                    }
                }

            }
            startThread();
        }
    }

    /**
     * 开启线程接收深度数据
     */
    private void startThread() {
        initOk = true;
        thread = new Thread() {

            @Override
            public void run() {

                List<VideoStream> streams = new ArrayList<VideoStream>();

                streams.add(mDepthStream);
                mDepthStream.start();
                while (!exit) {

                    try {
                        OpenNI.waitForAnyStream(streams, 2000);

                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        continue;
                    }

                    synchronized (sync) {
                        if (mDepthStream != null) {
                            mDepthGLView.update(mDepthStream);
                            VideoFrameRef videoFrameRef = mDepthStream.readFrame();
                            ByteBuffer depthByteBuf = videoFrameRef.getData();
                            if (depthByteBuf != null) {
                                int depthLen = depthByteBuf.remaining();
                                byte[] depthByte = new byte[depthLen];
                                depthByteBuf.get(depthByte);
                                dealDepth(depthByte);
                            }
                            videoFrameRef.release();
                        }
                    }

                }
            }
        };

        thread.start();
    }

    private void dealDepth(byte[] data) {
        depthData = data;
        checkData();
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
        if (rgbData != null && depthData != null && irData != null) {
            FinanceFaceSDKManager.getInstance().onDetectCheck(rgbData, irData, depthData, RGB_HEIGHT,
                    RGB_WIDTH, 4, new FaceDetectCallBack() {
                        @Override
                        public void onFaceDetectCallback(LivenessModel livenessModel) {
                            // 输出结果
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
                            if (!mAutoCameraPreviewView.isDraw) {
                                showFrame(livenessModel);
                            }
                        }
                    });
            rgbData = null;
            irData = null;
            depthData = null;
        }
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
                        }, 1 * 500);  // 延迟1秒执行
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
//                preToastText.setTextColor(Color.parseColor("#FFFFFF"));
//                preToastText.setText("正在识别中...");
                progressBarView.setImageResource(R.mipmap.ic_loading_blue);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //   todo somthing here
                        if (count) {
                            count = false;
                            payHint(livenessModel);
                        }
                    }
                }, 1 * 500);  // 延迟1秒执行

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
                    isRgbCheckImage.setVisibility(View.GONE);
                    isDepthCheckImage.setVisibility(View.GONE);
                    isNirCheckImage.setVisibility(View.GONE);
                    mFaceDetectImageView.setImageResource(R.mipmap.ic_image_video);
                    mTvDetect.setText(String.format("检测耗时 ：%s ms", 0));
                    mTvLive.setText(String.format("RGB活体检测耗时 ：%s ms", 0));
                    mTvLiveScore.setText(String.format("RGB活体检测结果 ：%s", false));
                    mTvIr.setText(String.format("NIR活体检测耗时 ：%s ms", 0));
                    mTvIrScore.setText(String.format("NIR活体检测结果 ：%s", false));
                    mTvDepth.setText(String.format("Depth活体检测耗时 ：%s ms", 0));
                    mTvDepthScore.setText(String.format("Depth活体检测结果 ：%s", false));
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
                    if (livenessModel.getListOcclusion().size() != 0 && livenessModel.getListDetected().size() != 0) {
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                            textCompareStatus.setText("未通过质量检测");
                        }
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
                if (livenessModel.isDepthLiveStatus()) {
                    if (isCheck) {
                        isDepthCheckImage.setVisibility(View.VISIBLE);
                        isDepthCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }
                } else {
                    if (isCheck) {
                        isDepthCheckImage.setVisibility(View.VISIBLE);
                        isDepthCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                }
                if (livenessModel.isRGBLiveStatus() && livenessModel.isNIRLiveStatus()
                        && livenessModel.isDepthLiveStatus()) {
                    User user = livenessModel.getUser();
                    if (user == null) {
                        mUser = null;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                            textCompareStatus.setText("通过活体检测");
                        }

                    } else {
                        mUser = user;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                            textCompareStatus.setText("未通过活体检测");
                        }

                    }
                }

                mTvDetect.setText(String.format("检测耗时 ：%s ms", livenessModel.getRgbDetectDuration()));
                mTvLive.setText(String.format("RGB活体检测耗时 ：%s ms", livenessModel.getRgbLivenessDuration()));
                mTvLiveScore.setText(String.format("RGB活体检测结果 ：%s", livenessModel.isRGBLiveStatus()));
                mTvDepth.setText(String.format("Depth活体检测耗时 ：%s ms", livenessModel.getDepthtLivenessDuration()));
                mTvDepthScore.setText(String.format("Depth活体检测结果 ：%s", livenessModel.isDepthLiveStatus()));
                mTvIr.setText(String.format("NIR活体检测耗时 ：%s ms", livenessModel.getIrLivenessDuration()));
                mTvIrScore.setText(String.format("NIR活体检测结果 ：%s", livenessModel.isNIRLiveStatus()));
                mTvAllTime.setText(String.format("总耗时 ：%s ms", livenessModel.getAllDetectDuration()));
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        // 返回
        if (id == R.id.btn_back) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            finish();
            // 设置
        } else if (id == R.id.btn_setting) {
            if (!FinanceFaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK正在加载模型，请稍后再试",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (thread != null) {
                thread.interrupt();
            }
            startActivity(new Intent(mContext, FinanceSettingActivity.class));
            finish();
        } else if (id == R.id.preview_text) {
            irPreviewView.setAlpha(0);
            if (payHintRl.getVisibility() == View.VISIBLE) {
                return;
            }
            isRgbCheckImage.setVisibility(View.GONE);
            isDepthCheckImage.setVisibility(View.GONE);
            mFaceDetectImageView.setVisibility(View.GONE);
            detectSurfaceText.setVisibility(View.GONE);
            layoutCompareStatus.setVisibility(View.GONE);
            mDepthGLView.setVisibility(View.GONE);
            depthSurfaceText.setVisibility(View.GONE);

            rgbSurfaceLl.setVisibility(View.GONE);
            nirSurfaceLl.setVisibility(View.GONE);
            depthSurfaceLl.setVisibility(View.GONE);

            nirSurfaceText.setVisibility(View.GONE);

            deveLop.setTextColor(Color.parseColor("#a9a9a9"));
            preText.setTextColor(Color.parseColor("#ffffff"));
            preView.setVisibility(View.VISIBLE);
            developView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            preToastText.setVisibility(View.VISIBLE);
            mDrawDetectFaceView.setVisibility(View.GONE);
            progressBarView.setVisibility(View.VISIBLE);
            payHintRl.setVisibility(View.GONE);
            isCheck = false;
            isCompareCheck = false;
            mAutoCameraPreviewView.isDraw = true;
            mIsPayHint = true;
            count = true;
        } else if (id == R.id.develop_text) {
            irPreviewView.setAlpha(1);
            mIsPayHint = false;
            isNeedCamera = true;
            isCheck = true;
            isCompareCheck = true;
            mAutoCameraPreviewView.isDraw = false;
            count = false;
            isRgbCheckImage.setVisibility(View.VISIBLE);
            isDepthCheckImage.setVisibility(View.VISIBLE);
            mFaceDetectImageView.setVisibility(View.VISIBLE);
            detectSurfaceText.setVisibility(View.VISIBLE);
            developView.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.VISIBLE);
            mDepthGLView.setVisibility(View.VISIBLE);
            depthSurfaceText.setVisibility(View.VISIBLE);
            mDrawDetectFaceView.setVisibility(View.VISIBLE);
            rgbSurfaceLl.setVisibility(View.VISIBLE);
            nirSurfaceLl.setVisibility(View.VISIBLE);
            depthSurfaceLl.setVisibility(View.VISIBLE);
            nirSurfaceText.setVisibility(View.VISIBLE);

            testPopWindow.closePopupWindow();
            financeQualityTestFailed.setVisibility(View.GONE);
            financeFailedInVivoTest.setVisibility(View.GONE);
            financeByLivingDetection.setVisibility(View.GONE);

            deveLop.setTextColor(Color.parseColor("#ffffff"));
            preText.setTextColor(Color.parseColor("#a9a9a9"));
            preView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);
            preToastText.setVisibility(View.GONE);
            progressBarView.setVisibility(View.GONE);
            payHintRl.setVisibility(View.GONE);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                // 摄像头图像预览
                startCameraPreview();
                mCamera[1] = Camera.open(1);
                mPreview[1].setCamera(mCamera[1], PREFER_WIDTH, PERFER_HEIGH);
                mCamera[1].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        dealIr(data);
                    }
                });
                // 初始化 深度摄像头
                mOpenNIHelper = new OpenNIHelper(this);
                mOpenNIHelper.requestDeviceOpen(this);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        exit = true;
        if (initOk) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDepthStream != null) {
                mDepthStream.stop();
                mDepthStream.destroy();
                mDepthStream = null;
            }
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
        }
        if (mOpenNIHelper != null) {
            mOpenNIHelper.shutdown();
            mOpenNIHelper = null;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        exit = true;
        if (initOk) {
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mDepthStream != null) {
                mDepthStream.stop();
                mDepthStream.destroy();
                mDepthStream = null;
            }
            if (mDevice != null) {
                mDevice.close();
                mDevice = null;
            }
        }
        if (mOpenNIHelper != null) {
            mOpenNIHelper.shutdown();
            mOpenNIHelper = null;
        }
    }

    @Override
    public void onDeviceOpenFailed(String msg) {
        showAlertAndExit("Open Device failed: " + msg);
    }

    @Override
    public void onDeviceNotFound() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == DEPTH_NEED_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Permission Grant", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
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

    private void showAlertAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void payHint(final LivenessModel livenessModel) {
        if (livenessModel == null && mIsPayHint) {
            if (!this.isFinishing()) {
                testPopWindow.showPopupWindow(FaceRgbNirDepthFinanceActivity.this.getWindow().getDecorView());
                isNeedCamera = true;
            }
        }

        if (livenessModel != null && mIsPayHint) {
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
                    isNeedCamera = true;
                    count = false;
//                    preToastText.setVisibility(View.GONE);

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
                    }
                    if (stringBufferOcclusion.toString() == "") {
                        qualityShelteredPart.setVisibility(View.GONE);
                    }

                    qualityDetectedTv.setText("检测到：" + stringBufferDetected.toString());
                    qualityShelteredPart.setText("遮挡部位：" + stringBufferOcclusion.toString());
                }
            } else {
                if (livenessModel.isRGBLiveStatus() && livenessModel.isNIRLiveStatus()
                        && livenessModel.isDepthLiveStatus()) {
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
}
