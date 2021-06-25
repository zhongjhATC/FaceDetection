package com.zhongjh.mvvmrapid.http.interceptor.logging;

import okhttp3.internal.platform.Platform;

/**
 * Created by zhongjh on 2021/3/25.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface Logger {
    void log(int level, String tag, String msg);

    Logger DEFAULT = (level, tag, message) -> Platform.get().log(message, level, null);
}
