package com.zhongjh.mvvmrapid.base.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zhongjh.mvvmrapid.base.BaseApplication;
import com.zhongjh.mvvmrapid.base.viewmodel.BaseViewModel;
import com.zhongjh.mvvmrapid.utils.DialogUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author zhongjh
 * @date 2021/3/25
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment implements IBaseView {

    public static String TAG = BaseFragment.class.getSimpleName();

    protected V binding;
    protected VM viewModel;
    protected ProgressDialog progressDialog;
    protected Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
    }

    @Override
    public void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        // 不再保存Fragment的状态，达到fragment随MyActivity一起销毁的目的。
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 私有的初始化DataBinding和ViewModel方法
        initViewDataBinding();
        // 私有的ViewModel与View的契约事件回调逻辑
        initUiChangeLiveDataCallBack();
        // 页面数据初始化方法
        initData();
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        // 注册RxBus
        viewModel.registerRxBus();
    }

    /**
     * 注入绑定
     */
    @SuppressWarnings("unchecked")
    private void initViewDataBinding() {
        int viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class<BaseViewModel> modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                // 获取泛型中的索引第2个，所以第二个都要固定写继承于BaseViewModel
                modelClass = (Class<BaseViewModel>) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                // 抛出异常
                throw new NullPointerException("必须加入BaseViewModel");
            }
            viewModel = (VM) createViewModel(modelClass);
        }
        binding.setVariable(viewModelId, viewModel);
        // 支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        // 让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        // 注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
    }

    /**
     * 创建ViewModel
     *
     * @param cls class
     * @return ViewModel
     */
    public BaseViewModel createViewModel(Class<BaseViewModel> cls) {
        return new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(BaseApplication.getInstance())).get(cls);
    }

    // region 公共的回调事件

    /**
     * 初始化UI回调事件
     */
    private void initUiChangeLiveDataCallBack() {
        // 等待对话框显示
        viewModel.getUC().getShowDialogEvent().observe(getViewLifecycleOwner(), s -> showDialog());

        // 等待对话框消失
        viewModel.getUC().getDismissDialogEvent().observe(getViewLifecycleOwner(), v -> dismissDialog());

        // 跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(getViewLifecycleOwner(), params -> {
            Class<?> clz = (Class<?>) params.get(BaseViewModel.ParameterField.CLASS);
            Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
            startActivity(clz, bundle);
        });

        // 关闭界面
        viewModel.getUC().getFinishEvent().observe(getViewLifecycleOwner(), v -> Objects.requireNonNull(getActivity()).finish());
        // 带有动画的退出
        viewModel.getUC().getFinishAnimationEvent().observe(getViewLifecycleOwner(), aVoid -> {
            if (getActivity() != null) {
                getActivity().finish();
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        // 关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(getViewLifecycleOwner(), v -> Objects.requireNonNull(getActivity()).onBackPressed());

        // 开启触屏
        viewModel.getUC().getOpenTouchingEvent().observe(getViewLifecycleOwner(), v -> openTouching());

        // 关闭触屏
        viewModel.getUC().getCloseTouchingEvent().observe(getViewLifecycleOwner(), v -> closeTouching());
    }

    /**
     * 等待对话框显示
     */
    public void showDialog() {
        DialogUtil.showDialogProgressBar(activity, progressDialog);
    }

    /**
     * 等待对话框消失
     */
    public void dismissDialog() {
        DialogUtil.dismissDialogProgressBar(progressDialog);
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 关闭触摸，一般用于dialog的时候禁止触摸防止别的意外
     */
    public void closeTouching() {
        if (getActivity() != null) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    /**
     * 开启触摸
     */
    public void openTouching() {
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    // endregion

    // region 公开的API

    /**
     * 初始化根布局
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState savedInstanceState
     * @return 布局layout的id
     */
    public abstract int initContentView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化ViewModel Id
     *
     * @return BR的id
     */
    public abstract int initVariableId();

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    // endregion

}
