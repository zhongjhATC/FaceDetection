package com.baidu.idl.main.facesdk.registerlibrary.user.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.registerlibrary.user.api.FaceApi;
import com.baidu.idl.main.facesdk.registerlibrary.user.listener.OnImportListener;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.ImportFeatureResult;
import com.baidu.idl.main.facesdk.registerlibrary.user.model.User;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.BitmapUtils;
import com.baidu.idl.main.facesdk.registerlibrary.user.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 导入相关管理类
 * Created by v_liujialu01 on 2019/5/28.
 */

public class ImportFileManager {
    private static final String TAG = "ImportFileManager";

    private Future mFuture;
    private ExecutorService mExecutorService;
    private OnImportListener mImportListener;
    // 是否需要导入
    private volatile boolean mIsNeedImport;

    private int mTotalCount;
    private int mFinishCount;
    private int mSuccessCount;
    private int mFailCount;

    private static class HolderClass {
        private static final ImportFileManager instance = new ImportFileManager();
    }

    public static ImportFileManager getInstance() {
        return HolderClass.instance;
    }

    // 私有构造，实例化ExecutorService
    private ImportFileManager() {
        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }
    }

    public void setOnImportListener(OnImportListener importListener) {
        mImportListener = importListener;
    }

    /**
     * 开始批量导入
     */
    public void batchImport() {
        // 1、获取导入目录 /sdcard/Face-Import
        File batchImportDir = FileUtils.getBatchImportDirectory();
        // 2、遍历该目录下的所有文件
        File[] picFiles = batchImportDir.listFiles();
        if (picFiles == null || picFiles.length == 0) {
            Log.i(TAG, "导入数据的文件夹没有数据");
            if (mImportListener != null) {
                mImportListener.showToastMessage("导入数据的文件夹没有数据");
            }
            return;
        }

        // 开启线程导入图片
        asyncImport(picFiles);
    }

    public void setIsNeedImport(boolean isNeedImport) {
        mIsNeedImport = isNeedImport;
    }

    /**
     * 开启线程导入图片
     * @param picFiles  要导入的图片集
     */
    private void asyncImport(final File[] picFiles) {
        mIsNeedImport = true;     // 判断是否需要导入
        mFinishCount = 0;         // 已完成的图片数量
        mSuccessCount = 0;        // 已导入成功的图片数量
        mFailCount = 0;           // 已导入失败的图片数量

        if (mExecutorService == null) {
            mExecutorService = Executors.newSingleThreadExecutor();
        }

        mFuture = mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if (picFiles == null || picFiles.length == 0) {
                        Log.i(TAG, "导入数据的文件夹没有数据");
                        if (mImportListener != null) {
                            mImportListener.showToastMessage("导入数据的文件夹没有数据");
                        }
                        return;
                    }

                    // 读取图片成功，开始显示进度条
                    if (mImportListener != null) {
                        mImportListener.showProgressView();
                    }

                    Thread.sleep(400);

                    mTotalCount = picFiles.length;  // 总图片数

                    for (int i = 0; i < picFiles.length; i++) {
                        if (!mIsNeedImport) {
                            break;
                        }

                        // 3、获取图片名
                        String picName = picFiles[i].getName();
                        Log.e(TAG, "i = " + i + ", picName = " + picName);
                        // 4、判断图片后缀
                        if (!picName.endsWith(".jpg") && !picName.endsWith(".png")) {
                            Log.e(TAG, "图片后缀不满足要求");
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mTotalCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 5、获取不带后缀的图片名，即用户名
                        String userName = FileUtils.getFileNameNoEx(picName);

                        boolean success = false;  // 判断成功状态

                        // 6、判断姓名是否有效
                        String nameResult = FaceApi.getInstance().isValidName(userName);
                        if (!"0".equals(nameResult)) {
                            Log.i(TAG, nameResult);
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mTotalCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 7、根据姓名查询数据库与文件中对应的姓名是否相等，如果相等，则直接过滤
                        List<User> listUsers = FaceApi.getInstance().getUserListByUserName(userName);
                        if (listUsers != null && listUsers.size() > 0) {
                            Log.i(TAG, "与之前图片名称相同");
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mTotalCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 8、根据图片的路径将图片转成Bitmap
                        Bitmap bitmap = BitmapFactory.decodeFile(picFiles[i].getAbsolutePath());

                        // 9、判断bitmap是否转换成功
                        if (bitmap == null) {
                            Log.e(TAG, picName + "：该图片转成Bitmap失败");
                            mFinishCount++;
                            mFailCount++;
                            // 更新进度
                            updateProgress(mTotalCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                            continue;
                        }

                        // 图片缩放
                        if (bitmap.getWidth() * bitmap.getHeight() > 3000 * 2000) {
                            if (bitmap.getWidth() > bitmap.getHeight()) {
                                float scale = 1 / (bitmap.getWidth() * 1.0f / 1000.0f);
                                bitmap = BitmapUtils.scale(bitmap, scale);
                            } else {
                                float scale = 1 / (bitmap.getHeight() * 1.0f / 1000.0f);
                                bitmap = BitmapUtils.scale(bitmap, scale);
                            }
                        }

                        byte[] bytes = new byte[512];
                        ImportFeatureResult result;
                        // 10、走人脸SDK接口，通过人脸检测、特征提取拿到人脸特征值
                        result = FaceApi.getInstance().getFeature(bitmap, bytes,
                                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_LIVE_PHOTO);

                        // 11、判断是否提取成功：128为成功，-1为参数为空，-2表示未检测到人脸
                        Log.i(TAG, "live_photo = " + result.getResult());
                        if (result.getResult() == -1) {
                            Log.e(TAG, picName + "：bitmap参数为空");
                        } else if (result.getResult() == -2) {
                            Log.e(TAG, picName + "：未检测到人脸");
                        } else if (result.getResult() == -3) {
                            Log.e(TAG, picName + "：抠图失败");
                        } else if (result.getResult() == 128) {
                            // 将用户信息保存到数据库中
                            boolean importDBSuccess = FaceApi.getInstance().registerUserIntoDBmanager(null,
                                    userName, picName, null, bytes);

                            // 保存数据库成功
                            if (importDBSuccess) {
                                // 保存图片到新目录中
                                File facePicDir = FileUtils.getBatchImportSuccessDirectory();
                                if (facePicDir != null) {
                                    File savePicPath = new File(facePicDir, picName);
                                    if (FileUtils.saveBitmap(savePicPath, result.getBitmap())) {
                                        Log.i(TAG, "图片保存成功");
                                        success = true;
                                    } else {
                                        Log.i(TAG, "图片保存失败");
                                    }
                                }
                            } else {
                                Log.e(TAG, picName + "：保存到数据库失败");
                            }
                        } else {
                            Log.e(TAG, picName + "：未检测到人脸");
                        }

                        // 图片回收
                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();
                        }

                        // 判断成功与否
                        if (success) {
                            mSuccessCount++;
                        } else {
                            mFailCount++;
                            Log.e(TAG, "失败图片:" + picName);
                        }
                        mFinishCount++;
                        // 导入中（用来显示进度）
                        Log.i(TAG, "mFinishCount = " + mFinishCount
                                + " progress = " + ((float) mFinishCount / (float) mTotalCount));
                        if (mImportListener != null) {
                            mImportListener.onImporting(mTotalCount, mSuccessCount, mFailCount,
                                    ((float) mFinishCount / (float) mTotalCount));
                        }
                    }

                    // 导入完成
                    if (mImportListener != null) {
                        mImportListener.endImport(mTotalCount, mSuccessCount, mFailCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "exception = " + e.getMessage());
                }
            }
        });
    }

    private void updateProgress(int totalCount, int successCount, int failureCount, float progress) {
        if (mImportListener != null) {
            mImportListener.onImporting(totalCount, successCount, failureCount, progress);
        }
    }

    /**
     * 释放功能，用于关闭线程操作
     */
    public void release() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }

        if (mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
        }
    }
}
