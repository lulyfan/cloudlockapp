package com.ut.commoncomponent;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * author : chenjiajun
 * time   : 2018/11/23
 * desc   : 云锁使用的Toast
 */
public class CLToast {
    private static long DEFAULT_DURATION = 2200L;

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
            contentView.animate().alphaBy(0f).withEndAction(() -> {
                windowManager.removeView(contentView);
            }).setDuration(DEFAULT_DURATION).start();
        }
    }

    public static void showAtBottom(Context context, String message) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.verticalMargin= 0.1f;

        ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
        TextView messageTv = contentView.findViewById(R.id.message);
        messageTv.setText(String.valueOf(message));
        if (windowManager != null) {
            windowManager.addView(contentView, layoutParams);
            contentView.animate().alpha(0f).withEndAction(() -> {
                windowManager.removeView(contentView);
            }).setDuration(DEFAULT_DURATION).start();
        }
    }
}
