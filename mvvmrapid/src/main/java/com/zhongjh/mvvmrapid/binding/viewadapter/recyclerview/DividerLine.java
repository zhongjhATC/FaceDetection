package com.zhongjh.mvvmrapid.binding.viewadapter.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class DividerLine extends RecyclerView.ItemDecoration {

    // 默认分隔线厚度为2dp
    private static final int DEFAULT_DIVIDER_SIZE = 1;
    // 控制分隔线的属性,值为一个drawable
    private static final int[] ATTRS = {android.R.attr.listDivider};
    // divider对应的drawable
    private final Drawable dividerDrawable;
    private final Context mContext;
    private int dividerSize;
    // 默认为null
    private LineDrawMode mMode = null;

    /**
     * 分隔线绘制模式,水平，垂直，两者都绘制
     */
    public enum LineDrawMode {
        HORIZONTAL, VERTICAL, BOTH
    }

    public DividerLine(Context context) {
        mContext = context;
        //获取样式中对应的属性值
        TypedArray attrArray = context.obtainStyledAttributes(ATTRS);
        dividerDrawable = attrArray.getDrawable(0);
        attrArray.recycle();
    }

    public DividerLine(Context context, LineDrawMode mode) {
        this(context);
        mMode = mode;
    }

    public DividerLine(Context context, int dividerSize, LineDrawMode mode) {
        this(context, mode);
        this.dividerSize = dividerSize;
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
    }

    public LineDrawMode getMode() {
        return mMode;
    }

    public void setMode(LineDrawMode mode) {
        mMode = mode;
    }

    /**
     * Item绘制完毕之后绘制分隔线
     * 根据不同的模式绘制不同的分隔线
     */
    @Override
    public void onDrawOver(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (getMode() == null) {
            throw new IllegalStateException("assign LineDrawMode,please!");
        }
        switch (getMode()) {
            case VERTICAL:
                drawVertical(c, parent);
                break;
            case HORIZONTAL:
                drawHorizontal(c, parent);
                break;
            case BOTH:
                drawHorizontal(c, parent);
                drawVertical(c, parent);
                break;
        }
    }

    /**
     * 绘制垂直分隔线
     */
    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = getDividerSize() == 0 ? left + dip2px(mContext, DEFAULT_DIVIDER_SIZE) : left + getDividerSize();
            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(c);
        }
    }

    /**
     * 绘制水平分隔线
     */
    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //分别为每个item绘制分隔线,首先要计算出item的边缘在哪里,给分隔线定位,定界
            final View child = parent.getChildAt(i);
            //RecyclerView的LayoutManager继承自ViewGroup,支持了margin
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //child的左边缘(也是分隔线的左边)
            final int left = child.getLeft() - params.leftMargin;
            //child的底边缘(恰好是分隔线的顶边)
            final int top = child.getBottom() + params.topMargin;
            //child的右边(也是分隔线的右边)
            final int right = child.getRight() - params.rightMargin;
            //分隔线的底边所在的位置(那就是分隔线的顶边加上分隔线的高度)
            final int bottom = getDividerSize() == 0 ? top + dip2px(mContext, DEFAULT_DIVIDER_SIZE) : top + getDividerSize();
            dividerDrawable.setBounds(left, top, right, bottom);
            //画上去
            dividerDrawable.draw(c);
        }
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
