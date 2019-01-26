package com.ut.base.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * author : chenjiajun
 * time   : 2019/1/26
 * desc   :
 */
public class DialogHelper {
    private static AlertDialog.Builder sBuilder = null;
    private AlertDialog alertDialog;

    private DialogHelper() {
    }

    private static DialogHelper instance;

    public static DialogHelper getInstance(Context context) {
        synchronized (DialogHelper.class) {
            if (instance == null) {
                instance = new DialogHelper();
            }

            if (instance.isShowing()) {
                instance.alertDialog.dismiss();
                instance.alertDialog = null;
            }

            if (sBuilder == null) {
                sBuilder = new AlertDialog.Builder(context);
            }
        }
        return instance;
    }

    public DialogHelper setTitle(String title) {
        sBuilder.setTitle(title);
        return instance;
    }

    public DialogHelper setMessage(String message) {
        sBuilder.setMessage(message);
        return instance;
    }

    public DialogHelper setContentView(View contentView) {
        sBuilder.setView(contentView);
        return instance;
    }

    public DialogHelper setPositiveButton(String positiveBtnText, DialogInterface.OnClickListener clickListener) {
        sBuilder.setPositiveButton(positiveBtnText, clickListener);
        return instance;
    }

    public DialogHelper setNegativeButton(String positiveBtnText, DialogInterface.OnClickListener clickListener) {
        sBuilder.setNegativeButton(positiveBtnText, clickListener);
        return instance;
    }

    public boolean isShowing() {
        return sBuilder != null && alertDialog != null && alertDialog.isShowing();
    }

    public void show() {
        if (isShowing()) {
            return;
        }

        sBuilder.setOnDismissListener(dialog -> {
            sBuilder = null;
        });
        alertDialog = sBuilder.create();
        alertDialog.show();
    }

}
