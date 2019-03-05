package com.ut.base.databinding;

import android.databinding.BindingAdapter;
import android.os.SystemClock;
import android.view.View;

/**
 * author : zhouyubin
 * time   : 2019/03/04
 * desc   :
 * version: 1.0
 */
public class ViewBindingAdapter {
    @BindingAdapter({"android:onClick", "android:clickable"})
    public static void setOnClick(View view, View.OnClickListener clickListener,
                                  boolean clickable) {
        setOnClick(view, clickListener);
        view.setClickable(clickable);
    }

    @BindingAdapter({"android:onClick"})
    public static void setOnClick(View view, final View.OnClickListener clickListener) {
        final long[] lastClickTime = {0L};
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime[0] >= 500) {
                    clickListener.onClick(v);
                    lastClickTime[0] = currentTime;
                }
            }
        });
    }
}
