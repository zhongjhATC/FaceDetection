package com.zhongjh.mvvmrapid.binding.viewadapter.viewgroup;


import androidx.databinding.ViewDataBinding;

public interface IBindingItemViewModel<V extends ViewDataBinding> {
    void injecDataBinding(V binding);
}
