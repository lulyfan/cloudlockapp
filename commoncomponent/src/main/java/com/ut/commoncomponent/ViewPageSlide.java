package com.ut.commoncomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * author : zhouyubin
 * time   : 2019/01/18
 * desc   :
 * version: 1.0
 */
public class ViewPageSlide extends ViewPager {
    private boolean canSlide = true;

    public ViewPageSlide(@NonNull Context context) {
        super(context);
    }

    public ViewPageSlide(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().getResources().obtainAttributes(attrs, R.styleable.ViewPageSlide);
        canSlide = typedArray.getBoolean(R.styleable.ViewPageSlide_canSlide, true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canSlide) return false;
        return super.onInterceptTouchEvent(ev);
    }
}
