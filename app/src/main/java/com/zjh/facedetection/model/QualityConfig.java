package com.zjh.facedetection.model;

import org.json.JSONObject;

public class QualityConfig {
    /**
     * 光照 最大
     */
    private float minIllum;
    /**
     * 光照 最小
     */
    private float maxIllum;
    /**
     * 模糊
     */
    private float blur;
    /**
     * 遮挡左眼
     */
    private float leftEyeOcclusion;
    /**
     * 遮挡右眼
     */
    private float rightEyeOcclusion;
    /**
     * 遮挡鼻子
     */
    private float noseOcclusion;
    /**
     * 遮挡嘴巴
     */
    private float mouseOcclusion;
    /**
     * 遮挡左脸颊
     */
    private float leftContourOcclusion;
    /**
     * 遮挡右脸颊
     */
    private float rightContourOcclusion;
    /**
     * 遮挡下巴
     */
    private float chinOcclusion;

    /**
     * 姿态角上下角
     */
    private int pitch;
    /**
     * 姿态角左右角
     */
    private int yaw;
    /**
     * 姿态角旋转角
     */
    private int roll;

    public float getMinIllum() {
        return minIllum;
    }

    public void setMinIllum(float minIllum) {
        this.minIllum = minIllum;
    }

    public float getMaxIllum() {
        return maxIllum;
    }

    public void setMaxIllum(float maxIllum) {
        this.maxIllum = maxIllum;
    }

    public float getBlur() {
        return blur;
    }

    public void setBlur(float blur) {
        this.blur = blur;
    }

    public float getLeftEyeOcclusion() {
        return leftEyeOcclusion;
    }

    public void setLeftEyeOcclusion(float leftEyeOcclusion) {
        this.leftEyeOcclusion = leftEyeOcclusion;
    }

    public float getRightEyeOcclusion() {
        return rightEyeOcclusion;
    }

    public void setRightEyeOcclusion(float rightEyeOcclusion) {
        this.rightEyeOcclusion = rightEyeOcclusion;
    }

    public float getNoseOcclusion() {
        return noseOcclusion;
    }

    public void setNoseOcclusion(float noseOcclusion) {
        this.noseOcclusion = noseOcclusion;
    }

    public float getMouseOcclusion() {
        return mouseOcclusion;
    }

    public void setMouseOcclusion(float mouseOcclusion) {
        this.mouseOcclusion = mouseOcclusion;
    }

    public float getLeftContourOcclusion() {
        return leftContourOcclusion;
    }

    public void setLeftContourOcclusion(float leftContourOcclusion) {
        this.leftContourOcclusion = leftContourOcclusion;
    }

    public float getRightContourOcclusion() {
        return rightContourOcclusion;
    }

    public void setRightContourOcclusion(float rightContourOcclusion) {
        this.rightContourOcclusion = rightContourOcclusion;
    }

    public float getChinOcclusion() {
        return chinOcclusion;
    }

    public void setChinOcclusion(float chinOcclusion) {
        this.chinOcclusion = chinOcclusion;
    }

    public int getPitch() {
        return pitch;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public int getYaw() {
        return yaw;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    /**
     * 解析json文件的内容
     * @param jsonObject  json数据
     */
    public void parseFromJSONObject(JSONObject jsonObject) {
        minIllum = (float) jsonObject.optDouble("minIllum");
        maxIllum = (float) jsonObject.optDouble("maxIllum");
        blur = (float) jsonObject.optDouble("blur");
        leftEyeOcclusion = (float) jsonObject.optDouble("leftEyeOcclusion");
        rightEyeOcclusion = (float) jsonObject.optDouble("rightEyeOcclusion");
        noseOcclusion = (float) jsonObject.optDouble("noseOcclusion");
        mouseOcclusion = (float) jsonObject.optDouble("mouseOcclusion");
        leftContourOcclusion = (float) jsonObject.optDouble("leftContourOcclusion");
        rightContourOcclusion = (float) jsonObject.optDouble("rightContourOcclusion");
        chinOcclusion = (float) jsonObject.optDouble("chinOcclusion");
        pitch = jsonObject.optInt("pitch");
        yaw = jsonObject.optInt("yaw");
        roll = jsonObject.optInt("roll");
    }
}
