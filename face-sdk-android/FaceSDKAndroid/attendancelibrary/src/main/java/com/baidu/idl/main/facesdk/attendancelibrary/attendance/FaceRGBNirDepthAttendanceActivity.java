package com.baidu.idl.main.facesdk.attendancelibrary.attendance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.attendancelibrary.BaseOrbbecActivity;
import com.baidu.idl.main.facesdk.attendancelibrary.R;
import com.baidu.idl.main.facesdk.attendancelibrary.api.FaceApi;
import com.baidu.idl.main.facesdk.attendancelibrary.callback.CameraDataCallback;
import com.baidu.idl.main.facesdk.attendancelibrary.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.attendancelibrary.camera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.attendancelibrary.camera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.attendancelibrary.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.attendancelibrary.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.attendancelibrary.model.LivenessModel;
import com.baidu.idl.main.facesdk.attendancelibrary.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.attendancelibrary.model.User;
import com.baidu.idl.main.facesdk.attendancelibrary.setting.AttendanceSettingActivity;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.DensityUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.FileUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.TimeUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.utils.ToastUtils;
import com.baidu.idl.main.facesdk.attendancelibrary.view.PreviewTexture;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;


public class FaceRGBNirDepthAttendanceActivity extends BaseOrbbecActivity implements OpenNIHelper.DeviceOpenListener,
        ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {
    private static final int DEPTH_NEED_PERMISSION = 33;

    // RGB????????????????????????
    private static final int RGB_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int RGB_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();

    // NIR????????????????????????
    private static final int PREFER_WIDTH = 640;
    private static final int PERFER_HEIGH = 480;

    // Depth????????????????????????
    private static final int DEPTH_WIDTH = SingleBaseConfig.getBaseConfig().getDepthWidth();
    private static final int DEPTH_HEIGHT = SingleBaseConfig.getBaseConfig().getDepthHeight();

    private Context mContext;

    // ??????????????????
    private TextureView mDrawDetectFaceView;
    private AutoTexturePreviewView mAutoCameraPreviewView;
    private ImageView mFaceDetectImageView;
    private TextView mTvDetect;
    private TextView mTvLive;
    private TextView mTvLiveScore;

    // ??????????????????
    private TextView mTvDepth;
    private TextView mTvDepthScore;
    private TextView mTvIr;
    private TextView mTvIrScore;

    private TextView mTvFeature;
    private TextView mTvAll;
    private TextView mTvAllTime;

    // ??????Depth???
    private OpenNIView mDepthGLView;

    // ???????????????????????????
    private boolean initOk = false;
    // ???????????????
    private Device mDevice;
    private Thread thread;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream mDepthStream;

    private Object sync = new Object();
    // ???????????????????????????
    private boolean exit = false;

    // ?????????????????????
    private static int cameraType;

    // ?????????????????????
    private volatile byte[] rgbData;
    private volatile byte[] depthData;
    private volatile byte[] irData;

    // ???????????????
    private RectF rectF;
    private Paint paint;
    private Paint paintBg;

    private RelativeLayout relativeLayout;
    private float rgbLiveScore;
    private float depthLiveScore;

    // ????????????????????????????????????x?????????y????????????width
    private float[] pointXY = new float[3];
    private boolean requestToInner = false;
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
    private ImageView isRgbCheckImage;
    private ImageView isDepthCheckImage;
    private View preView;
    private View developView;
    private View view;
    private RelativeLayout layoutCompareStatus;
    private TextView textCompareStatus;
    private TextView attendanceTime;
    private TextView attendanceDate;
    private TextView attendanceTimeText;
    private RelativeLayout outRelativelayout;
    private TextView depthSurfaceText;
    private User mUser;

    private LinearLayout rgbSurfaceLl;
    private LinearLayout nirSurfaceLl;
    private LinearLayout depthSurfaceLl;
    private TextureView irPreviewView;
    // ???????????????
    private int mCameraNum;

    // RGB+IR ??????
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;
    private TextView nirSurfaceText;
    private TextView mNum;
    private ImageView isNirCheckImage;
    private float nirLiveScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        initListener();
        FaceSDKManager.getInstance().initDataBases(this);
        setContentView(R.layout.activity_face_rgbnirdepth_attendancelibrary);
        PreferencesUtil.initPrefs(this);
        cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
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
                    ToastUtils.toast(FaceRGBNirDepthAttendanceActivity.this, "?????????????????????????????????");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    FaceSDKManager.initModelSuccess = false;
                    if (errorCode != -12) {
                        ToastUtils.toast(FaceRGBNirDepthAttendanceActivity.this, "??????????????????????????????????????????");
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

        // RGB ??????
        rgbLiveScore = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        // depth ??????
        depthLiveScore = SingleBaseConfig.getBaseConfig().getDepthLiveScore();
        // nir ??????
        nirLiveScore = SingleBaseConfig.getBaseConfig().getNirLiveScore();

        // ????????????
        rectF = new RectF();
        paint = new Paint();
        paintBg = new Paint();
        mDrawDetectFaceView = findViewById(R.id.draw_detect_face_view);
        mDrawDetectFaceView.setOpaque(false);
        mDrawDetectFaceView.setKeepScreenOn(true);


        // ??????
        ImageView mButReturn = findViewById(R.id.btn_back);
        mButReturn.setOnClickListener(this);
        // ??????
        ImageView mBtSetting = findViewById(R.id.btn_setting);
        mBtSetting.setOnClickListener(this);
        // ????????????
        preText = findViewById(R.id.preview_text);
        preText.setOnClickListener(this);
        preText.setTextColor(Color.parseColor("#ffffff"));
        preViewRelativeLayout = findViewById(R.id.yvlan_relativeLayout);
        preView = findViewById(R.id.preview_view);
        // ????????????
        deveLop = findViewById(R.id.develop_text);
        deveLop.setOnClickListener(this);
        deveLopRelativeLayout = findViewById(R.id.kaifa_relativeLayout);
        developView = findViewById(R.id.develop_view);
        developView.setVisibility(View.GONE);
        layoutCompareStatus = findViewById(R.id.layout_compare_status);
        layoutCompareStatus.setVisibility(View.GONE);
        textCompareStatus = findViewById(R.id.text_compare_status);

        // ***************????????????*************
        isRgbCheckImage = findViewById(R.id.is_check_image);
        isDepthCheckImage = findViewById(R.id.depth_is_check_image);
        isNirCheckImage = findViewById(R.id.nir_is_check_image_Iv);

        // ???????????????RGB ????????????
        mAutoCameraPreviewView = findViewById(R.id.auto_camera_preview_view);
        // ??????RGB ????????????
        mFaceDetectImageView = findViewById(R.id.face_detect_image_view);
        mFaceDetectImageView.setVisibility(View.VISIBLE);
        // ???????????????????????????
        mDepthGLView = findViewById(R.id.depth_camera_preview_view);
        mDepthGLView.setVisibility(View.INVISIBLE);

        // ?????????????????????
        mNum = findViewById(R.id.tv_num);
        mNum.setText(String.format("?????? ??? %s ?????????", FaceApi.getInstance().getmUserNum()));

        // ????????????
        mTvDetect = findViewById(R.id.tv_detect_time);
        // RGB??????
        mTvLive = findViewById(R.id.tv_rgb_live_time);
        mTvLiveScore = findViewById(R.id.tv_rgb_live_score);
        // depth??????
        mTvDepth = findViewById(R.id.tv_depth_live_time);
        mTvDepthScore = findViewById(R.id.tv_depth_live_score);
        // Ir??????
        mTvIr = findViewById(R.id.tv_nir_live_time);
        mTvIrScore = findViewById(R.id.tv_nir_live_score);
        // ????????????
        mTvFeature = findViewById(R.id.tv_feature_time);
        // ??????
        mTvAll = findViewById(R.id.tv_feature_search_time);
        // ?????????
        mTvAllTime = findViewById(R.id.tv_all_time);


        // ***************????????????*************
        textHuanying = findViewById(R.id.huanying_relative);
        userNameLayout = findViewById(R.id.user_name_layout);
        nameImage = findViewById(R.id.detect_reg_image_item);
        nameText = findViewById(R.id.name_text);
        detectSurfaceText = findViewById(R.id.detect_surface_text);
        mFaceDetectImageView.setVisibility(View.GONE);
        detectSurfaceText.setVisibility(View.GONE);
        attendanceTime = findViewById(R.id.attendance_time);
        attendanceDate = findViewById(R.id.attendance_date);
        attendanceTimeText = findViewById(R.id.attendance_time_text);
        outRelativelayout = findViewById(R.id.out_relativelayout);
        depthSurfaceText = findViewById(R.id.depth_surface_text);
        depthSurfaceText.setVisibility(View.GONE);

        nirSurfaceText = findViewById(R.id.nir_surface_textTv);
        nirSurfaceText.setVisibility(View.GONE);
        view = findViewById(R.id.mongolia_view);
        view.setAlpha(0.85f);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
        rgbSurfaceLl = findViewById(R.id.rgb_surface_Ll);
        nirSurfaceLl = findViewById(R.id.nir_surface_Ll);
        depthSurfaceLl = findViewById(R.id.depth_surface_Ll);
        rgbSurfaceLl.setVisibility(View.GONE);
        nirSurfaceLl.setVisibility(View.GONE);
        depthSurfaceLl.setVisibility(View.GONE);
        // ???????????????IR ????????????
        irPreviewView = findViewById(R.id.ir_camera_preview_view);
        if (SingleBaseConfig.getBaseConfig().getMirrorNIR() == 1) {
            irPreviewView.setRotationY(180);
        }
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

    /**
     * ???device ?????????????????????USB ??????
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
     * ?????????????????????
     */
    private void startCameraPreview() {
        // ?????????????????????
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_FRONT);
        // ?????????????????????
        // CameraPreviewManager.getInstance().setCameraFacing(CameraPreviewManager.CAMERA_FACING_BACK);
        // ??????USB?????????
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
     * ??????????????????????????????
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
        if (rgbData != null && depthData != null && irData != null) {
            FaceSDKManager.getInstance().onDetectCheck(rgbData, irData, depthData, RGB_HEIGHT,
                    RGB_WIDTH, 4, new FaceDetectCallBack() {
                        @Override
                        public void onFaceDetectCallback(LivenessModel livenessModel) {
                            // ????????????
                            // ????????????
                            checkCloseDebugResult(livenessModel);
                            // ????????????
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
            depthData = null;
            irData = null;
        }
    }

    // ***************????????????????????????*************
    private void checkCloseDebugResult(final LivenessModel livenessModel) {
        // ?????????????????????UI??????
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                attendanceTime.setText(TimeUtils.getTimeShort(date));
                attendanceDate.setText(TimeUtils.getStringDateShort(date) + " "
                        + TimeUtils.getWeek(date));
                if (livenessModel == null) {
                    textHuanying.setVisibility(View.VISIBLE);
                    userNameLayout.setVisibility(View.GONE);
                    return;
                }
                User user = livenessModel.getUser();
                if (user == null) {
                    mUser = null;
                    textHuanying.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    nameImage.setImageResource(R.mipmap.ic_tips_fail);
                    nameText.setTextColor(Color.parseColor("#fec133"));
                    nameText.setText("????????????");
                    attendanceTimeText.setText("???????????????......");
                } else {
                    mUser = user;
                    textHuanying.setVisibility(View.GONE);
                    userNameLayout.setVisibility(View.VISIBLE);
                    String absolutePath = FileUtils.getBatchImportSuccessDirectory()
                            + "/" + user.getImageName();
                    Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
                    nameImage.setImageBitmap(bitmap);
                    nameText.setTextColor(Color.parseColor("#00BAF2"));
                    nameText.setText(user.getUserName() + " ????????????");
                    attendanceTimeText.setText("???????????????" + TimeUtils.getTimeShort(date));
                }

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
                    isRgbCheckImage.setVisibility(View.GONE);
                    isDepthCheckImage.setVisibility(View.GONE);
                    isNirCheckImage.setVisibility(View.GONE);
                    mFaceDetectImageView.setImageResource(R.mipmap.ic_image_video);
                    mTvDetect.setText(String.format("???????????? ???%s ms", 0));
                    mTvLive.setText(String.format("RGB?????????????????? ???%s ms", 0));
                    mTvLiveScore.setText(String.format("RGB???????????? ???%s", 0));
                    mTvDepth.setText(String.format("Depth?????????????????? ???%s ms", 0));
                    mTvDepthScore.setText(String.format("Depth???????????? ???%s", 0));
                    mTvIr.setText(String.format("NIR?????????????????? ???%s ms", 0));
                    mTvIrScore.setText(String.format("NIR???????????? ???%s", 0));
                    mTvFeature.setText(String.format("?????????????????? ???%s ms", 0));
                    mTvAll.setText(String.format("?????????????????? ???%s ms", 0));
                    mTvAllTime.setText(String.format("????????? ???%s ms", 0));
                    return;
                }

                BDFaceImageInstance image = livenessModel.getBdFaceImageInstance();
                if (image != null) {
                    mFaceDetectImageView.setImageBitmap(BitmapUtils.getInstaceBmp(image));
                    image.destory();
                }

                float rgbLivenessScore = livenessModel.getRgbLivenessScore();
                if (rgbLivenessScore < rgbLiveScore) {
                    if (isCheck) {
                        isRgbCheckImage.setVisibility(View.VISIBLE);
                        isRgbCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                } else {
                    if (isCheck) {
                        isRgbCheckImage.setVisibility(View.VISIBLE);
                        isRgbCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }
                }

                float depthLivenessScore = livenessModel.getDepthLivenessScore();
                if (depthLivenessScore < depthLiveScore) {
                    if (isCheck) {
                        isDepthCheckImage.setVisibility(View.VISIBLE);
                        isDepthCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                } else {
                    if (isCheck) {
                        isDepthCheckImage.setVisibility(View.VISIBLE);
                        isDepthCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }
                }

                float nirLivenessScore = livenessModel.getIrLivenessScore();
                if (nirLivenessScore < nirLiveScore) {
                    if (isCheck) {
                        isNirCheckImage.setVisibility(View.VISIBLE);
                        isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_fail);
                    }
                } else {
                    if (isCheck) {
                        isNirCheckImage.setVisibility(View.VISIBLE);
                        isNirCheckImage.setImageResource(R.mipmap.ic_icon_develop_success);
                    }

                }

                if (rgbLivenessScore > rgbLiveScore && depthLivenessScore > depthLiveScore
                        && nirLivenessScore > nirLiveScore) {
                    User user = livenessModel.getUser();
                    if (user == null) {
                        mUser = null;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                            textCompareStatus.setText("???????????????");
                        }
                    } else {
                        mUser = user;
                        if (isCompareCheck) {
                            layoutCompareStatus.setVisibility(View.VISIBLE);
                            textCompareStatus.setTextColor(Color.parseColor("#00BAF2"));
                            textCompareStatus.setText("????????????");
                        }

                    }
                } else {
                    if (isCompareCheck) {
                        layoutCompareStatus.setVisibility(View.VISIBLE);
                        textCompareStatus.setTextColor(Color.parseColor("#fec133"));
                        textCompareStatus.setText("???????????????");
                    }
                }

                mTvDetect.setText(String.format("???????????? ???%s ms", livenessModel.getRgbDetectDuration()));
                mTvLive.setText(String.format("RGB?????????????????? ???%s ms", livenessModel.getRgbLivenessDuration()));
                mTvLiveScore.setText(String.format("RGB???????????? ???%s", livenessModel.getRgbLivenessScore()));
                mTvDepth.setText(String.format("Depth?????????????????? ???%s ms", livenessModel.getDepthtLivenessDuration()));
                mTvDepthScore.setText(String.format("Depth???????????? ???%s", livenessModel.getDepthLivenessScore()));
                mTvIr.setText(String.format("NIR?????????????????? ???%s ms", livenessModel.getIrLivenessDuration()));
                mTvIrScore.setText(String.format("NIR???????????? ???%s", livenessModel.getIrLivenessScore()));
                mTvFeature.setText(String.format("?????????????????? ???%s ms", livenessModel.getFeatureDuration()));
                mTvAll.setText(String.format("?????????????????? ???%s ms", livenessModel.getCheckDuration()));
                mTvAllTime.setText(String.format("????????? ???%s ms", livenessModel.getAllDetectDuration()));
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        // ??????
        if (id == R.id.btn_back) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (thread != null) {
                thread.interrupt();
            }
            finish();
            // ??????
        } else if (id == R.id.btn_setting) {
            if (!FaceSDKManager.initModelSuccess) {
                Toast.makeText(mContext, "SDK????????????????????????????????????",
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (thread != null) {
                thread.interrupt();
            }
            startActivity(new Intent(mContext, AttendanceSettingActivity.class));
            finish();
        } else if (id == R.id.preview_text) {
            irPreviewView.setAlpha(0);
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
            view.setVisibility(View.VISIBLE);
            deveLop.setTextColor(Color.parseColor("#a9a9a9"));
            preText.setTextColor(Color.parseColor("#ffffff"));
            preView.setVisibility(View.VISIBLE);
            developView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.GONE);
            isCheck = false;
            isCompareCheck = false;
            outRelativelayout.setVisibility(View.VISIBLE);
        } else if (id == R.id.develop_text) {
            irPreviewView.setAlpha(1);
            isCheck = true;
            isCompareCheck = true;
            isRgbCheckImage.setVisibility(View.VISIBLE);
            isDepthCheckImage.setVisibility(View.VISIBLE);
            nirSurfaceText.setVisibility(View.VISIBLE);
            mFaceDetectImageView.setVisibility(View.VISIBLE);
            detectSurfaceText.setVisibility(View.VISIBLE);
            developView.setVisibility(View.VISIBLE);
            deveLopRelativeLayout.setVisibility(View.VISIBLE);
            mDepthGLView.setVisibility(View.VISIBLE);
            depthSurfaceText.setVisibility(View.VISIBLE);
            rgbSurfaceLl.setVisibility(View.VISIBLE);
            nirSurfaceLl.setVisibility(View.VISIBLE);
            depthSurfaceLl.setVisibility(View.VISIBLE);
            nirSurfaceText.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            deveLop.setTextColor(Color.parseColor("#ffffff"));
            preText.setTextColor(Color.parseColor("#a9a9a9"));
            preView.setVisibility(View.GONE);
            preViewRelativeLayout.setVisibility(View.GONE);
            outRelativelayout.setVisibility(View.GONE);
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
                mCamera[1] = Camera.open(1);
                mPreview[1].setCamera(mCamera[1], PREFER_WIDTH, PERFER_HEIGH);
                mCamera[1].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        dealIr(data);
                    }
                });
                // ?????????????????????
                startCameraPreview();
                // ????????? ???????????????
                mOpenNIHelper = new OpenNIHelper(this);
                mOpenNIHelper.requestDeviceOpen(this);
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
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

    // ???????????????????????????
    private void isInserLimit(final LivenessModel livenessModel) {
        if (livenessModel == null) {
            requestToInner = false;
            return;
        }
        FaceInfo[] faceInfos = livenessModel.getTrackFaceInfo();
        if (faceInfos == null || faceInfos.length == 0) {
            requestToInner = false;
            return;
        }
        pointXY[0] = livenessModel.getFaceInfo().centerX;
        pointXY[1] = livenessModel.getFaceInfo().centerY;
        pointXY[2] = livenessModel.getFaceInfo().width;
        FaceOnDrawTexturViewUtil.converttPointXY(pointXY, mAutoCameraPreviewView,
                livenessModel.getBdFaceImageInstance(), livenessModel.getFaceInfo().width);
        float lfetLimitX = AutoTexturePreviewView.circleX - AutoTexturePreviewView.circleRadius;
        float rightLimitX = AutoTexturePreviewView.circleX + AutoTexturePreviewView.circleRadius;
        float topLimitY = AutoTexturePreviewView.circleY - AutoTexturePreviewView.circleRadius;
        float bottomLimitY = AutoTexturePreviewView.circleY + AutoTexturePreviewView.circleRadius;

        if (pointXY[0] - pointXY[2] / 2 < lfetLimitX
                || pointXY[0] + pointXY[2] / 2 > rightLimitX
                || pointXY[1] - pointXY[2] / 2 < topLimitY
                || pointXY[1] + pointXY[2] / 2 > bottomLimitY) {
            requestToInner = true;
        } else {
            requestToInner = false;
        }
    }


}
