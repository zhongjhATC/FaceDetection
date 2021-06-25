package com.baidu.idl.face.main.finance.manager;

import android.content.Context;
import android.util.Log;

import com.baidu.idl.face.main.finance.callback.FaceDetectCallBack;
import com.baidu.idl.face.main.finance.listener.SdkInitListener;
import com.baidu.idl.face.main.finance.model.GlobalSet;
import com.baidu.idl.face.main.finance.model.LivenessModel;
import com.baidu.idl.face.main.finance.model.SingleBaseConfig;

import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FinanceFaceSDKManager {

    public static final int SDK_MODEL_LOAD_SUCCESS = 0;
    public static final int SDK_UNACTIVATION = 1;
    public static final int SDK_UNINIT = 2;
    public static final int SDK_INITING = 3;
    public static final int SDK_INITED = 4;
    public static final int SDK_INIT_FAIL = 5;
    public static final int SDK_INIT_SUCCESS = 6;


    public static volatile int initStatus = SDK_UNACTIVATION;
    public static volatile boolean initModelSuccess = false;
    private FaceAuth faceAuth;
    private FaceDetect faceDetect;
    private FaceFeature faceFeature;
    private FaceLive faceLiveness;


    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;
    private ExecutorService es2 = Executors.newSingleThreadExecutor();
    private Future future2;


    private FaceDetect faceDetectNir;


    private ArrayList<String> listDetected = new ArrayList<>();
    private ArrayList<String> listOcclusion = new ArrayList<>();


    private FinanceFaceSDKManager() {
        faceAuth = new FaceAuth();
        faceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 1);
        faceAuth.setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode.BDFACE_LITE_POWER_LOW, 2);
    }

    private static class HolderClass {
        private static final FinanceFaceSDKManager instance = new FinanceFaceSDKManager();
    }

    public static FinanceFaceSDKManager getInstance() {
        return HolderClass.instance;
    }

    public FaceDetect getFaceDetect() {
        return faceDetect;
    }

    public FaceLive getFaceLiveness() {
        return faceLiveness;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    /**
     * 初始化模型，目前包含检查，活体，识别模型；因为初始化是顺序执行，可以在最好初始化回掉中返回状态结果
     *
     * @param context
     * @param listener
     */
    public void initModel(final Context context, final SdkInitListener listener) {

        // 默认检测
        BDFaceInstance bdFaceInstance = new BDFaceInstance();
        bdFaceInstance.creatInstance();
        faceDetect = new FaceDetect(bdFaceInstance);
        // 默认识别
        faceFeature = new FaceFeature();
        // 红外检测
        BDFaceInstance IrBdFaceInstance = new BDFaceInstance();
        IrBdFaceInstance.creatInstance();
        faceDetectNir = new FaceDetect(IrBdFaceInstance);

        faceLiveness = new FaceLive();

        initConfig();

        final long startInitModelTime = System.currentTimeMillis();

        faceDetect.initModel(context,
                GlobalSet.DETECT_VIS_MODEL,
                GlobalSet.ALIGN_TRACK_MODEL,
                BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });

        faceDetect.initModel(context,
                GlobalSet.DETECT_VIS_MODEL,
                GlobalSet.ALIGN_RGB_MODEL, BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });
        faceDetectNir.initModel(context,
                GlobalSet.DETECT_NIR_MODE,
                GlobalSet.ALIGN_NIR_MODEL, BDFaceSDKCommon.DetectType.DETECT_NIR,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });

        faceDetect.initQuality(context,
                GlobalSet.BLUR_MODEL,
                GlobalSet.OCCLUSION_MODEL, new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });

        faceDetect.initAttrEmo(context, GlobalSet.ATTRIBUTE_MODEL, GlobalSet.EMOTION_MODEL, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });

        faceLiveness.initModel(context,
                GlobalSet.LIVE_VIS_MODEL,
                GlobalSet.LIVE_NIR_MODEL,
                GlobalSet.LIVE_DEPTH_MODEL,
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code != 0) {
                            if (listener != null) {
                                listener.initModelFail(code, response);
                            }
                        } else {
                            initStatus = SDK_MODEL_LOAD_SUCCESS;
                            if (listener != null) {
                                listener.initModelSuccess();
                            }
                        }
                    }
                });
    }


    /**
     * 初始化配置
     *
     * @return
     */
    public boolean initConfig() {
        if (faceDetect != null) {
            BDFaceSDKConfig config = new BDFaceSDKConfig();
            // TODO: 最小人脸个数检查，默认设置为1,用户根据自己需求调整
            config.maxDetectNum = 1;

            // TODO: 默认为80px。可传入大于30px的数值，小于此大小的人脸不予检测，生效时间第一次加载模型
            config.minFaceSize = SingleBaseConfig.getBaseConfig().getMinimumFace();

            // 是否进行属性检测，默认关闭
            config.isAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
//            // TODO: 模糊，遮挡，光照三个质量检测和姿态角查默认关闭，如果要开启，设置页启动
            config.isCheckBlur = config.isOcclusion
                    = config.isIllumination = config.isHeadPose
                    = SingleBaseConfig.getBaseConfig().isQualityControl();

            faceDetect.loadConfig(config);

            return true;
        }
        return false;
    }

    /**
     * 检测-活体-特征-人脸检索流程
     *
     * @param rgbData            可见光YUV 数据流
     * @param nirData            红外YUV 数据流
     * @param depthData          深度depth 数据流
     * @param srcHeight          可见光YUV 数据流-高度
     * @param srcWidth           可见光YUV 数据流-宽度
     * @param liveCheckMode      活体检测类型  【不使用活体：0】【RGB活体：1】；【RGB+NIR活体：2】；【RGB+Depth活体：3】；【RGB+NIR+Depth活体：4】
     * @param faceDetectCallBack
     */
    public void onDetectCheck(final byte[] rgbData,
                              final byte[] nirData,
                              final byte[] depthData,
                              final int srcHeight,
                              final int srcWidth,
                              final int liveCheckMode,
                              final FaceDetectCallBack faceDetectCallBack) {

        // 【【提取特征+1：N检索：3】
        onDetectCheck(rgbData, nirData, depthData, srcHeight, srcWidth, liveCheckMode, 3, faceDetectCallBack);
    }


    /**
     * 检测-活体-特征- 全流程
     *
     * @param rgbData            可见光YUV 数据流
     * @param nirData            红外YUV 数据流
     * @param depthData          深度depth 数据流
     * @param srcHeight          可见光YUV 数据流-高度
     * @param srcWidth           可见光YUV 数据流-宽度
     * @param liveCheckMode      活体检测模式【不使用活体：0】；【RGB活体：1】；【RGB+NIR活体：2】；【RGB+Depth活体：3】；RGB+NIR+Depth活体：4】
     * @param featureCheckMode   特征抽取模式【不提取特征：1】；【提取特征：2】；【提取特征+1：N检索：3】；
     * @param faceDetectCallBack
     */
    public void onDetectCheck(final byte[] rgbData,
                              final byte[] nirData,
                              final byte[] depthData,
                              final int srcHeight,
                              final int srcWidth,
                              final int liveCheckMode,
                              final int featureCheckMode,
                              final FaceDetectCallBack faceDetectCallBack) {

        if (future != null && !future.isDone()) {
            return;
        }

        future = es.submit(new Runnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();
                // 创建检测结果存储数据
                LivenessModel livenessModel = new LivenessModel();
                // 创建检测对象，如果原始数据YUV，转为算法检测的图片BGR
                // TODO: 用户调整旋转角度和是否镜像，手机和开发版需要动态适配
                BDFaceImageInstance rgbInstance;
                if (SingleBaseConfig.getBaseConfig().getType() == 4 || SingleBaseConfig.getBaseConfig().getType() == 3
                        && SingleBaseConfig.getBaseConfig().getCameraType() == 6) {
                    rgbInstance = new BDFaceImageInstance(rgbData, srcHeight,
                            srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_RGB,
                            SingleBaseConfig.getBaseConfig().getDetectDirection(),
                            SingleBaseConfig.getBaseConfig().getMirrorRGB());
                } else {
                    rgbInstance = new BDFaceImageInstance(rgbData, srcHeight,
                            srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                            SingleBaseConfig.getBaseConfig().getDetectDirection(),
                            SingleBaseConfig.getBaseConfig().getMirrorRGB());
                }

                // TODO: getImage() 获取送检图片,如果检测数据有问题，可以通过image view 展示送检图片
                livenessModel.setBdFaceImageInstance(rgbInstance.getImage());

                // 检查函数调用，返回检测结果
                long startDetectTime = System.currentTimeMillis();
                // 快速检测获取人脸信息，仅用于绘制人脸框，详细人脸数据后续获取
                FaceInfo[] faceInfos = FinanceFaceSDKManager.getInstance().getFaceDetect()
                        .track(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST, rgbInstance);
                livenessModel.setRgbDetectDuration(System.currentTimeMillis() - startDetectTime);
//                LogUtils.e(TIME_TAG, "detect vis time = " + livenessModel.getRgbDetectDuration());

                // 检测结果判断
                if (faceInfos != null && faceInfos.length > 0) {
                    livenessModel.setTrackFaceInfo(faceInfos);
                    livenessModel.setFaceInfo(faceInfos[0]);
                    livenessModel.setTrackStatus(1);
                    livenessModel.setLandmarks(faceInfos[0].landmarks);
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectDarwCallback(livenessModel);
                    }

                    onLivenessCheck(rgbInstance, nirData, depthData, srcHeight,
                            srcWidth, livenessModel.getLandmarks(),
                            livenessModel, startTime, liveCheckMode, featureCheckMode,
                            faceDetectCallBack, faceInfos);
                } else {
                    // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(null);
                        faceDetectCallBack.onFaceDetectDarwCallback(null);
                        faceDetectCallBack.onTip(0, "未检测到人脸");
                    }
                }
            }
        });
    }


    /**
     * 质量检测结果过滤，如果需要质量检测，
     * 需要调用 SingleBaseConfig.getBaseConfig().setQualityControl(true);设置为true，
     * 再调用  FaceSDKManager.getInstance().initConfig() 加载到底层配置项中
     *
     * @param livenessModel
     * @param faceDetectCallBack
     * @return
     */
    public boolean onQualityCheck(final LivenessModel livenessModel,
                                  final FaceDetectCallBack faceDetectCallBack) {

        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return true;
        }

        if (listOcclusion != null && listOcclusion.size() > 0) {
            listOcclusion.clear();
        }
        if (listDetected != null && listDetected.size() > 0) {
            listDetected.clear();
        }

        if (livenessModel != null && livenessModel.getFaceInfo() != null) {

            // 角度过滤
            if (Math.abs(livenessModel.getFaceInfo().yaw) > SingleBaseConfig.getBaseConfig().getYaw() ||
                    Math.abs(livenessModel.getFaceInfo().roll) > SingleBaseConfig.getBaseConfig().getRoll() ||
                    Math.abs(livenessModel.getFaceInfo().pitch) > SingleBaseConfig.getBaseConfig().getPitch()) {
                listDetected.add("姿态不满足");
            }
            // 模糊结果过滤
            float blur = livenessModel.getFaceInfo().bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                listDetected.add("模糊");
            }

            // 光照结果过滤
            float illum = livenessModel.getFaceInfo().illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                listDetected.add("光线不满足");
            }


            // 遮挡结果过滤
            if (livenessModel.getFaceInfo().occlusion != null) {
                BDFaceOcclusion occlusion = livenessModel.getFaceInfo().occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye() ||
                        occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // 左右眼遮挡置信度
                    listOcclusion.add("左右眼");
                }
                if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // 鼻子遮挡置信度
                    listOcclusion.add("鼻子");
                }
                if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // 嘴巴遮挡置信度
                    listOcclusion.add("嘴巴");
                }
                if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek() ||
                        occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // 左右脸遮挡置信度
                    listOcclusion.add("左右脸");
                }
                if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // 下巴遮挡置信度
                    listOcclusion.add("下巴");
                }
            }
            livenessModel.setListDetected(listDetected);
            livenessModel.setListOcclusion(listOcclusion);

            if (listOcclusion.size() > 0 || listDetected.size() > 0) {
                return false;
            } else {
                return true;
            }

        }
        return false;
    }

