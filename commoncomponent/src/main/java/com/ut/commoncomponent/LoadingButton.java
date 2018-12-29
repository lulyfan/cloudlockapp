package com.ut.commoncomponent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * author : chenjiajun
 * time   : 2018/11/23
 * desc   :
 */
public class LoadingButton extends FrameLayout {

    private ImageView mLoadingImageView = null;

    public LoadingButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LoadingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LoadingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0);
        String mLabel = a.getString(R.styleable.LoadingButton_button_label);
        int selector = a.getResourceId(R.styleable.LoadingButton_button_selector, R.drawable.selector_loading_button);
        int labelColor = a.getColor(R.styleable.LoadingButton_button_label_color, Color.WHITE);
        float labelTextSize = a.getDimensionPixelSize(R.styleable.LoadingButton_button_label_text_size, (int) getResources().getDimension(R.dimen.loading_button_default_text_size));

        setClickable(true);
        setBackgroundDrawable(context.getResources().getDrawable(selector));

        //add loading view
        int size = dip2px(context, 16);
        LinearLayout.LayoutParams linearLp = new LinearLayout.LayoutParams(size, size);
        linearLp.gravity = Gravity.CENTER;
        linearLp.rightMargin = dip2px(context, 10);
        mLoadingImageView = new ImageView(context);
        mLoadingImageView.setImageResource(R.mipmap.loading_icon);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.addView(mLoadingImageView, linearLp);
        mLoadingImageView.setVisibility(GONE);

        //add label
        linearLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(context);
        textView.setText(TextUtils.isEmpty(mLabel) ? "" : mLabel);
        textView.setTextColor(labelColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, labelTextSize);
        linearLayout.addView(textView, linearLp);

        FrameLayout.LayoutParams frameLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        frameLp.gravity = Gravity.CENTER;
        addView(linearLayout, frameLp);
    }

    public void startLoading() {
        mLoadingImageView.setVisibility(VISIBLE);
        Animation rotateAnimation = new RotateAnimation(0, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(1200L);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        mLoadingImageView.startAnimation(rotateAnimation);
        setEnabled(false);
    }

    public void endLoading() {
        mLoadingImageView.setVisibility(GONE);
        if (mLoadingImageView.getAnimation() != null) {
            mLoadingImageView.getAnimation().cancel();
            mLoadingImageView.clearAnimation();
        }
        setEnabled(true);
    }

    /**
     * 将dp转换成px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将像素转换成dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
