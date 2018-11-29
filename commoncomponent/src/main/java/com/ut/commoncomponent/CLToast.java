package com.ut.commoncomponent;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * author : chenjiajun
 * time   : 2018/11/23
 * desc   : 云锁使用的Toast
 */
public class CLToast {
    private static long DEFAULT_DURATION = 1500L;

    public static void showAtCenter(Context context, String message) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.gravity = Gravity.CENTER;

        ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
        TextView messageTv = contentView.findViewById(R.id.message);
        messageTv.setText(message);

        if (windowManager != null) {
            windowManager.addView(contentView, layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.animate().alphaBy(0f).withEndAction(() -> {
                    windowManager.removeView(contentView);
                }).setDuration(1500L).start();
            } else {
                Animation animation = new AlphaAnimation(1f,0f);
                animation.setDuration(DEFAULT_DURATION);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        windowManager.removeView(contentView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                contentView.startAnimation(animation);
            }
        }
    }

    public static void showAtBottom(Context context, String message) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.gravity = Gravity.BOTTOM;

        ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
        TextView messageTv = contentView.findViewById(R.id.message);
        messageTv.setText(message);

        if (windowManager != null) {
            windowManager.addView(contentView, layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.animate().alpha(0f).withEndAction(() -> {
                    windowManager.removeView(contentView);
                }).setDuration(1500L).start();
            } else {
                Animation animation = new AlphaAnimation(1f,0f);
                animation.setDuration(DEFAULT_DURATION);
                animation.setFillAfter(true);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        windowManager.removeView(contentView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                contentView.startAnimation(animation);
            }
        }
    }
}
