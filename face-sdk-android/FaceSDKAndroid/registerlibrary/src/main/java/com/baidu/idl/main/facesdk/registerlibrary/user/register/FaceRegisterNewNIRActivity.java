package com.baidu.idl.main.facesdk.registerlibrary.user.register;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.registerlibrary.R;
import com.baidu.idl.main.facesdk.registerlibrary.user.activity.BaseActivity;
import com.baidu.idl.main.facesdk.registerlibrary.user.api.FaceApi;
import com.baidu.idl.main.facesdk.registerlibrary.user.callback.CameraDataCallback;
import com.baidu.idl.main.facesdk.registerlibrary.user.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.registerlibrary.user.camera.AutoTexturePreviewView;
import com.baidu.idl.main.facesdk.registerlibrary.user.camera.CameraPreviewManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.registerlibrary.user.manager.FaceSDKManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.LivenessModel;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.User;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.DensityUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.FaceOnDrawTexturViewUtil;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.FileUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.ToastUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.CircleImageView;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.FaceRoundProView;
import com.baidu.idl.main.facesdk.registerlibrary.user.view.PreviewTexture;

import java.io.File;
import java.util.List;

/**
 * 新人脸注册页面（红外）
 * Created by v_liujialu01 on 2020/02/19.
 */
public class FaceRegisterNewNIRActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = FaceRegisterNewNIRActivity.class.getSimpleName();

    // RGB摄像头图像宽和高
    private static final int PREFER_WIDTH = SingleBaseConfig.getBaseConfig().getRgbAndNirWidth();
    private static final int PERFER_HEIGHT = SingleBaseConfig.getBaseConfig().getRgbAndNirHeight();

    private AutoTexturePreviewView mAutoTexturePreviewView;
    private TextureView irPreviewView;
    private FaceRoundProView mFaceRoundProView;
    private RelativeLayout mRelativePreview;     // 预览相关布局

    // 采集相关布局
    private RelativeLayout mRelativeCollectSuccess;
    private CircleImageView mCircleHead;
    private EditText mEditName;
    private TextView mTextError;
    private Button mBtnCollectConfirm;
    private ImageView mImageInputClear;

    // 注册成功相关布局
    private RelativeLayout mRelativeRegisterSuccess;
    private CircleImageView mCircleRegSucHead;

    // 包含适配屏幕后的人脸的x坐标，y坐标，和width
    private float[] mPointXY = new float[4];
    private byte[] mFeatures = new byte[512];
    private Bitmap mCropBitmap;
    private boolean mCollectSuccess;

    // RGB+IR 控件
    private PreviewTexture[] mPreview;
    private Camera[] mCamera;
    // 摄像头个数
    private int mCameraNum;
    // 摄像头采集数据
    private volatile byte[] rgbData;
    private volatile byte[] irData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListener();
        setContentView(R.layout.activity_new_registerlibrary_nir);
        initView();
        FaceSDKManager.getInstance().setCropFace(true);
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
                    ToastUtils.toast(FaceRegisterNewNIRActivity.this, "模型加载成功，欢迎使用");
                }

                @Override
                public void initModelFail(int errorCode, String msg) {
                    if (errorCode != -12) {
                        ToastUtils.toast(FaceRegisterNewNIRActivity.this, "模型加载失败，请尝试重启应用");
                    }
                }
            });
        }
    }

    private void initView() {
        mAutoTexturePreviewView = findViewById(R.id.auto_camera_preview_view);
        mAutoTexturePreviewView.setIsRegister(true);
        mFaceRoundProView = findViewById(R.id.round_view);
        mRelativePreview = findViewById(R.id.relative_preview);

        mRelativeCollectSuccess = findViewById(R.id.relative_collect_success);
        mCircleHead = findViewById(R.id.circle_head);
        mCircleHead.setBorderWidth(DensityUtils.dip2px(FaceRegisterNewNIRActivity.this,
                3));
        mCircleHead.setBorderColor(Color.parseColor("#0D9EFF"));
        mEditName = findViewById(R.id.edit_name);
        mTextError = findViewById(R.id.text_error);
        mBtnCollectConfirm = findViewById(R.id.btn_collect_confirm);
        mBtnCollectConfirm.setOnClickListener(this);
        mImageInputClear = findViewById(R.id.image_input_delete);
        mImageInputClear.setOnClickListener(this);

        mRelativeRegisterSuccess = findViewById(R.id.relative_register_success);
        mCircleRegSucHead = findViewById(R.id.circle_reg_suc_head);
        findViewById(R.id.btn_return_home).setOnClickListener(this);
        findViewById(R.id.btn_continue_reg).setOnClickListener(this);

        ImageView imageBack = findViewById(R.id.image_register_back);
        imageBack.setOnClickListener(this);

        // 输入框监听事件
        mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mImageInputClear.setVisibility(View.VISIBLE);
                    mBtnCollectConfirm.setEnabled(true);
                    mBtnCollectConfirm.setTextColor(Color.WHITE);
                    mBtnCollectConfirm.setBackgroundResource(R.drawable.button_selector);
                    List<User> listUsers = FaceApi.getInstance().getUserListByUserName(s.toString());
                    if (listUsers != null && listUsers.size() > 0) {     // 出现用户名重复
                        mTextError.setVisibility(View.VISIBLE);
                        mBtnCollectConfirm.setEnabled(false);
                    } else {
                        mTextError.setVisibility(View.INVISIBLE);
                        mBtnCollectConfirm.setEnabled(true);
                    }
                } else {
                    mImageInputClear.setVisibility(View.GONE);
                    mBtnCollectConfirm.setEnabled(false);
                    mBtnCollectConfirm.setTextColor(Color.parseColor("#666666"));
                    mBtnCollectConfirm.setBackgroundResource(R.mipmap.btn_all_d);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        irPreviewView = findViewById(R.id.ir_preview_view);
        // 双摄像头
        mCameraNum = Camera.getNumberOfCameras();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        }
        mPreview = new PreviewTexture[mCameraNum];
        mCamera = new Camera[mCameraNum];
        mPreview[1] = new PreviewTexture(this, irPreviewView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraNum < 2) {
            Toast.makeText(this, "未检测到2个摄像头", Toast.LENGTH_LONG).show();
            return;
        } else {
            try {
                startCameraPreview();
                mCamera[1] = Camera.open(1);
                mPreview[1].setCamera(mCamera[1], PREFER_WIDTH, PERFER_HEIGHT);
                mCamera[1].setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if (!mCollectSuccess) {
                            dealIr(data);
                        }
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭摄像头
        CameraPreviewManager.getInstance().stopPreview();
        FaceSDKManager.getInstance().setCropFace(false);
        if (mCropBitmap != null) {
            if (!mCropBitmap.isRecycled()) {
                mCropBitmap.recycle();
            }
            mCropBitmap = null;
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

        CameraPreviewManager.getInstance().startPreview(this, mAutoTexturePreviewView,
                PREFER_WIDTH, PERFER_HEIGHT, new CameraDataCallback() {

                    @Override
                    public void onGetCameraData(byte[] data, Camera camera, int width, int height) {
                        if (mCollectSuccess) {
                            return;
                        }
                        // 摄像头数据处理
                        dealRgb(data);
                    }
                });
    }

    private void dealRgb(byte[] data) {
        rgbData = data;
        faceDetect();
    }

    private void dealIr(byte[] data) {
        irData = data;
        faceDetect();
    }

    /**
     * 摄像头数据处理
     */
    private void faceDetect() {
        if (mCollectSuccess) {
            return;
        }

        // 摄像头预览数据进行人脸检测
        FaceSDKManager.getInstance().onDetectCheck(rgbData, irData, null, PERFER_HEIGHT,
                PREFER_WIDTH, 2, 2, new FaceDetectCallBack() {
                    @Override
                    public void onFaceDetectCallback(LivenessModel livenessModel) {
                        checkFaceBound(livenessModel);
                    }

                    @Override
                    public void onTip(int code, final String msg) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mFaceRoundProView == null) {
                                    return;
                                }
                                mFaceRoundProView.setTipText("保持面部在取景框内");
                                mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                            }
                        });
                    }

                    @Override
                    public void onFaceDetectDarwCallback(LivenessModel livenessModel) {

                    }
                });
    }

    /**
     * 检查人脸边界
     *
     * @param livenessModel LivenessModel实体
     */
    private void checkFaceBound(final LivenessModel livenessModel) {
        // 当未检测到人脸UI显示
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (livenessModel == null || livenessModel.getFaceInfo() == null) {
                    mFaceRoundProView.setTipText("保持面部在取景框内");
                    mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    return;
                }

                mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_grey);

                mPointXY[0] = livenessModel.getFaceInfo().centerX;   // 人脸X坐标
                mPointXY[1] = livenessModel.getFaceInfo().centerY;   // 人脸Y坐标
                mPointXY[2] = livenessModel.getFaceInfo().width;     // 人脸宽度
                mPointXY[3] = livenessModel.getFaceInfo().height;    // 人脸高度

                FaceOnDrawTexturViewUtil.converttPointXY(mPointXY, mAutoTexturePreviewView,
                        livenessModel.getBdFaceImageInstance(), livenessModel.getFaceInfo().width);

                float leftLimitX = AutoTexturePreviewView.circleX - AutoTexturePreviewView.circleRadius;
                float rightLimitX = AutoTexturePreviewView.circleX + AutoTexturePreviewView.circleRadius;
                float topLimitY = AutoTexturePreviewView.circleY - AutoTexturePreviewView.circleRadius;
                float bottomLimitY = AutoTexturePreviewView.circleY + AutoTexturePreviewView.circleRadius;
                float previewWidth = AutoTexturePreviewView.circleRadius * 2;

