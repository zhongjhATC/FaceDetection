package com.baidu.idl.main.facesdk.registerlibrary.user.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import com.baidu.idl.main.facesdk.FaceAuth;
import com.baidu.idl.main.facesdk.FaceCrop;
import com.baidu.idl.main.facesdk.FaceDetect;
import com.baidu.idl.main.facesdk.FaceDriverMonitor;
import com.baidu.idl.main.facesdk.FaceFeature;
import com.baidu.idl.main.facesdk.FaceGaze;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.FaceLive;
import com.baidu.idl.main.facesdk.FaceMouthMask;
import com.baidu.idl.main.facesdk.ImageIllum;
import com.baidu.idl.main.facesdk.callback.Callback;
import com.baidu.idl.main.facesdk.license.BDFaceLicenseAuthInfo;
import com.baidu.idl.main.facesdk.model.BDFaceCropParam;
import com.baidu.idl.main.facesdk.model.BDFaceDetectListConf;
import com.baidu.idl.main.facesdk.model.BDFaceDriverMonitorInfo;
import com.baidu.idl.main.facesdk.model.BDFaceGazeInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.model.BDFaceSDKConfig;
import com.baidu.idl.main.facesdk.model.Feature;
import com.baidu.idl.main.facesdk.registerlibrary.user.api.FaceApi;
import com.baidu.idl.main.facesdk.registerlibrary.user.callback.FaceDetectCallBack;
import com.baidu.idl.main.facesdk.registerlibrary.user.callback.FaceFeatureCallBack;
import com.baidu.idl.main.facesdk.registerlibrary.user.db.DBManager;
import com.baidu.idl.main.facesdk.registerlibrary.user.listener.SdkInitListener;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.DriverInfo;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.GlobalSet;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.LivenessModel;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.SingleBaseConfig;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.User;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.ToastUtils;
import com.baidu.idl.main.facesdk.utils.PreferencesUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import static com.baidu.idl.main.facesdk.registerlibrary.user.model.GlobalSet.FEATURE_SIZE;


public class FaceSDKManager {

    public static final int SDK_MODEL_LOAD_SUCCESS = 0;
    public static final int SDK_UNACTIVATION = 1;
    public static final int SDK_UNINIT = 2;
    public static final int SDK_INITING = 3;
    public static final int SDK_INITED = 4;
    public static final int SDK_INIT_FAIL = 5;
    public static final int SDK_INIT_SUCCESS = 6;

    private int threholdScore;

    private DriverInfo driverInfo;
    private BDFaceDriverMonitorInfo bdFaceDriverMonitorInfo;

    public static volatile int initStatus = SDK_UNACTIVATION;
    public static volatile boolean initModelSuccess = false;
    private FaceAuth faceAuth;
    private FaceDetect faceDetect;
    private FaceFeature faceFeature;
    private FaceLive faceLiveness;
    private FaceCrop faceCrop;

    private FaceGaze faceGaze;
    private FaceDriverMonitor faceDriverMonitor;

    private ExecutorService es = Executors.newSingleThreadExecutor();
    private Future future;
    private ExecutorService es2 = Executors.newSingleThreadExecutor();
    private Future future2;

    private ExecutorService mRegExecutorService = Executors.newSingleThreadExecutor();
    private Future mRegFuture;

    private FaceDetect faceDetectNir;
    private FaceMouthMask faceMouthMask;
    private float[] scores;
    public static boolean isDetectMask = false;
    private boolean mIsCropFace;
    private FaceDetect faceDetectPerson;
    private FaceFeature faceFeaturePerson;
    private ImageIllum imageIllum;

    private FaceSDKManager() {
        faceAuth = new FaceAuth();
        faceAuth.setActiveLog(BDFaceSDKCommon.BDFaceLogInfo.BDFACE_LOG_TYPE_ALL, 1);
        faceAuth.setCoreConfigure(BDFaceSDKCommon.BDFaceCoreRunMode.BDFACE_LITE_POWER_LOW, 2);

//        faceDetect = new FaceDetect();
//        faceFeature = new FaceFeature();
//        faceLiveness = new FaceLive();

    }

    private static class HolderClass {
        private static final FaceSDKManager instance = new FaceSDKManager();
    }

    public static FaceSDKManager getInstance() {
        return HolderClass.instance;
    }

