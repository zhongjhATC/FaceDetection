package com.zjh.facedetection.ui.main;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.util.Log;

import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusNewEnum;
import com.baidu.idl.face.platform.IDetectStrategy;
import com.baidu.idl.face.platform.IDetectStrategyCallback;
import com.baidu.idl.face.platform.model.ImageInfo;
import com.baidu.idl.face.platform.stat.Ast;
import com.baidu.idl.face.platform.ui.FaceSDKResSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 人脸检测
 *
 * @author zhongjh
 */
public class FaceDetect implements IDetectStrategyCallback {


    private final Context mContext;
    private final FaceCamera mFaceCamera;

    /**
     * 人脸配置
     */
    protected FaceConfig mFaceConfig;
    protected IDetectStrategy mDetectStrategy;

    // region 状态标识

    protected volatile boolean mIsEnableSound = true;
    protected volatile boolean mIsCompletion = false;

    // endregion

    /**
     * 检测后的最优图片
     */
    ImageInfo mImageInfo = null;

    private Callback mCallback;

    /**
     * 回调
     */
    public interface Callback {
        /**
         * 获取最优图片
         *
         * @param bmpStr 图片实体
         */
        void getBestImage(ImageInfo bmpStr);

        /**
         * 没获取到人脸
         *
         * @param message 消息
         */
        void getNoFaceMessage(String message);

        /**
         * 获取到人脸但并不是最优的
         *
         * @param message 消息
         */
        void getFaceMessage(String message);
    }

    public FaceDetect(Context context, FaceCamera faceCamera) {
        this.mContext = context;
        this.mFaceCamera = faceCamera;
        initFace();
        initVol();
    }

    /**
     * 设置回调
     *
     * @param callback 回调事件
     */
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    /**
     * 初始化人脸信息
     */
    public void initFace() {
        FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();
    }

    /**
     * 初始化vol
     */
    public void initVol() {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int vol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        mIsEnableSound = vol > 0 && mFaceConfig.isSound();
    }

    /**
     * onPause生命周期
     */
    public void onPause() {
        mIsCompletion = false;
    }

    /**
     * 重新检测
     */
    public void reset() {
        mIsCompletion = false;
        if (mDetectStrategy != null) {
            mDetectStrategy.reset();
            mFaceCamera.mFaceDetect.mDetectStrategy = null;
        }
    }

    /**
     * 通过onPreviewFrame采集人脸数据
     *
     * @param data data
     */
    public void onPreviewFrame(byte[] data) {
        if (mIsCompletion) {
            return;
        }

        if (mDetectStrategy == null) {
            mDetectStrategy = FaceSDKManager.getInstance().getDetectStrategyModule();
            mDetectStrategy.setPreviewDegree(mFaceCamera.mPreviewDegree);
            mDetectStrategy.setDetectStrategySoundEnable(mIsEnableSound);

            Rect detectRect = new Rect(0, 0, mFaceCamera.mDisplayWidth, mFaceCamera.mDisplayHeight);

            mDetectStrategy.setDetectStrategyConfig(mFaceCamera.mPreviewRect, detectRect, this);
        } else {
            mDetectStrategy.detectStrategy(data);
        }
    }

    /**
     * 采集完成
     */
    @Override
    public void onDetectCompletion(FaceStatusNewEnum status, String message,
                                   HashMap<String, ImageInfo> base64ImageCropMap,
                                   HashMap<String, ImageInfo> base64ImageSrcMap) {
        if (mIsCompletion) {
            return;
        }

        onRefreshView(status, message);

        if (status == FaceStatusNewEnum.OK) {
            mIsCompletion = true;
        }
        // 打点
        Ast.getInstance().faceHit("detect");

        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            // 获取最优图片
            getBestImage(base64ImageCropMap, base64ImageSrcMap);
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            // 超时
//            if (mViewBg != null) {
//                mViewBg.setVisibility(View.VISIBLE);
//            }
//            showMessageDialog();
        }

    }

    private void onRefreshView(FaceStatusNewEnum status, String message) {
        Log.d("onRefreshView", status.name());
        switch (status) {
            case OK:
                // 正常
            case DetectRemindCodePitchOutofUpRange:
                // 头部偏高
            case DetectRemindCodePitchOutofDownRange:
                // 头部偏低
            case DetectRemindCodeYawOutofLeftRange:
                // 头部偏左
            case DetectRemindCodeYawOutofRightRange:
                // 头部偏右
            case DetectRemindCodePoorIllumination:
                // 光照不足
            case DetectRemindCodeImageBlured:
                // 图像模糊
            case DetectRemindCodeOcclusionLeftEye:
                // 左眼有遮挡
            case DetectRemindCodeOcclusionRightEye:
                // 右眼有遮挡
            case DetectRemindCodeOcclusionNose:
                // 鼻子有遮挡
            case DetectRemindCodeOcclusionMouth:
                // 嘴巴有遮挡
            case DetectRemindCodeOcclusionLeftContour:
                // 左脸颊有遮挡
            case DetectRemindCodeOcclusionRightContour:
                // 右脸颊有遮挡
            case DetectRemindCodeTooClose:
                // 太近
            case DetectRemindCodeTooFar:
                // 太远
                mCallback.getFaceMessage(message);
                break;
            default:
                break;
            case DetectRemindCodeNoFaceDetected:
                // 没有检测到人脸
                mCallback.getNoFaceMessage(message);
                break;
        }
    }

    /**
     * 获取最优图片
     *
     * @param imageCropMap 抠图集合
     * @param imageSrcMap  原图集合
     */
    private void getBestImage(HashMap<String, ImageInfo> imageCropMap, HashMap<String, ImageInfo> imageSrcMap) {
        // 将抠图集合中的图片按照质量降序排序，最终选取质量最优的一张抠图图片
        if (imageCropMap != null && imageCropMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list = new ArrayList<>(imageCropMap.entrySet());
            Collections.sort(list, (o1, o2) -> {
                String[] key1 = o1.getKey().split("_");
                String score1 = key1[2];
                String[] key2 = o2.getKey().split("_");
                String score2 = key2[2];
                // 降序排序
                return Float.valueOf(score2).compareTo(Float.valueOf(score1));
            });
        }

        // 将原图集合中的图片按照质量降序排序，最终选取质量最优的一张原图图片
        if (imageSrcMap != null && imageSrcMap.size() > 0) {
            List<Map.Entry<String, ImageInfo>> list2 = new ArrayList<>(imageSrcMap.entrySet());
            Collections.sort(list2, (o1, o2) -> {
                String[] key1 = o1.getKey().split("_");
                String score1 = key1[2];
                String[] key2 = o2.getKey().split("_");
                String score2 = key2[2];
                // 降序排序
                return Float.valueOf(score2).compareTo(Float.valueOf(score1));
            });
            mImageInfo = list2.get(0).getValue();
        }

        if (mCallback != null) {
            mCallback.getBestImage(mImageInfo);
        }

    }

}
