package com.ut.module_mine.adapter;

import android.app.Activity;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ImageViewAdapter {

    @BindingAdapter(value={"imageUrl", "isCircle"}, requireAll=false)
    public static void setImageUrl(ImageView imageView, String url, boolean isCircle) {
        if (url == null) {
            return;
        }

        RequestOptions options = new RequestOptions();
        if (isCircle) {
            options.circleCrop();
        }

        Glide.with((Activity) imageView
                .getContext())
                .load(url)
                .apply(options)
                .into(imageView);
    }
}
