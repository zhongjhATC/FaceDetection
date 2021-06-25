package com.zhongjh.mvvmrapid.binding.viewadapter.swiperefresh;


import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zhongjh.mvvmrapid.binding.command.BindingCommand;

public class ViewAdapter {

    //下拉刷新命令
    @BindingAdapter({"onRefreshCommand"})
    public static void onRefreshCommand(SwipeRefreshLayout swipeRefreshLayout, final BindingCommand onRefreshCommand) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (onRefreshCommand != null) {
                onRefreshCommand.execute();
            }
        });
    }

    //是否刷新中
    @BindingAdapter({"refreshing"})
    public static void setRefreshing(SwipeRefreshLayout swipeRefreshLayout, boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }

}
