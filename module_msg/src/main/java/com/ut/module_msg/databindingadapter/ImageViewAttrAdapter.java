package com.ut.module_msg.databindingadapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ut.module_msg.R;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class ImageViewAttrAdapter {

    @BindingAdapter(value = {"url", "circle"}, requireAll = false)
    public static void loadImg(ImageView imageView, String url, boolean isCircle) {
        RequestOptions requestOptions = isCircle ? RequestOptions.circleCropTransform() : new RequestOptions();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(requestOptions.error(R.mipmap.default_icon_b).placeholder(R.mipmap.default_icon_b))
                    .into(imageView);
        } else {
            Glide.with(imageView.getContext())
                    .load(R.mipmap.default_icon_b)
                    .apply(requestOptions.error(R.mipmap.default_icon_b).placeholder(R.mipmap.default_icon_b))
                    .into(imageView);
        }
    }
}
