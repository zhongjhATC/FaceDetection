package com.zjh.facedetection.model;

/**
 * @author zhongjh
 * @date 2021/5/31
 */
public class ErrorModel {

    int errCode;
    String errMsg;

    public ErrorModel(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