    public FaceDetect getFaceDetect() {
        return faceDetect;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public FaceLive getFaceLiveness() {
        return faceLiveness;
    }

    public FaceCrop getFaceCrop() {
        return faceCrop;
    }

    public void setCropFace(boolean isCropFace) {
        mIsCropFace = isCropFace;
    }

    public boolean getCropFace() {
        return mIsCropFace;
    }

    public ImageIllum getImageIllum() {
        return imageIllum;
    }

    /**
     * ????????????????????????????????????????????????????????????
     *
     * @param context
     * @param listener
     */
    public void init(final Context context, final SdkInitListener listener) {

        PreferencesUtil.initPrefs(context.getApplicationContext());
        final String licenseOfflineKey = PreferencesUtil.getString("activate_offline_key", "");
        final String licenseOnlineKey = PreferencesUtil.getString("activate_online_key", "");
        final String licenseBatchlineKey = PreferencesUtil.getString("activate_batchline_key", "");

        // ??????licenseKey ????????????????????????????????????????????????????????????
        if (TextUtils.isEmpty(licenseOfflineKey) && TextUtils.isEmpty(licenseOnlineKey)
                && TextUtils.isEmpty(licenseBatchlineKey)) {
            ToastUtils.toast(context, "???????????????????????????????????????");
            if (listener != null) {
                listener.initLicenseFail(-1, "???????????????????????????????????????");
            }
            return;
        }
        // todo ??????????????????
        if (listener != null) {
            listener.initStart();
        }

        if (!TextUtils.isEmpty(licenseOnlineKey)) {
            // ????????????
            faceAuth.initLicenseOnLine(context, licenseOnlineKey, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        initStatus = SDK_INIT_SUCCESS;
                        if (listener != null) {
                            listener.initLicenseSuccess();
                        }
//                        initModel(context, listener);
                        return;
                    } else {
                        listener.initLicenseFail(code, response);
                    }
                }
            });
        } else if (!TextUtils.isEmpty(licenseOfflineKey)) {
            // ????????????
            faceAuth.initLicenseOffLine(context, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        initStatus = SDK_INIT_SUCCESS;
                        if (listener != null) {
                            listener.initLicenseSuccess();
                        }
//                        initModel(context, listener);
                        return;
                    } else {
                        listener.initLicenseFail(code, response);
                    }
                }
            });
        } else if (!TextUtils.isEmpty(licenseBatchlineKey)) {
            // ????????????
            faceAuth.initLicenseBatchLine(context, licenseBatchlineKey, new Callback() {
                @Override
                public void onResponse(int code, String response) {
                    if (code == 0) {
                        initStatus = SDK_INIT_SUCCESS;
                        if (listener != null) {
                            listener.initLicenseSuccess();
                        }
//                        initModel(context, listener);
                        return;
                    } else {
                        listener.initLicenseFail(code, response);
                    }
                }
            });
        } else {
            if (listener != null) {
                listener.initLicenseFail(-1, "???????????????????????????????????????");
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param context
     * @param listener
     */
    public void initModel(final Context context, final SdkInitListener listener) {
//      ToastUtils.toast(context, "????????????????????????????????????");

        // ????????????
        BDFaceInstance bdFaceInstance = new BDFaceInstance();
        bdFaceInstance.creatInstance();
        faceDetect = new FaceDetect(bdFaceInstance);
        // ????????????
        BDFaceInstance IrBdFaceInstance = new BDFaceInstance();
        IrBdFaceInstance.creatInstance();
        faceDetectNir = new FaceDetect(IrBdFaceInstance);
        // ????????????
        BDFaceInstance faceInstancePerson = new BDFaceInstance();
        faceInstancePerson.creatInstance();
        faceDetectPerson = new FaceDetect(faceInstancePerson);

        // ????????????
        faceFeature = new FaceFeature();
        // ????????????
        faceFeaturePerson = new FaceFeature(faceInstancePerson);

        faceLiveness = new FaceLive();
        faceGaze = new FaceGaze();
        faceDriverMonitor = new FaceDriverMonitor();
        faceCrop = new FaceCrop();
        faceMouthMask = new FaceMouthMask();

        // ??????
        imageIllum = new ImageIllum();

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
        faceDetectPerson.initModel(context,
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

//        faceDetect.initModel(context,
//                GlobalSet.DETECT_VIS_MODEL,
//                "",
//                GlobalSet.ALIGN_MODEL, BDFaceSDKCommon.DetectType.DETECT_VIS,
//                BDFaceSDKCommon.AlignType.BDFACE_FULL_ALIGN_TYPE_GENERAL_RGB,
//                new Callback() {
//                    @Override
//                    public void onResponse(int code, String response) {
//                        //  ToastUtils.toast(context, code + "  " + response);
//                        if (code != 0 && listener != null) {
//                            listener.initModelFail(code, response);
//                        }
//                    }
//                });

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

        faceDetectPerson.initQuality(context,
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
                        //  ToastUtils.toast(context, code + "  " + response);
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });

        faceGaze.initModel(context, GlobalSet.GAZE_MODEL, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });

        faceMouthMask.initModel(context, GlobalSet.MOUTH_MASK, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });

        faceDriverMonitor.initDriverMonitor(context, GlobalSet.DRIVEMONITOR_MODEL, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });

        faceCrop.initFaceCrop(new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });
        faceDetect.initBestImage(context, GlobalSet.BEST_IMAGE, new Callback() {
            @Override
            public void onResponse(int code, String response) {
                if (code != 0 && listener != null) {
                    listener.initModelFail(code, response);
                }
            }
        });

        faceFeaturePerson.initModel(context,
                GlobalSet.RECOGNIZE_IDPHOTO_MODEL,
                "",
                "",
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        if (code != 0 && listener != null) {
                            listener.initModelFail(code, response);
                        }
                    }
                });


        faceFeature.initModel(context,
                GlobalSet.RECOGNIZE_IDPHOTO_MODEL,
                GlobalSet.RECOGNIZE_VIS_MODEL,
                "",
                new Callback() {
                    @Override
                    public void onResponse(int code, String response) {
                        long endInitModelTime = System.currentTimeMillis();
//                        LogUtils.e(TIME_TAG, "init model time = " + (endInitModelTime - startInitModelTime));
                        if (code != 0) {
//                            ToastUtils.toast(context, "??????????????????,??????????????????");
                            if (listener != null) {
                                listener.initModelFail(code, response);
                            }
                        } else {
                            initStatus = SDK_MODEL_LOAD_SUCCESS;
                            // ??????????????????????????????????????????
                            initDataBases(context);
//                            ToastUtils.toast(context, "?????????????????????????????????");
                            if (listener != null) {
                                listener.initModelSuccess();
                            }
                        }
                    }
                });
    }


    /**
     * ???????????????
     *
     * @return
     */
    public boolean initConfig() {
        if (faceDetect != null && faceDetectPerson != null) {
            BDFaceSDKConfig config = new BDFaceSDKConfig();
            // TODO: ??????????????????????????????????????????1,??????????????????????????????
            config.maxDetectNum = 1;

            // TODO: ?????????80px??????????????????30px????????????????????????????????????????????????????????????????????????????????????
            config.minFaceSize = SingleBaseConfig.getBaseConfig().getMinimumFace();

            // ???????????????????????????????????????
            config.isAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
//
//            // TODO: ?????????????????????????????????????????????????????????????????????????????????????????????????????????
            config.isCheckBlur = config.isOcclusion
                    = config.isIllumination = config.isHeadPose
                    = SingleBaseConfig.getBaseConfig().isQualityControl();

            faceDetect.loadConfig(config);
            faceDetectPerson.loadConfig(config);

            return true;
        }
        return false;
    }

    public void initDataBases(Context context) {
        // ??????????????????
        DBManager.getInstance().init(context);
        // ???????????????????????????
        FaceApi.getInstance().initDatabases(true);
    }


    /**
     * ??????-??????-??????-??????????????????
     *
     * @param rgbData            ?????????YUV ?????????
     * @param nirData            ??????YUV ?????????
     * @param depthData          ??????depth ?????????
     * @param srcHeight          ?????????YUV ?????????-??????
     * @param srcWidth           ?????????YUV ?????????-??????
     * @param liveCheckMode      ???????????????????????????????????????0?????????RGB?????????1?????????RGB+NIR?????????2?????????RGB+Depth?????????3?????????RGB+NIR+Depth?????????4???
     * @param faceDetectCallBack
     */
    public void onDetectCheck(final byte[] rgbData,
                              final byte[] nirData,
                              final byte[] depthData,
                              final int srcHeight,
                              final int srcWidth,
                              final int liveCheckMode,
                              final FaceDetectCallBack faceDetectCallBack) {

        // ??????????????????+1???N?????????3???
        onDetectCheck(rgbData, nirData, depthData, srcHeight, srcWidth, liveCheckMode, 3, faceDetectCallBack);
    }


    /**
     * ??????-??????-??????- ?????????
     *
     * @param rgbData            ?????????YUV ?????????
     * @param nirData            ??????YUV ?????????
     * @param depthData          ??????depth ?????????
     * @param srcHeight          ?????????YUV ?????????-??????
     * @param srcWidth           ?????????YUV ?????????-??????
     * @param liveCheckMode      ???????????????????????????????????????0?????????RGB?????????1?????????RGB+NIR?????????2?????????RGB+Depth?????????3?????????RGB+NIR+Depth?????????4???
     * @param featureCheckMode   ???????????????????????????????????????1????????????????????????2?????????????????????+1???N?????????3??????
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
                // ??????????????????????????????
                LivenessModel livenessModel = new LivenessModel();
                // ???????????????????????????????????????YUV??????????????????????????????BGR
                // TODO: ??????????????????????????????????????????????????????????????????????????????
                BDFaceImageInstance rgbInstance;
                if (SingleBaseConfig.getBaseConfig().getType() == 4
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

                // TODO: getImage() ??????????????????,??????????????????????????????????????????image view ??????????????????
                livenessModel.setBdFaceImageInstance(rgbInstance.getImage());

                // ???????????????????????????????????????
                long startDetectTime = System.currentTimeMillis();

                // ??????????????????????????????????????????????????????????????????????????????????????????
                FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                        .track(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_FAST, rgbInstance);
                livenessModel.setRgbDetectDuration(System.currentTimeMillis() - startDetectTime);
//                LogUtils.e(TIME_TAG, "detect vis time = " + livenessModel.getRgbDetectDuration());

                // ??????????????????
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
                    // ???????????????????????????????????????????????????????????????????????????
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(null);
                        faceDetectCallBack.onFaceDetectDarwCallback(null);
                        faceDetectCallBack.onTip(0, "??????????????????");
                    }
                }
            }
        });
    }


    /**
     * ??????????????????????????????????????????????????????
     * ???????????? SingleBaseConfig.getBaseConfig().setQualityControl(true);?????????true???
     * ?????????  FaceSDKManager.getInstance().initConfig() ???????????????????????????
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

        if (livenessModel != null && livenessModel.getFaceInfo() != null) {

            // ????????????
            if (Math.abs(livenessModel.getFaceInfo().yaw) > SingleBaseConfig.getBaseConfig().getYaw()) {
                faceDetectCallBack.onTip(-1, "?????????????????????????????????");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().roll) > SingleBaseConfig.getBaseConfig().getRoll()) {
                faceDetectCallBack.onTip(-1, "???????????????????????????????????????????????????");
                return false;
            } else if (Math.abs(livenessModel.getFaceInfo().pitch) > SingleBaseConfig.getBaseConfig().getPitch()) {
                faceDetectCallBack.onTip(-1, "?????????????????????????????????");
                return false;
            }

            // ??????????????????
            float blur = livenessModel.getFaceInfo().bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                faceDetectCallBack.onTip(-1, "????????????");
                return false;
            }

            // ??????????????????
            float illum = livenessModel.getFaceInfo().illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                faceDetectCallBack.onTip(-1, "?????????????????????");
                return false;
            }


            // ??????????????????
            if (livenessModel.getFaceInfo().occlusion != null) {
                BDFaceOcclusion occlusion = livenessModel.getFaceInfo().occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // ?????????????????????
                    faceDetectCallBack.onTip(-1, "????????????");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ??????????????????
     *
     * @param livenessModel
     * @param faceDetectCallBack
     * @return
     */
    public boolean onBestImageCheck(LivenessModel livenessModel,
                                    FaceDetectCallBack faceDetectCallBack) {
        if (!SingleBaseConfig.getBaseConfig().isUsingBestImage()) {
            return true;
        }

        if (livenessModel != null && livenessModel.getFaceInfo() != null) {
            float bestImageScore = livenessModel.getFaceInfo().bestImageScore;
            if (bestImageScore < SingleBaseConfig.getBaseConfig().getBestImageScore()) {
                faceDetectCallBack.onTip(-1, "?????????????????????");
                return false;
            }
        }
        return true;
    }


    /**
     * ??????-??????-?????????????????????
     *
     * @param rgbInstance        ???????????????????????????
     * @param nirData            ??????YUV ?????????
     * @param depthData          ??????depth ?????????
     * @param srcHeight          ?????????YUV ?????????-??????
     * @param srcWidth           ?????????YUV ?????????-??????
     * @param landmark           ?????????????????????????????????72????????????
     * @param livenessModel      ????????????????????????
     * @param startTime          ??????????????????
     * @param liveCheckMode      ???????????????????????????????????????0?????????RGB?????????1?????????RGB+NIR?????????2?????????RGB+Depth?????????3?????????RGB+NIR+Depth?????????4???
     * @param featureCheckMode   ???????????????????????????????????????1????????????????????????2?????????????????????+1???N?????????3??????
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
            // ???????????????????????????????????????????????????????????????????????????
            rgbInstance.destory();
            return;
        }

        future2 = es2.submit(new Runnable() {

            private FaceInfo[] faceInfos;

            @Override
            public void run() {
                BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
                bdFaceDetectListConfig.usingQuality = bdFaceDetectListConfig.usingHeadPose
                        = SingleBaseConfig.getBaseConfig().isQualityControl();
                bdFaceDetectListConfig.usingAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
                bdFaceDetectListConfig.usingBestImage = SingleBaseConfig.getBaseConfig().isUsingBestImage();

                if (SingleBaseConfig.getBaseConfig().getType() == 2 &&
                        SingleBaseConfig.getBaseConfig().getActiveModel() == 3) {
                    AtomicInteger atomicInteger = new AtomicInteger();
                    int status = FaceSDKManager.getInstance().getImageIllum().imageIllum(rgbInstance, atomicInteger);
                    int illumScore = atomicInteger.get();

                    if (illumScore > SingleBaseConfig.getBaseConfig().getCamera_lightThreshold()) {
                        faceInfos = FaceSDKManager.getInstance()
                                .getFaceDetect()
                                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                                        rgbInstance,
                                        fastFaceInfos, bdFaceDetectListConfig);
                    } else {
                        faceInfos = FaceSDKManager.getInstance()
                                .getFaceDetect()
                                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                        BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                                        rgbInstance,
                                        fastFaceInfos, bdFaceDetectListConfig);
                    }
                }
                faceInfos = FaceSDKManager.getInstance()
                        .getFaceDetect()
                        .detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                                rgbInstance,
                                fastFaceInfos, bdFaceDetectListConfig);

                // ??????????????????????????????
                if (faceInfos != null && faceInfos.length > 0) {
                    livenessModel.setFaceInfo(faceInfos[0]);
                    livenessModel.setTrackStatus(2);
                    livenessModel.setLandmarks(faceInfos[0].landmarks);

                    if (isDetectMask) {
                        scores = faceMouthMask.checkMask(rgbInstance, faceInfos);
                        if (scores != null) {
                            livenessModel.setMaskScore(scores[0]);
                            Log.e("FaceMouthMask", scores[0] + "");
                        }
                    }
                } else {
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
                    }
                    return;
                }
                // ??????????????????
                if (!onBestImageCheck(livenessModel, faceDetectCallBack)) {
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
                    }
                    return;
                }

                // ?????????????????????,??????BDFaceImageInstance???????????????
                if (!onQualityCheck(livenessModel, faceDetectCallBack)) {
                    rgbInstance.destory();
                    if (faceDetectCallBack != null) {
                        faceDetectCallBack.onFaceDetectCallback(livenessModel);
                    }
                    return;
                }

                // ??????LivenessConfig liveCheckMode ????????????????????????????????????0?????????RGB?????????1?????????RGB+NIR?????????2?????????RGB+Depth?????????3?????????RGB+NIR+Depth?????????4???
                // TODO ????????????
                float rgbScore = -1;
                if (liveCheckMode != 0) {
                    long startRgbTime = System.currentTimeMillis();
                    rgbScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(
                            BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB,
                            rgbInstance, faceInfos[0].landmarks);
                    livenessModel.setRgbLivenessScore(rgbScore);
                    livenessModel.setRgbLivenessDuration(System.currentTimeMillis() - startRgbTime);
//                    LogUtils.e(TIME_TAG, "live rgb time = " + livenessModel.getRgbLivenessDuration());
                }

                float nirScore = -1;
                if (liveCheckMode == 2 || liveCheckMode == 4 && nirData != null) {
                    // ???????????????????????????????????????YUV-IR??????????????????????????????BGR
                    // TODO: ??????????????????????????????????????????????????????????????????????????????
                    BDFaceImageInstance nirInstance = new BDFaceImageInstance(nirData, srcHeight,
                            srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                            SingleBaseConfig.getBaseConfig().getDetectDirection(),
                            SingleBaseConfig.getBaseConfig().getMirrorNIR());

                    // ??????RGB??????????????????IR???????????????????????????????????????
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
                        nirScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(
                                BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_NIR,
                                nirInstance, faceInfoIr.landmarks);
                        livenessModel.setIrLivenessScore(nirScore);
                        livenessModel.setIrLivenessDuration(System.currentTimeMillis() - startNirTime);
//                        LogUtils.e(TIME_TAG, "live ir time = " + livenessModel.getIrLivenessDuration());
                    }

                    nirInstance.destory();
                }

                float depthScore = -1;
                if (liveCheckMode == 3 || liveCheckMode == 4 && depthData != null) {
                    // TODO: ????????????????????????????????????????????????Atlas ????????????????????????400*640????????????????????????????????????,??????72 ????????????x ??????????????????80????????????
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

                    // ???????????????????????????????????????Depth
                    long startDepthTime = System.currentTimeMillis();
                    if (SingleBaseConfig.getBaseConfig().getCameraType() == 2) {
                        depthScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(
                                BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_DEPTH,
                                depthInstance, depthLandmark);
                    } else {
                        depthScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(
                                BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_DEPTH,
                                depthInstance, faceInfos[0].landmarks);
                    }
                    livenessModel.setDepthLivenessScore(depthScore);
                    livenessModel.setDepthtLivenessDuration(System.currentTimeMillis() - startDepthTime);
//                    LogUtils.e(TIME_TAG, "live depth time = " + livenessModel.getDepthtLivenessDuration());
                    depthInstance.destory();
                }

                // TODO ????????????+????????????
                if (liveCheckMode == 0) {
                    onFeatureCheck(rgbInstance, faceInfos[0].landmarks, null, srcHeight,
                            srcWidth, livenessModel, featureCheckMode,
                            SingleBaseConfig.getBaseConfig().getActiveModel());
                } else {
                    if (liveCheckMode == 1 && rgbScore > SingleBaseConfig.getBaseConfig().getRgbLiveScore()) {
                        onFeatureCheck(rgbInstance, faceInfos[0].landmarks, null, srcHeight,
                                srcWidth, livenessModel, featureCheckMode,
                                SingleBaseConfig.getBaseConfig().getActiveModel());
                    } else if (liveCheckMode == 2 && rgbScore > SingleBaseConfig.getBaseConfig().getRgbLiveScore()
                            && nirScore > SingleBaseConfig.getBaseConfig().getNirLiveScore()) {
                        onFeatureCheck(rgbInstance, faceInfos[0].landmarks, nirData, srcHeight,
                                srcWidth, livenessModel, featureCheckMode,
                                SingleBaseConfig.getBaseConfig().getActiveModel());
                    } else if (liveCheckMode == 3 && rgbScore > SingleBaseConfig.getBaseConfig().getRgbLiveScore()
                            && depthScore > SingleBaseConfig.getBaseConfig().getDepthLiveScore()) {
                        onFeatureCheck(rgbInstance, faceInfos[0].landmarks, null, srcHeight,
                                srcWidth, livenessModel, featureCheckMode,
                                SingleBaseConfig.getBaseConfig().getActiveModel());
                    } else if (liveCheckMode == 4 && rgbScore > SingleBaseConfig.getBaseConfig().getRgbLiveScore()
                            && nirScore > SingleBaseConfig.getBaseConfig().getNirLiveScore()
                            && depthScore > SingleBaseConfig.getBaseConfig().getDepthLiveScore()) {
                        onFeatureCheck(rgbInstance, faceInfos[0].landmarks, nirData, srcHeight,
                                srcWidth, livenessModel, featureCheckMode,
                                SingleBaseConfig.getBaseConfig().getActiveModel());
                    }
                }

                if (getCropFace()) {
                    BDFaceCropParam cropParam = new BDFaceCropParam();
                    cropParam.foreheadExtend = 2.0f / 9;
                    cropParam.chinExtend = 1.0f / 9;
                    cropParam.enlargeRatio = 1.5f;
                    cropParam.height = 640;
                    cropParam.width = 480;
                    BDFaceImageInstance cropInstance = FaceSDKManager.getInstance().getFaceCrop()
                            .cropFaceByLandmarkParam(rgbInstance, faceInfos[0].landmarks, cropParam);
                    if (cropInstance == null) {
                        rgbInstance.destory();
                        if (faceDetectCallBack != null) {
                            faceDetectCallBack.onTip(-1, "????????????");
                        }
                        return;
                    }
                    livenessModel.setBdFaceImageInstanceCrop(cropInstance);
                }

                // ????????????,??????????????????
                livenessModel.setAllDetectDuration(System.currentTimeMillis() - startTime);
//                LogUtils.e(TIME_TAG, "all process time = " + livenessModel.getAllDetectDuration());
                // ???????????????????????????????????????????????????????????????????????????
                rgbInstance.destory();
                // ????????????????????????
                if (faceDetectCallBack != null) {
                    faceDetectCallBack.onFaceDetectCallback(livenessModel);
                }
            }
        });
    }

    /**
     * ????????????-??????????????????
     *
     * @param rgbInstance      ???????????????????????????
     * @param landmark         ?????????????????????????????????72????????????
     * @param nirData          nir?????????
     * @param livenessModel    ????????????????????????
     * @param featureCheckMode ???????????????????????????????????????1????????????????????????2?????????????????????+1???N?????????3??????
     * @param featureType      ???????????????????????? ???????????????1?????????????????????2????????????????????????3??????
     */
    private void onFeatureCheck(BDFaceImageInstance rgbInstance,
                                float[] landmark,
                                byte[] nirData,
                                final int srcHeight,
                                final int srcWidth,
                                LivenessModel livenessModel,
                                final int featureCheckMode,
                                final int featureType) {

        // ????????????????????????????????????
        if (featureCheckMode == 1) {
            return;
        }
        byte[] feature = new byte[512];
        if (featureType == 3) {
            // todo: ????????????????????????????????????????????????????????????????????????type??????????????????????????????0~255??????
            AtomicInteger atomicInteger = new AtomicInteger();
            FaceSDKManager.getInstance().getImageIllum().imageIllum(rgbInstance, atomicInteger);
            int illumScore = atomicInteger.get();
            if (illumScore > SingleBaseConfig.getBaseConfig().getIllumination()) {
                if (nirData != null) {
                    BDFaceImageInstance nirInstance = new BDFaceImageInstance(nirData, srcHeight,
                            srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                            SingleBaseConfig.getBaseConfig().getDetectDirection(),
                            SingleBaseConfig.getBaseConfig().getMirrorNIR());
                    BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
                    bdFaceDetectListConfig.usingDetect = true;
                    FaceInfo[] faceInfos = FaceSDKManager.getInstance()
                            .getFaceDetect()
                            .detect(BDFaceSDKCommon.DetectType.DETECT_NIR,
                                    BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                                    nirInstance,
                                    null, bdFaceDetectListConfig);
                    bdFaceDetectListConfig.usingDetect = false;
                    long startFeatureTime = System.currentTimeMillis();
                    float featureSize = FaceSDKManager.getInstance().getFaceFeature().feature(
                            BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_NIR, nirInstance,
                            faceInfos[0].landmarks, feature);
                    nirInstance.destory();
                    livenessModel.setFeatureDuration(System.currentTimeMillis() - startFeatureTime);
                    livenessModel.setFeature(feature);
                    // ????????????
                    featureSearch(featureCheckMode, livenessModel, feature, featureSize,
                            BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_NIR);

                }
            } else {
                long startFeatureTime = System.currentTimeMillis();
                float featureSize = FaceSDKManager.getInstance().getFaceFeature().feature(
                        BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, rgbInstance, landmark, feature);
                livenessModel.setFeatureDuration(System.currentTimeMillis() - startFeatureTime);
                livenessModel.setFeature(feature);
                // ????????????
                featureSearch(featureCheckMode, livenessModel, feature, featureSize,
                        BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);
            }

        } else if (featureType == 2) {
            // ???????????????
            long startFeatureTime = System.currentTimeMillis();
            float featureSize = FaceSDKManager.getInstance().getFaceFeature().feature(
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, landmark, feature);
            livenessModel.setFeatureDuration(System.currentTimeMillis() - startFeatureTime);
            livenessModel.setFeature(feature);
            // ????????????
            featureSearch(featureCheckMode, livenessModel, feature, featureSize,
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO);

        } else {
            // ???????????????
            long startFeatureTime = System.currentTimeMillis();
            float featureSize = FaceSDKManager.getInstance().getFaceFeature().feature(
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO, rgbInstance, landmark, feature);
            livenessModel.setFeatureDuration(System.currentTimeMillis() - startFeatureTime);
            livenessModel.setFeature(feature);
            // ????????????
            featureSearch(featureCheckMode, livenessModel, feature, featureSize,
                    BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);
        }

    }
    /**
     * ???????????????
     *
     * @param featureCheckMode ???????????????????????????????????????1????????????????????????2?????????????????????+1???N?????????3??????
     * @param livenessModel    ????????????????????????
     * @param feature          ?????????
     * @param featureSize      ????????????size
     * @param type             ??????????????????
     */
    private void featureSearch(final int featureCheckMode,
                               LivenessModel livenessModel,
                               byte[] feature,
                               float featureSize,
                               BDFaceSDKCommon.FeatureType type) {

        // ???????????????????????????????????????????????????
        if (featureCheckMode == 2) {
            livenessModel.setFeatureCode(featureSize);
            return;
        }
        // ??????????????????+???????????????search ??????
        if (featureSize == FEATURE_SIZE / 4) {

            // ??????????????????
            // TODO ????????????????????????????????????
            long startFeature = System.currentTimeMillis();
            ArrayList<Feature> featureResult =
                    FaceSDKManager.getInstance().getFaceFeature().featureSearch(feature,
                            type, 1, true);

            // TODO ??????top num = 1 ?????????????????????????????????????????????????????????????????????????????????num ???????????????
            if (featureResult != null && featureResult.size() > 0) {

                // ?????????????????????
                Feature topFeature = featureResult.get(0);
                // ???????????????????????????????????????????????????????????????????????????
                if (SingleBaseConfig.getBaseConfig().getActiveModel() == 1) {
                    threholdScore = SingleBaseConfig.getBaseConfig().getLiveThreshold();
                } else if (SingleBaseConfig.getBaseConfig().getActiveModel() == 2) {
                    threholdScore = SingleBaseConfig.getBaseConfig().getIdThreshold();
                } else if (SingleBaseConfig.getBaseConfig().getActiveModel() == 3) {
                    threholdScore = SingleBaseConfig.getBaseConfig().getRgbAndNirThreshold();
                }
                if (topFeature != null && topFeature.getScore() >
                        threholdScore) {
                    // ??????featureEntity ??????id+feature ??????????????????????????????????????????
                    User user = FaceApi.getInstance().getUserListById(topFeature.getId());
                    if (user != null) {
                        livenessModel.setUser(user);
                        livenessModel.setFeatureScore(topFeature.getScore());
                    }
                }
            }
            livenessModel.setCheckDuration(System.currentTimeMillis() - startFeature);
        }
    }

    /**
     * ???????????? ????????????
     *
     * @param imageInstance       ???????????????????????????
     * @param landmark            ?????????????????????????????????72????????????
     * @param featureCheckMode    ??????????????????
     * @param faceFeatureCallBack ????????????
     */
    public void onFeatureCheck(final BDFaceImageInstance imageInstance, final float[] landmark,
                               final BDFaceSDKCommon.FeatureType featureCheckMode,
                               final FaceFeatureCallBack faceFeatureCallBack) {
        final long startFeatureTime = System.currentTimeMillis();
        if (mRegFuture != null && !mRegFuture.isDone()) {
            return;
        }

        mRegFuture = mRegExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                BDFaceImageInstance rgbInstance = new BDFaceImageInstance(imageInstance.data,
                        imageInstance.height, imageInstance.width,
                        imageInstance.imageType, 0, 0);

                byte[] feature = new byte[512];
                float featureSize = FaceSDKManager.getInstance().getFaceFeature().feature(
                        featureCheckMode, rgbInstance, landmark, feature);
                if (featureSize == FEATURE_SIZE / 4) {
                    // ??????????????????
                    if (faceFeatureCallBack != null) {
                        long endFeatureTime = System.currentTimeMillis() - startFeatureTime;
                        faceFeatureCallBack.onFaceFeatureCallBack(featureSize, feature, endFeatureTime);
                    }

                }
                // ????????????????????????
                rgbInstance.destory();
            }
        });
    }

    // ???????????????????????????
    public void onAttrDetectCheck(final byte[] rgbData,
                                  final byte[] nirData,
                                  final byte[] depthData,
                                  final int srcHeight,
                                  final int srcWidth,
                                  final int liveCheckMode,
                                  final FaceDetectCallBack faceDetectCallBack) {

        onDetectCheck(rgbData, nirData, depthData, srcHeight, srcWidth, liveCheckMode, 1, faceDetectCallBack);
    }

    // ????????????
    public float personDetect(final Bitmap bitmap, final byte[] feature, Context context) {
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        float ret = -1;
//        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
//                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
//        FaceInfo[] faceInfos = faceDetectPerson.detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
//                rgbInstance);
        BDFaceDetectListConf bdFaceDetectListConfig = new BDFaceDetectListConf();
        bdFaceDetectListConfig.usingQuality = bdFaceDetectListConfig.usingHeadPose
                = SingleBaseConfig.getBaseConfig().isQualityControl();
        bdFaceDetectListConfig.usingAttribute = SingleBaseConfig.getBaseConfig().isAttribute();
        bdFaceDetectListConfig.usingDetect = true;

        FaceInfo[] faceInfos = faceDetectPerson.detect(BDFaceSDKCommon.DetectType.DETECT_VIS,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_RGB_ACCURATE,
                rgbInstance,
                null, bdFaceDetectListConfig);

        if (faceInfos != null && faceInfos.length > 0) {
            // ??????????????????????????????????????????????????????
            if (qualityCheck(faceInfos[0], context)) {
                ret = faceFeaturePerson.feature(BDFaceSDKCommon.FeatureType.
                        BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
            }
        } else {
            rgbInstance.destory();
            return -1;
        }
        rgbInstance.destory();
        return ret;
    }

    // ???????????????
    public BDFaceGazeInfo gazeDetect(final LivenessModel livenessModel) {
        BDFaceImageInstance bdFaceImageInstance = new BDFaceImageInstance(livenessModel.getBdFaceImageInstance().data,
                livenessModel.getBdFaceImageInstance().height, livenessModel.getBdFaceImageInstance().width,
                BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_RGB,
                SingleBaseConfig.getBaseConfig().getDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorRGB());
        BDFaceGazeInfo bdFaceGazeInfo = faceGaze.gaze(bdFaceImageInstance, livenessModel.getLandmarks());
        bdFaceImageInstance.destory();
        return bdFaceGazeInfo;
    }

    // ??????????????????
    public DriverInfo driverMonitorDetect(byte[] nirData, int srcWidth, int srcHeight) {
        driverInfo = null;
        BDFaceImageInstance nirInstance = new BDFaceImageInstance(nirData, srcHeight,
                srcWidth, BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21,
                SingleBaseConfig.getBaseConfig().getDetectDirection(),
                SingleBaseConfig.getBaseConfig().getMirrorNIR());

        // ??????RGB??????????????????IR???????????????????????????????????????
        BDFaceDetectListConf bdFaceDetectListConf = new BDFaceDetectListConf();
        bdFaceDetectListConf.usingDetect = true;
        FaceInfo[] faceInfosIr = faceDetectNir.detect(BDFaceSDKCommon.DetectType.DETECT_NIR,
                BDFaceSDKCommon.AlignType.BDFACE_ALIGN_TYPE_NIR_ACCURATE,
                nirInstance, null, bdFaceDetectListConf);
        bdFaceDetectListConf.usingDetect = false;
        if (faceInfosIr != null) {
            driverInfo = new DriverInfo();
            long startTime = System.currentTimeMillis();
            bdFaceDriverMonitorInfo = faceDriverMonitor.driverMonitor(nirInstance, faceInfosIr[0]);
            long endTime = System.currentTimeMillis() - startTime;
            driverInfo.setBdFaceDriverMonitorInfo(bdFaceDriverMonitorInfo);
            driverInfo.setTime(endTime);
        }
        nirInstance.destory();
        return driverInfo;
    }

    public String getLicenseData(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy???MM???dd???");
        BDFaceLicenseAuthInfo bdFaceLicenseAuthInfo = faceAuth.getAuthInfo(context);
        Date dateLong = new Date(bdFaceLicenseAuthInfo.expireTime * 1000L);
        String dateTime = simpleDateFormat.format(dateLong);
        return dateTime;
    }

    /**
     * ????????????
     * FaceInfo faceInfo
     *
     * @return
     */
    public boolean qualityCheck(final FaceInfo faceInfo, Context context) {

//        // ??????????????????????????????????????????????????????????????????????????????????????????????????????
//        if (!isFromPhotoLibrary) {
//            return true;
//        }

        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return true;
        }
        if (faceInfo != null) {
            // ??????????????????
            float blur = faceInfo.bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                ToastUtils.toast(context, "????????????");
                return false;
            }
            // ??????????????????
            float illum = faceInfo.illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                ToastUtils.toast(context, "?????????????????????");
                return false;
            }
            // ??????????????????
            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;
                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // ?????????????????????
                    ToastUtils.toast(context, "????????????");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ????????????
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
//        if (faceDetect.uninitModel() == 0
//                && faceFeature.uninitModel() == 0
//                && faceDetectNir.uninitModel() == 0
//                && faceLiveness.uninitModel() == 0) {
//            initStatus = SDK_UNACTIVATION;
//            initModelSuccess = false;
//        }
//    }
}