//                Log.e(TAG, "faceX = " + mPointXY[0] + ", faceY = " + mPointXY[1]
//                        + ", faceW = " + mPointXY[2] + ", prw = " + previewWidth);
//                Log.e(TAG, "leftLimitX = " + leftLimitX + ", rightLimitX = " + rightLimitX
//                        + ", topLimitY = " + topLimitY + ", bottomLimitY = " + bottomLimitY);
//                Log.e(TAG, "cX = " + AutoTexturePreviewView.circleX + ", cY = " + AutoTexturePreviewView.circleY
//                        + ", cR = " + AutoTexturePreviewView.circleRadius);

                if (mPointXY[2] < 50 || mPointXY[3] < 50) {
                    mFaceRoundProView.setTipText("请向前靠近镜头");
                    mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                if (mPointXY[2] > previewWidth || mPointXY[3] > previewWidth) {
                    mFaceRoundProView.setTipText("请向后远离镜头");
                    mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                if (mPointXY[0] - mPointXY[2] / 2 < leftLimitX
                        || mPointXY[0] + mPointXY[2] / 2 > rightLimitX
                        || mPointXY[1] - mPointXY[3] / 2 < topLimitY
                        || mPointXY[1] + mPointXY[3] / 2 > bottomLimitY) {
                    mFaceRoundProView.setTipText("保持面部在取景框内");
                    mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                    // 释放内存
                    destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
                    return;
                }

                mFaceRoundProView.setTipText("请保持面部在取景框内");
                mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_blue);
                // 检验活体分值
                checkLiveScore(livenessModel);
            }
        });
    }

    /**
     * 检验活体分值
     *
     * @param livenessModel LivenessModel实体
     */
    private void checkLiveScore(LivenessModel livenessModel) {
        if (livenessModel == null || livenessModel.getFaceInfo() == null) {
            mFaceRoundProView.setTipText("保持面部在取景框内");
            return;
        }

        float rgbLivenessScore = livenessModel.getRgbLivenessScore();
        float irLivenessScore = livenessModel.getIrLivenessScore();
        float liveThreadHold = SingleBaseConfig.getBaseConfig().getRgbLiveScore();
        float liveIrThreadHold = SingleBaseConfig.getBaseConfig().getNirLiveScore();
        Log.e(TAG, "score = " + rgbLivenessScore + ", ns = " + irLivenessScore);
        if (rgbLivenessScore < liveThreadHold || irLivenessScore < liveIrThreadHold) {
            mFaceRoundProView.setTipText("活体检测未通过");
            mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
            // 释放内存
            destroyImageInstance(livenessModel.getBdFaceImageInstanceCrop());
            return;
        }
        // 提取特征值
        getFeatures(livenessModel);
    }

    /**
     * 提取特征值
     *
     * @param model 人脸数据
     */
    private void getFeatures(final LivenessModel model) {
        if (model == null) {
            return;
        }

        float ret = model.getFeatureCode();
        displayCompareResult(ret, model.getFeature(), model);
    }

    // 根据特征抽取的结果 注册人脸
    private void displayCompareResult(float ret, byte[] faceFeature, LivenessModel model) {
        if (model == null) {
            mFaceRoundProView.setTipText("保持面部在取景框内");
            mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
            return;
        }

        // 特征提取成功
        if (ret == 128) {
            BDFaceImageInstance cropInstance = model.getBdFaceImageInstanceCrop();
            if (cropInstance == null) {
                mFaceRoundProView.setTipText("抠图失败");
                mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
                return;
            }
            mCropBitmap = BitmapUtils.getInstaceBmp(cropInstance);
            // 获取头像
            if (mCropBitmap != null) {
                mCollectSuccess = true;
                mCircleHead.setImageBitmap(mCropBitmap);
            }
            cropInstance.destory();

            mRelativeCollectSuccess.setVisibility(View.VISIBLE);
            mRelativePreview.setVisibility(View.GONE);
            mFaceRoundProView.setTipText("");

            for (int i = 0; i < faceFeature.length; i++) {
                mFeatures[i] = faceFeature[i];
            }
        } else {
            mFaceRoundProView.setTipText("特征提取失败");
            mFaceRoundProView.setBitmapSource(R.mipmap.ic_loading_red);
        }
    }

    /**
     * 释放图像
     *
     * @param imageInstance
     */
    private void destroyImageInstance(BDFaceImageInstance imageInstance) {
        if (imageInstance != null) {
            imageInstance.destory();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.image_register_back) {    // 返回
            finish();
        } else if (id == R.id.btn_collect_confirm) {   // 用户名注册
            String userName = mEditName.getText().toString();
//                if (TextUtils.isEmpty(userName)) {
//                    ToastUtils.toast(getApplicationContext(), "请先输入用户名");
//                    return;
//                }
//                if (userName.length() > 10) {
//                    ToastUtils.toast(getApplicationContext(), "用户名长度不得大于10位");
//                    return;
//                }
            // 姓名过滤
            String nameResult = FaceApi.getInstance().isValidName(userName);
            if (!"0".equals(nameResult)) {
                ToastUtils.toast(getApplicationContext(), nameResult);
                return;
            }
            String imageName = userName + ".jpg";
            // 注册到人脸库
            boolean isSuccess = FaceApi.getInstance().registerUserIntoDBmanager(null,
                    userName, imageName, null, mFeatures);
            if (isSuccess) {
                // 保存人脸图片
                File faceDir = FileUtils.getBatchImportSuccessDirectory();
                File file = new File(faceDir, imageName);
                FileUtils.saveBitmap(file, mCropBitmap);
                // 数据变化，更新内存
                FaceApi.getInstance().initDatabases(true);
                // 更新UI
                mRelativeCollectSuccess.setVisibility(View.GONE);
                mRelativeRegisterSuccess.setVisibility(View.VISIBLE);
                mCircleRegSucHead.setImageBitmap(mCropBitmap);
            } else {
                ToastUtils.toast(getApplicationContext(), "保存数据库失败，" +
                        "可能是用户名格式不正确");
            }
        } else if (id == R.id.btn_continue_reg) {      // 继续注册
            if (mRelativeRegisterSuccess.getVisibility() == View.VISIBLE) {
                mRelativeRegisterSuccess.setVisibility(View.GONE);
            }
            mRelativePreview.setVisibility(View.VISIBLE);
            mFaceRoundProView.setTipText("");
            mCollectSuccess = false;
            mEditName.setText("");
        } else if (id == R.id.btn_return_home) {       // 回到首页
            // 关闭摄像头
            CameraPreviewManager.getInstance().stopPreview();
            finish();
        } else if (id == R.id.image_input_delete) {   // 清除输入
            mEditName.setText("");
            mTextError.setVisibility(View.INVISIBLE);
        }
    }

}
