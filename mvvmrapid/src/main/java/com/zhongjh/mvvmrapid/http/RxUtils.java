package com.zhongjh.mvvmrapid.http;

import com.zhongjh.mvvmrapid.http.ExceptionHandle;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhongjh on 2021/3/26.
 * 有关Rx的工具类
 */
public class RxUtils {

    /**
     * 线程调度器
     */
    public static <T> ObservableTransformer<T, T> io2main() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 异常调度
     */
    public static <T> ObservableTransformer<T, T> exceptionTransformer() {
        return upstream -> upstream.onErrorResumeNext(new HttpResponseFunc<>());
    }

    private static class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
        @Override
        public Observable<T> apply(@NotNull Throwable t) {
            return Observable.error(ExceptionHandle.handleException(t));
        }
    }

}
