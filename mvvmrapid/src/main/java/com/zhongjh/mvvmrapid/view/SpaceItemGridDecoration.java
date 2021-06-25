package com.zhongjh.mvvmrapid.view;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * 一款九宫格的间距
 * Created by zhongjh on 2021/4/20.
 */
public class SpaceItemGridDecoration extends RecyclerView.ItemDecoration {

    private final int space;
    private final int spanCount;

    /**
     *
     * @param space 间距
     * @param spanCount 一行几个格子
     */
    public SpaceItemGridDecoration(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
        // 不是第一个的格子都设一个左边和底部的间距
        outRect.left = space;
        outRect.bottom = space;
        // 由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) % spanCount == 0) {
            outRect.left = 0;
        }
    }


}