package com.zjh.facedetection.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.baidu.aip.bodyanalysis.AipBodyAnalysis;
import com.baidu.idl.face.platform.ui.utils.CameraPreviewUtils;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.zjh.facedetection.model.PreviewFrameModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 里面包含开启摄像头等功能
 *
 * @author zhongjh
 */
public class FaceCamera implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback {

    private static final String TAG = FaceCamera.class.getSimpleName();

    private final Context mContext;

    // region surface

    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;

    public SurfaceTexture mSurfaceTexture;

    // endregion

    // region Size

    protected Rect mPreviewRect = new Rect();
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;

    // endregion

    /**
     * 人脸检测
     */
    FaceDetect mFaceDetect;

    // region 状态标识

    protected boolean mIsCreateSurface = false;

    // endregion

    // region 相机

    protected Camera mCamera;
    protected Camera.Parameters mCameraParam;
    protected int mCameraId;
    protected int mPreviewWidth;
    protected int mPreviewHight;
    /**
     * 相机预览角度
     */
    protected int mPreviewDegree;

    MainViewModel.UiChangeObservable mUiChange;

    /**
     * 初始化一个AipBodyAnalysis 这是AR方面的人体识别
     */
    AipBodyAnalysis client = new AipBodyAnalysis("25209607", "hLoeACCQ924GHz1MK2bmT58e", "Npm0aRrAjvm5AYDlt8Zpbe90moLim1GL");
    HashMap<String, String> options = new HashMap<>();
    byte[] mPreviewBuffer;

    // endregion

    public FaceCamera(Context context, int displayWidth, int displayHeight, MainViewModel.UiChangeObservable uiChange) {
        this.mContext = context;
        initDisplayWidthHeight(displayWidth, displayHeight);
        mFaceDetect = new FaceDetect(context, this);

        mUiChange = uiChange;

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        if (mPreviewBuffer == null) {
            //不要二次设置mPreviewBuffer 否则可能会有画面延迟，原因还不知道
            mPreviewBuffer = new byte[mDisplayWidth * mDisplayHeight * 6];
        }

        initSurface();
    }

    /**
     * 初始化获取最大宽高
     */
    private void initDisplayWidthHeight(int displayWidth, int displayHeight) {
        mDisplayWidth = displayWidth;
        // 包括状态栏高度
        mDisplayHeight = displayHeight;
    }

    private void initSurface() {
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        mSurfaceTexture = new SurfaceTexture(10);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onError(int i, Camera camera) {

    }

    boolean isHandle = false;

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
//        mFaceDetect.onPreviewFrame(bytes);

//        mUiChange.onPreviewFrame.setValue(bytes);

        if (isHandle) {
            return;
        }

        Observable.create((ObservableOnSubscribe<PreviewFrameModel>) e -> {
            isHandle = true;
            // TODO 在此处进行网络请求的操作
            PreviewFrameModel previewFrameModel = new PreviewFrameModel();
            byte[] newBytes = convertYuvToJpeg(bytes,camera);
            previewFrameModel.setBytes(newBytes);
            previewFrameModel.setCamera(camera);
            try {
                Log.d(TAG, "请求");
                JSONObject res = client.gesture(newBytes, options);
                Log.d(TAG, res.toString(2));
                e.onNext(previewFrameModel);
            } catch (Exception ex) {
                e.onNext(previewFrameModel);
            }
        })
                // 指定被观察者中的方法在io线程中进行处理
                .subscribeOn(Schedulers.io())
                // 指定观察者接收数据在主线程中
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PreviewFrameModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PreviewFrameModel s) {
                        // TODO 在此处主线程中进行UI的更新
//                        Bitmap bitmap = getPriviewPic(s.getBytes());
                        Log.d(TAG,"释放内存");
                        s.setBytes(null);
                        System.gc();
                        isHandle = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"释放内存");
                        System.gc();
                        isHandle = false;
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        mCamera.addCallbackBuffer(mPreviewBuffer);

    }

    public byte[] convertYuvToJpeg(byte[] data, Camera camera) {

        YuvImage image = new YuvImage(data, ImageFormat.NV21,
                camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 20; //set quality
        image.compressToJpeg(new Rect(0, 0, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height), quality, baos);//this line decreases the image quality


        return baos.toByteArray();
    }

    public Bitmap getPriviewPic(byte[] data) {//这里传入的data参数就是onpreviewFrame中需要传入的byte[]型数据
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();//获取尺寸,格式转换的时候要用到
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                previewSize.width,
                previewSize.height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 100, baos);// 80--JPG图片的质量[0-100],100最高
        byte[] rawImage = baos.toByteArray();
        //将rawImage转换成bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return bitmap;
    }
    /**
     * @return camera
     */
    private Camera open() {
        Camera camera;
        // 如果没摄像头则返回
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        // 全部设置CAMERA_FACING_FRONT前置摄像头
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        // 打开最后一个的摄像头
        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    /**
     * 开始预览
     */
    protected void startPreview() {
        // 初始化 SurfaceHolder
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        // 获取相机
        if (mCamera == null) {
            try {
                mCamera = open();
                if (mCamera != null) {
                    mCamera.addCallbackBuffer(mPreviewBuffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mCamera == null) {
            return;
        }
        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        // 设置jpg格式
        mCameraParam.setPictureFormat(PixelFormat.JPEG);

        // 设置角度
        int degree = displayOrientation(mContext);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;
        // 人脸配置也设置同样角度
        if (mFaceDetect.mDetectStrategy != null) {
            mFaceDetect.mDetectStrategy.setPreviewDegree(degree);
        }

        // 计算xy赋值宽高
        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));
        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432
        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);

        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
//            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }

    /**
     * 设置角度
     *
     * @param context 上下文
     * @return 角度
     */
    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (-degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }




}
