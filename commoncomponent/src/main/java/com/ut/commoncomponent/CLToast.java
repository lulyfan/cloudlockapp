package com.ut.commoncomponent;

import android.content.Context;
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
    public static void showAtCenter(Context context, String message) {
        Toast toast = new Toast(context);
        ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
        TextView messageTv = contentView.findViewById(R.id.message);
        messageTv.setText(message);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setView(contentView);
        toast.show();
    }

    public static void showAtBottom(Context context, String message) {
        Toast toast = new Toast(context);
        ViewGroup contentView = (ViewGroup) View.inflate(context, R.layout.toast_view, null);
        TextView messageTv = contentView.findViewById(R.id.message);
        messageTv.setText(message);
        toast.setView(contentView);
        toast.show();
    }
}
