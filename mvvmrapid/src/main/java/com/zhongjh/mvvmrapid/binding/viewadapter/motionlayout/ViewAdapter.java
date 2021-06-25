package com.zhongjh.mvvmrapid.binding.viewadapter.motionlayout;

import androidx.annotation.IntDef;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.motion.widget.TransitionAdapter;
import androidx.databinding.BindingAdapter;

import com.zhongjh.mvvmrapid.R;
import com.zhongjh.mvvmrapid.binding.command.BindingCommand;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.zhongjh.mvvmrapid.binding.viewadapter.motionlayout.ViewAdapter.TransitionValue.TO_END;
import static com.zhongjh.mvvmrapid.binding.viewadapter.motionlayout.ViewAdapter.TransitionValue.TO_START;

public class ViewAdapter {

    @IntDef({TO_START, TO_END})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TransitionValue {
        int TO_START = 0;
        int TO_END = 1;
    }

    @BindingAdapter({"onTransitionCompleted"})
    public static void setTransitionListener(MotionLayout motionLayout, final BindingCommand<Integer> onTransitionCompleted) {
        motionLayout.setTransitionListener(new TransitionAdapter() {
            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                if (onTransitionCompleted != null) {
                    onTransitionCompleted.execute(currentId);
                }
            }
        });
    }

    /**
     * 设置是往start运动还是往end运动
     *
     * @param motionLayout           view
     * @param transitionToStartOrEnd 值
     */
    @BindingAdapter({"transitionToStartOrEnd"})
    public static void transitionToStartOrEnd(MotionLayout motionLayout, @TransitionValue int transitionToStartOrEnd) {
        if (transitionToStartOrEnd == TO_START) {
            motionLayout.transitionToStart();
        } else {
            motionLayout.transitionToEnd();
        }
    }

    /**
     * 设置是往什么状态执行
     *
     * @param motionLayout view
     * @param stateId      值
     */
    @BindingAdapter({"stateId"})
    public static void transitionToState(MotionLayout motionLayout, int stateId) {
        motionLayout.transitionToState(stateId);
    }

}
