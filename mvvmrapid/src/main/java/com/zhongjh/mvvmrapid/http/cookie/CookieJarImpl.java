package com.zhongjh.mvvmrapid.http.cookie;


import com.zhongjh.mvvmrapid.http.cookie.store.CookieStore;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by zhongjh on 2021/3/25.
 */
public class CookieJarImpl implements CookieJar {

    private final CookieStore cookieStore;

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
        cookieStore.saveCookie(url, cookies);
    }

    @NotNull
    @Override
    public synchronized List<Cookie> loadForRequest(@NotNull HttpUrl url) {
        return cookieStore.loadCookie(url);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}