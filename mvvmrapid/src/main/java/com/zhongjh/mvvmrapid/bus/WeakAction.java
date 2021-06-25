package com.zhongjh.mvvmrapid.bus;

import com.zhongjh.mvvmrapid.binding.command.BindingAction;
import com.zhongjh.mvvmrapid.binding.command.BindingConsumer;

import java.lang.ref.WeakReference;


/**
 * About : kelin的WeakBindingAction
 */
public class WeakAction<T> {
    private BindingAction action;
    private BindingConsumer<T> consumer;
    private WeakReference<Object> reference;

    public WeakAction(Object target, BindingAction action) {
        reference = new WeakReference<>(target);
        this.action = action;

    }

    public WeakAction(Object target, BindingConsumer<T> consumer) {
        reference = new WeakReference<>(target);
        this.consumer = consumer;
    }

    public void execute() {
        if (action != null && isLive()) {
            action.call();
        }
    }

    public void execute(T parameter) {
        if (consumer != null
                && isLive()) {
            consumer.call(parameter);
        }
    }

    public void markForDeletion() {
        reference.clear();
        reference = null;
        action = null;
        consumer = null;
    }

    public BindingAction getBindingAction() {
        return action;
    }

    public BindingConsumer<T> getBindingConsumer() {
        return consumer;
    }

    public boolean isLive() {
        if (reference == null) {
            return false;
        }
        return reference.get() != null;
    }


    public Object getTarget() {
        if (reference != null) {
            return reference.get();
        }
        return null;
    }
}
