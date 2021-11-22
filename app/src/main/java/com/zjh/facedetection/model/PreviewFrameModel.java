package com.zjh.facedetection.model;


import android.hardware.Camera;

/**
 * @author zhongjh
 * @date 2021/11/22
 */
public class PreviewFrameModel {

    byte[] bytes;
    Camera camera;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