//    /**
//     * 最优人脸控制
//     *
//     * @param livenessModel
//     * @param faceDetectCallBack
//     * @return
//     */
//    public boolean onBestImageCheck(LivenessModel livenessModel,
//                                    FaceDetectCallBack faceDetectCallBack) {
//        if (!SingleBaseConfig.getBaseConfig().isUsingBestImage()) {
//            return true;
//        }
//
//        if (livenessModel != null && livenessModel.getFaceInfo() != null) {
//            float bestImageScore = livenessModel.getFaceInfo().bestImageScore;
//            if (bestImageScore < SingleBaseConfig.getBaseConfig().getBestImageScore()) {
//                faceDetectCallBack.onTip(-1, "最优人脸不通过");
//                return false;
//            }
//        }
//        return true;
//    }


    /**
     * 活体-特征-人脸检索全流程
     *
     * @param rgbInstance        可见光底层送检对象
     * @param nirData            红外YUV 数据流
     * @param depthData          深度depth 数据流
     * @param srcHeight          可见光YUV 数据流-高度
     * @param srcWidth           可见光YUV 数据流-宽度
     * @param landmark           检测眼睛，嘴巴，鼻子，72个关键点
     * @param livenessModel      检测结果数据集合
     * @param startTime          开始检测时间
     * @param liveCheckMode      活体检测模式【不使用活体：1】；【RGB活体：2】；【RGB+NIR活体：3】；【RGB+Depth活体：4】
     * @param featureCheckMode   特征抽取模式【不提取特征：1】；【提取特征：2】；【提取特征+1：N检索：3】；
     * @param faceDetectCallBack
     */
    public void onLivenessCheck(final BDFaceImageInstance rgbInstance,
                                final byte[] nirData,
                                final byte[] depthData,
                                final int srcHeight,
                                final int srcWidth,
                                final float[] landmark,
                                final LivenessModel livenessModel,
                                final long startTime,
                                final int liveCheckMode,
                                final int featureCheckMode,
                                final FaceDetectCallBack faceDetectCallBack,
                                final FaceInfo[] fastFaceInfos) {

        if (future2 != null && !future2.isDone()) {
            // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
            rgbInstance.destory();
            return;
        }

        future2 = es2.submit(new Runnable() {
            @Override
            public void run() {
                BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
                bdFaceDetectListConfig.usingQuality = bdFaceDetectListConfig.usingHeadPose
                        = SingleBaseConfig.getBaseConfig().isQualityControl();
                bdFaceDetectListConfig.usingAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
                bdFaceDetectListConfig.usingBestImage = SingleBaseConfig.getBaseConfig().isUsingBestImage();

                FaceInfo[] faceInfos = FinanceFaceSDKManager.getInstance()
                        .getFaceDetect()
                        .detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                                rgbInstance,
                                fastFaceInfos, bdFaceDetectListConfig);
                livenessModel.setQualityDetectDuration(System.currentTimeMillis() - startTime);
                // 重新赋予详细人脸信息
                if (faceInfos != null && faceInfos.length > 0) {
                    livenessModel.setFaceInfo(faceInfos[0]);
                    livenessModel.setTrackStatus(2);
                    livenessModel.setLandmarks(faceInfos[0].landmarks);
                } else {
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
                    }
                    return;
                }
//                // 最优人脸控制
//                if (!onBestImageCheck(livenessModel, faceDetectCallBack)) {
//                    rgbInstance.destory();
//                    if (faceDetectCallBack != null) {
//                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
//                    }
//                    return;
//                }

                // 质量检测未通过,销毁BDFaceImageInstance，结束函数
                if (!onQualityCheck(livenessModel, faceDetectCallBack)) {
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
                        livenessModel.setAllDetectDuration(System.currentTimeMillis() - startTime);
                    }
                    return;
                }


                // 获取LivenessConfig liveCheckMode 配置选项：【不使用活体：0】；【RGB活体：1】；【RGB+NIR活体：2】；【RGB+Depth活体：3】；【RGB+NIR+Depth活体：4】
                // TODO 活体检测
                if (liveCheckMode != 0) {
                    long startRgbTime = System.currentTimeMillis();
                    boolean rgbLiveStatus = FinanceFaceSDKManager.getInstance().getFaceLiveness().strategySilentLive(
                            BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                            rgbInstance, fastFaceInfos[0], SingleBaseConfig.getBaseConfig().getFramesThreshold(),
                            SingleBaseConfig.getBaseConfig().getRgbLiveScore());
                    livenessModel.setRGBLiveStatus(rgbLiveStatus);
                    livenessModel.setRgbLivenessDuration(System.currentTimeMillis() - startRgbTime);
                }

                if (liveCheckMode == 2 || liveCheckMode == 4 && nirData != null) {
                    // 创建检测对象，如果原始数据YUV-IR，转为算法检测的图片BGR
                    // TODO: 用户调整旋转角度和是否镜像，手机和开发版需要动态适配
                    BDFaceImageInstance nirInstance = new BDFaceImageInstance(nirData, srcHeight,
                            srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                            SingleBaseConfig.getBaseConfig().getDetectDirection(),
                            SingleBaseConfig.getBaseConfig().getMirrorNIR());

                    // 避免RGB检测关键点在IR对齐活体稳定，增加红外检测
                    long startIrDetectTime = System.currentTimeMillis();
                    BDFaceDetectListConf bdFaceDetectListConf = new BDFaceDetectListConf();
                    bdFaceDetectListConf.usingDetect = true;
                    FaceInfo[] faceInfosIr = faceDetectNir.detect(BDFaceSDKCommon.DetectType.DETECT_NIR,
                            BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                            nirInstance, null, bdFaceDetectListConf);
                    bdFaceDetectListConf.usingDetect = false;
                    livenessModel.setIrLivenessDuration(System.currentTimeMillis() - startIrDetectTime);
//                    LogUtils.e(TIME_TAG, "detect ir time = " + livenessModel.getIrLivenessDuration());

                    if (faceInfosIr != null && faceInfosIr.length > 0) {
                        FaceInfo faceInfoIr = faceInfosIr[0];
                        long startNirTime = System.currentTimeMillis();
                        boolean nirLiveStatus = FinanceFaceSDKManager.getInstance().getFaceLiveness()
                                .strategySilentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_NIR,
                                        nirInstance, fastFaceInfos[0], SingleBaseConfig.getBaseConfig().getFramesThreshold(),
                                        SingleBaseConfig.getBaseConfig().getNirLiveScore());
                        livenessModel.setNIRLiveStatus(nirLiveStatus);
                        livenessModel.setIrLivenessDuration(System.currentTimeMillis() - startNirTime);
//                        LogUtils.e(TIME_TAG, "live ir time = " + livenessModel.getIrLivenessDuration());
                    }

                    nirInstance.destory();
                }

                if (liveCheckMode == 3 || liveCheckMode == 4 && depthData != null) {
                    fastFaceInfos[0].landmarks = faceInfos[0].landmarks;
                    // TODO: 用户调整旋转角度和是否镜像，适配Atlas 镜头，目前宽和高400*640，其他摄像头需要动态调整,人脸72 个关键点x 坐标向左移动80个像素点
                    float[] depthLandmark = new float[faceInfos[0].landmarks.length];
                    BDFaceImageInstance depthInstance;
                    if (SingleBaseConfig.getBaseConfig().getCameraType() == 1) {
                        System.arraycopy(faceInfos[0].landmarks, 0, depthLandmark, 0, faceInfos[0].landmarks.length);
                        if (SingleBaseConfig.getBaseConfig().getCameraType() == 1) {
                            for (int i = 0; i < 144; i = i + 2) {
                                depthLandmark[i] -= 80;
                            }
                        }
                        depthInstance = new BDFaceImageInstance(depthData,
                                SingleBaseConfig.getBaseConfig().getDepthWidth(),
                                SingleBaseConfig.getBaseConfig().getDepthHeight(),
                                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_DEPTH,
                                0, 0);
                    } else {
                        depthInstance = new BDFaceImageInstance(depthData,
                                SingleBaseConfig.getBaseConfig().getDepthHeight(),
                                SingleBaseConfig.getBaseConfig().getDepthWidth(),
                                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_DEPTH,
                                0, 0);
                    }

                    // 创建检测对象，如果原始数据Depth
                    long startDepthTime = System.currentTimeMillis();
                    if (SingleBaseConfig.getBaseConfig().getCameraType() == 1) {
                        fastFaceInfos[0].landmarks = depthLandmark;
                        boolean depthLiveStatus = FinanceFaceSDKManager.getInstance().getFaceLiveness()
                                .strategySilentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_DEPTH,
                                        depthInstance, fastFaceInfos[0], SingleBaseConfig.getBaseConfig().getFramesThreshold(),
                                        SingleBaseConfig.getBaseConfig().getDepthLiveScore());
                        livenessModel.setDepthLiveStatus(depthLiveStatus);
                    } else {
                        boolean depthLiveStatus = FinanceFaceSDKManager.getInstance().getFaceLiveness()
                                .strategySilentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_DEPTH,
                                        depthInstance, fastFaceInfos[0], SingleBaseConfig.getBaseConfig().getFramesThreshold(),
                                        SingleBaseConfig.getBaseConfig().getDepthLiveScore());
                        livenessModel.setDepthLiveStatus(depthLiveStatus);
                    }
                    livenessModel.setDepthtLivenessDuration(System.currentTimeMillis() - startDepthTime);
                    depthInstance.destory();
                }

                // 流程结束,记录最终时间
                livenessModel.setAllDetectDuration(System.currentTimeMillis() - startTime);
//                LogUtils.e(TIME_TAG, "all process time = " + livenessModel.getAllDetectDuration());
                // 流程结束销毁图片，开始下一帧图片检测，否着内存泄露
                rgbInstance.destory();
                // 显示最终结果提示
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(livenessModel);
                }
            }
        });
    }


    /**
     * 卸载模型
     */
//    public void uninitModel() {
//        if (faceDetect != null) {
//            faceDetect.uninitModel();
//        }
//        if (faceFeature != null) {
//            faceFeature.uninitModel();
//        }
//        if (faceDetectNir != null) {
//            faceDetectNir.uninitModel();
//        }
//        if (faceLiveness != null) {
//            faceLiveness.uninitModel();
//        }
//
//        if (faceDetect.uninitModel() == 0
//                && faceFeature.uninitModel() == 0
//                && faceDetectNir.uninitModel() == 0
//                && faceLiveness.uninitModel() == 0) {
//            initStatus = SDK_UNACTIVATION;
//            initModelSuccess = false;
//        }
//
//        Log.e("uninitModel","finance-uninitModel"
//                + faceDetect.uninitModel()
//                + faceFeature.uninitModel()
//                + faceDetectNir.uninitModel()
//                + faceLiveness.uninitModel());
//    }

}