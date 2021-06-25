package com.zjh.facedetection.ui.main;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.baidu.idl.face.platform.ui.utils.CameraPreviewUtils;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.utils.APIUtils;

/**
 * 里面包含开启摄像头等功能
 *
 * @author zhongjh
 */
public class FaceCamera implements
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback {

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


    // endregion

    public FaceCamera(Context context, int displayWidth, int displayHeight) {
        this.mContext = context;
        initDisplayWidthHeight(displayWidth, displayHeight);
        mFaceDetect = new FaceDetect(context, this);
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

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        mFaceDetect.onPreviewFrame(bytes);
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
            mCamera.setPreviewTexture(mSurfaceTexture);
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
