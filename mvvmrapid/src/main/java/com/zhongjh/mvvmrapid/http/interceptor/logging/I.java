package com.zhongjh.mvvmrapid.http.interceptor.logging;


import java.util.logging.Level;

import okhttp3.internal.platform.Platform;

/**
 * Created by zhongjh on 2021/3/25.
 */
class I {

    protected I() {
        throw new UnsupportedOperationException();
    }

    static void log(int type, String tag, String msg) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(tag);
        if (type == Platform.INFO) {
            logger.log(Level.INFO, msg);
        } else {
            logger.log(Level.WARNING, msg);
        }
    }
}
