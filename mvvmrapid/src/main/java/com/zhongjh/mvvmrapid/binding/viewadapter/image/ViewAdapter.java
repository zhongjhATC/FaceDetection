package com.zhongjh.mvvmrapid.binding.viewadapter.image;


import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


public final class ViewAdapter {

    @BindingAdapter(value = {"url", "placeholderRes"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, int placeholderRes) {
        if (!TextUtils.isEmpty(url))
            // 使用Glide框架加载图片
        {
            Glide.with(imageView.getContext()).load(url)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .centerCrop()
                    .into(imageView);
        }
    }

    @BindingAdapter(value = {"resourceId", "placeholderRes"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, int resourceId, int placeholderRes) {
        if (resourceId != -1)
            // 使用Glide框架加载图片
        {
            Glide.with(imageView.getContext()).load(resourceId)
                    .apply(new RequestOptions().placeholder(placeholderRes))
                    .centerCrop()
                    .into(imageView);
        }
    }

}

