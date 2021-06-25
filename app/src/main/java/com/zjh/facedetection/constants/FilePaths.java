package com.zjh.facedetection.constants;

import android.content.Context;

import java.io.File;

/**
 * 文件名称
 * <p>
 * 外部存储
 * context.getExternalFilesDir(dir)	路径为:/mnt/sdcard/Android/data/< package name >/files/… 
 * context.getExternalCacheDir()	路径为:/mnt/sdcard//Android/data/< package name >/cach/…
 *  内部存储
 * context.getFilesDir()	路径是:/data/data/< package name >/files/…
 * context.getCacheDir()	路径是:/data/data/< package name >/cach/…
 *
 * @author zhongjh
 * @date 2021/5/13
 */
public class FilePaths {

    /**
     * 屏保中的视频文件名称，用于防止删除
     */
    public static final String VIDEO_NAME_SCREEN_IN = "videoNameScreenIn";

    /**
     * 当前下载的视频文件，用于防止删除
     */
    public static final String VIDEO_DOWN_LOAD = "videoDownload";

    /**
     * 屏保中的图片文件名称，用于防止删除
     */
    public static final String IMAGE_NAME_SCREEN_IN = "imageNameScreenIn";

    /**
     * 当前下载的图片文件，用于防止删除
     */
    public static final String IMAGE_DOWN_LOAD = "imageDownload";

    /**
     * 记录日志文件
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static File log(Context context) {
        return context.getExternalFilesDir(File.separator + "log" + File.separator);
    }

    /**
     * 视频文件夹
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static File videoDir(Context context) {
        return context.getExternalFilesDir("video");
    }

    /**
     * 视频文件
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 文件路径
     */
    public static File createVideoFile(Context context, String fileName) {
        return new File(context.getExternalFilesDir("video").getPath() + File.separator + fileName);
    }

    /**
     * 图片文件夹
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static File imageDir(Context context) {
        return context.getExternalFilesDir("image");
    }

    /**
     * 图片文件
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 文件路径
     */
    public static File createImageFile(Context context, String fileName) {
        return new File(context.getExternalFilesDir("image").getPath() + File.separator + fileName);
    }

    /**
     * 语音文件夹
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static File recordDir(Context context) {
        return context.getExternalFilesDir("record");
    }

    /**
     * 语音文件
     *
     * @param context  上下文
     * @param fileName 文件名称
     * @return 文件路径
     */
    public static String createRecordFile(Context context, String fileName) {
        return context.getExternalFilesDir("record").getPath() + File.separator + fileName;
    }

}
