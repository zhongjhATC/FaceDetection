package com.zhongjh.mvvmrapid.base.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zhongjh.mvvmrapid.R;
import com.zhongjh.mvvmrapid.base.viewmodel.BaseViewModel;
import com.zhongjh.mvvmrapid.utils.DialogUtil;
import com.zhongjh.mvvmrapid.utils.InputMethodUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zhongjh
 * @date 2021/3/25
 */
public abstract class BaseActivity<V extends ViewDataBinding, VM extends BaseViewModel> extends RxAppCompatActivity implements IBaseView {

    protected V binding;
    public VM viewModel;
    protected ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化参数方法
        initParam();
        // 初始化 DataBinding 、ViewModel
        initViewDataBinding(savedInstanceState);
        // 判断是否开启自动隐藏软键盘
        if (isStartAutoHideSoftKeyboard()) {
            startAutoHideSoftKeyboard(((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));
        }
        // 初始化UI回调事件
        initUiChangeLiveDataCallBack();
        // 初始化数据
        initData();
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        // 注册RxBus
        viewModel.registerRxBus();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
    }

    /**
     * 注入绑定
     */
    @SuppressWarnings("unchecked")
    private void initViewDataBinding(Bundle savedInstanceState) {
        // dataBinding {enabled true } 开启dataBinding
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState));
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
        // 关联ViewModel
        binding.setVariable(viewModelId, viewModel);
        // 关联LiveData绑定xml，数据改变，UI自动会更新
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
        return new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(cls);
    }

    /**
     * 是否给该Activity添加自动隐藏软键盘
     *
     * @param view view
     */
    @SuppressLint("ClickableViewAccessibility")
    private void startAutoHideSoftKeyboard(View view) {
        // 为非文本框视图设置触摸监听器以隐藏键盘
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                InputMethodUtil.hideSoftKeyboard(BaseActivity.this);
                return false;
            });
        }

        // 如果是布局容器，则递归遍历子元素和种子。
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                startAutoHideSoftKeyboard(innerView);
            }
        }
    }

    // region 公共的回调事件

    /**
     * 初始化UI回调事件
     */
    private void initUiChangeLiveDataCallBack() {
        // 等待对话框显示
        viewModel.getUC().getShowDialogEvent().observe(this, s -> showDialog());

        // 等待对话框消失
        viewModel.getUC().getDismissDialogEvent().observe(this, v -> dismissDialog());

        // 跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(this, params -> {
            Class<?> clz = (Class<?>) params.get(BaseViewModel.ParameterField.CLASS);
            Bundle bundle = (Bundle) params.get(BaseViewModel.ParameterField.BUNDLE);
            startActivity(clz, bundle);
        });

        // 关闭界面
        viewModel.getUC().getFinishEvent().observe(this, v -> finish());
        // 带有动画的退出
        viewModel.getUC().getFinishAnimationEvent().observe(this, aVoid -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(this, v -> onBackPressed());

        // 开启触屏
        viewModel.getUC().getOpenTouchingEvent().observe(this, v -> openTouching());

        // 关闭触屏
        viewModel.getUC().getCloseTouchingEvent().observe(this, v -> closeTouching());
    }

    /**
     * 等待对话框显示
     */
    public void showDialog() {
        DialogUtil.showDialogProgressBar(BaseActivity.this, progressDialog);
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
        Intent intent = new Intent(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 关闭触摸，一般用于dialog的时候禁止触摸防止别的意外
     */
    public void closeTouching() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * 开启触摸
     */
    public void openTouching() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    // endregion

    // region 公开的API

    /**
     * 初始化根布局
     * @param savedInstanceState savedInstanceState
     * @return 布局layout的id
     */
    public abstract int initContentView(Bundle savedInstanceState);

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

    /**
     * 初始化toolbar
     */
    public void initToolbar(Toolbar toolbar, String title, boolean isNavigation) {
        toolbar.setTitle(title);
        if (isNavigation) {
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_24);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    /**
     * 因为用的是循环整个view添加onTouch事件，所以酿情决定，最好是登录、修改密码等等简易界面
     *
     * @return 是否开启自动化缩小软键盘
     */
    public abstract boolean isStartAutoHideSoftKeyboard();

    // endregion


}
