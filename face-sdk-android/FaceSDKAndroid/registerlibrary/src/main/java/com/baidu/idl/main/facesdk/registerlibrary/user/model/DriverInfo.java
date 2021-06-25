package com.baidu.idl.main.facesdk.registerlibrary.user.model;

import com.baidu.idl.main.facesdk.model.BDFaceDriverMonitorInfo;

/**
 * author : baidu
 * date : 2020-02-17 17:37
 * description :
 */
public class DriverInfo {
    private BDFaceDriverMonitorInfo bdFaceDriverMonitorInfo;
    private long time;

    public BDFaceDriverMonitorInfo getBdFaceDriverMonitorInfo() {
        return bdFaceDriverMonitorInfo;
    }

    public void setBdFaceDriverMonitorInfo(BDFaceDriverMonitorInfo bdFaceDriverMonitorInfo) {
        this.bdFaceDriverMonitorInfo = bdFaceDriverMonitorInfo;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
