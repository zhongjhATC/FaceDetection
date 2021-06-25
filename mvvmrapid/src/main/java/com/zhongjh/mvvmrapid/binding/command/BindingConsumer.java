package com.zhongjh.mvvmrapid.binding.command;

/**
 * 带参数的回调操作
 */
public interface BindingConsumer<T> {
    void call(T t);
}
