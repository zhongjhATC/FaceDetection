package com.zhongjh.mvvmrapid.http.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhongjh on 2021/3/25.
 * 请求头，有的会用于token等之类的
 */
public class BaseInterceptor implements Interceptor {

    private final Map<String, String> headers;

    public BaseInterceptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey, Objects.requireNonNull(headers.get(headerKey))).build();
            }
        }
        // 请求信息
        return chain.proceed(builder.build());
    }
}