package com.zhongjh.mvvmrapid.http;

/**
 * Created by zhongjh on 2021/3/26.
 */
public class ResponseThrowable extends Exception {
    public int code;
    public String message;

    public ResponseThrowable(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }
}
