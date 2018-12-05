package com.ut.module_msg.databindingadapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class ImageViewAttrAdapter {

    @BindingAdapter({"url"})
    public static void loadImg(ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .asBitmap()
                    .centerCrop()
                    .into(imageView);
        }
    }
}
