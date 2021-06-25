package com.zhongjh.mvvmrapid.base.viewmodel;

import android.app.Application;
import android.os.Bundle;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.zhongjh.mvvmrapid.bus.event.SingleLiveEvent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 继承于AndroidViewModel
 * 实现IBaseViewModel即是lifecycle
 */
@SuppressWarnings("ALL")
public class BaseViewModel extends AndroidViewModel implements IBaseViewModel, Consumer<Disposable> {

    private UIChangeLiveData uc;
    // 弱引用持有
    private WeakReference<LifecycleProvider> lifecycle;
    // 用于RxJava异步操作销毁，防止内存泄漏
    private CompositeDisposable mCompositeDisposable;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 用于RxJava加入mCompositeDisposable后面统一销毁
     *
     * @param disposable rx的disposable
     */
    protected void addSubscribe(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 注入RxLifecycle生命周期
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = new WeakReference<>(lifecycle);
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle.get();
    }

    public UIChangeLiveData getUC() {
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }

    /**
     * 关闭dialog
     */
    public void dismissDialog() {
        uc.dismissDialogEvent.call();
    }

    /**
     * 自定义标题的界面
     */
    public void showDialog() {
        uc.showDialogEvent.call();
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(clz, null);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Map<String, Object> params = new HashMap<>();
        params.put(ParameterField.CLASS, clz);
        if (bundle != null) {
            params.put(ParameterField.BUNDLE, bundle);
        }
        uc.startActivityEvent.postValue(params);
    }

    /**
     * 关闭界面
     */
    public void finish() {
        uc.finishEvent.call();
    }

    /**
     * 关闭界面,带有动画
     */
    public void finishAnimation() {
        uc.finishAnimationEvent.call();
    }


    /**
     * 返回上一层
     */
    public void onBackPressed() {
        uc.onBackPressedEvent.call();
    }

    /**
     * 关闭触摸，一般用于dialog的时候禁止触摸防止别的意外
     */
    public void closeTouching() {
        uc.closeTouchingEvent.call();
    }

    /**
     * 开启触摸
     */
    public void openTouching() {
        uc.openTouchingEvent.call();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // ViewModel销毁时会执行，同时取消所有异步任务
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }

    // region Lifecycle 生命周期

    @Override
    public void onAny(LifecycleOwner owner, Lifecycle.Event event) {
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void registerRxBus() {

    }

    @Override
    public void removeRxBus() {

    }

    @Override
    public void accept(Disposable disposable) {
        addSubscribe(disposable);
    }

    // endregion

    public static final class UIChangeLiveData extends SingleLiveEvent {

        private SingleLiveEvent<Void> showDialogEvent;
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Map<String, Object>> startActivityEvent;
        private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> finishAnimationEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;
        private SingleLiveEvent<Void> closeTouchingEvent;
        private SingleLiveEvent<Void> openTouchingEvent;

        public SingleLiveEvent<Void> getShowDialogEvent() {
            return showDialogEvent = createLiveData(showDialogEvent);
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            return dismissDialogEvent = createLiveData(dismissDialogEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
            return startContainerActivityEvent = createLiveData(startContainerActivityEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<Void> getFinishAnimationEvent() {
            return finishAnimationEvent = createLiveData(finishAnimationEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        public SingleLiveEvent<Void> getCloseTouchingEvent() {
            return closeTouchingEvent = createLiveData(closeTouchingEvent);
        }

        public SingleLiveEvent<Void> getOpenTouchingEvent() {
            return openTouchingEvent = createLiveData(openTouchingEvent);
        }

        private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent<>();
            }
            return liveData;
        }

        @Override
        public void observe(@NonNull LifecycleOwner owner, @NonNull final Observer observer) {
            super.observe(owner, observer);
        }
    }

    public static final class ParameterField {
        public static String CLASS = "CLASS";
        public static String CANONICAL_NAME = "CANONICAL_NAME";
        public static String BUNDLE = "BUNDLE";
    }

}
