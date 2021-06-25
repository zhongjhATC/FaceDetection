package com.zjh.facedetection.ui.main;

import android.content.Context;
import android.media.MediaRecorder;

import com.zjh.facedetection.constants.FilePaths;

/**
 * 录音功能
 *
 * @author zhongjh
 */
public class RecordManager {

    private static final String TAG = RecordManager.class.getSimpleName();

    private final Context mContext;

    private MediaRecorder mRecorder = null;

    private AudioCallback mAudioCallback = null;

    /**
     * 最后录音的一个文件名称
     */
    public String recordName;

    public interface AudioCallback {
        /**
         * 结束录音
         */
        void stopRecord();

        /**
         * 录音失败
         */
        void recordError(String message);
    }

    public RecordManager(Context context) {
        mContext = context;
    }

    public void setAudioCallback(AudioCallback audioCallback) {
        mAudioCallback = audioCallback;
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        // 创建语音路径
        recordName = System.currentTimeMillis() + ".mp3";
        String path = FilePaths.createRecordFile(mContext, recordName);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(path);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            if (mAudioCallback != null) {
                mAudioCallback.recordError("prepare() failed" + e.toString());
            }
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
            } catch (RuntimeException ignored) {
                // 防止立即录音完成
            }
            mRecorder.release();
            mRecorder = null;
            if (mAudioCallback != null) {
                mAudioCallback.stopRecord();
            }
        }
    }

}
