package com.ut.commoncomponent;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * author : chenjiajun
 * time   : 2018/11/23
 * desc   : 云锁使用的Toast
 */
public class CLToast {
    private static Toast sToast;
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void showAtCenter(Context context, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if("还未登录".equals(message) || "未携带appid".equals(message)) return;
        if (sToast != null) {
            sToast.cancel();
        }
        handler.post(() -> {
            sToast = new Toast(context);
            ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
            TextView messageTv = contentView.findViewById(R.id.message);
            messageTv.setText(message);
            sToast.setGravity(Gravity.CENTER, 0, 0);
            sToast.setView(contentView);
            sToast.show();
        });
    }

    public static void showAtBottom(Context context, String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if("还未登录".equals(message) || "未携带appid".equals(message)) return;
        if (sToast != null) {
            sToast.cancel();
        }
        handler.post(() -> {
            sToast = new Toast(context);
            ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
            TextView messageTv = contentView.findViewById(R.id.message);
            messageTv.setText(message);
            sToast.setView(contentView);
            sToast.show();
        });
    }
}
