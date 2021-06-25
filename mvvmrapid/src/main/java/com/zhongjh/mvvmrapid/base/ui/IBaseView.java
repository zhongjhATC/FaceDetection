package com.zhongjh.mvvmrapid.base.ui;

/**
 * 区分相关方法，方便维护
 * @author zhongjh
 */
public interface IBaseView {
    /**
     * 初始化界面传递参数
     */
    void initParam();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * 初始化界面观察者的监听
     */
    void initViewObservable();
}
