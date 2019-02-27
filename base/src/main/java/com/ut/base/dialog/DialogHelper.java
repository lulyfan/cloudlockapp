package com.ut.base.dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.ut.base.AppManager;
import com.ut.base.R;

import java.util.Objects;

/**
 * author : chenjiajun
 * time   : 2019/1/26
 * desc   :
 */
public class DialogHelper {
    private AlertDialog.Builder sBuilder = null;
    private AlertDialog alertDialog;

    private DialogHelper() {
    }

    private static DialogHelper instance;

    public static DialogHelper getInstance() {
        if (instance == null) {
            synchronized (DialogHelper.class) {
                instance = new DialogHelper();
            }
        }

        if (!instance.isShowing()) {
            instance.sBuilder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
        }

        return instance;
    }


    public DialogHelper newDialog() {
        if (instance.isShowing()) {
            instance.alertDialog.dismiss();
        }
        instance.sBuilder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
        return instance;
    }


    public DialogHelper setTitle(String title) {
        if (!isShowing()) {
            sBuilder.setTitle(title);
        }
        return instance;
    }

    public DialogHelper setMessage(String message) {
        if (!isShowing()) {
            sBuilder.setMessage(message);
        }
        return instance;
    }

    public DialogHelper setContentView(View contentView) {
        if (!isShowing()) {
            sBuilder.setView(contentView);
        }
        return instance;
    }

    public DialogHelper setPositiveButton(String positiveBtnText, DialogInterface.OnClickListener clickListener) {
        if (!isShowing()) {
            sBuilder.setPositiveButton(positiveBtnText, clickListener);
        }
        return instance;
    }

    public DialogHelper setNegativeButton(String positiveBtnText, DialogInterface.OnClickListener clickListener) {
        if (!isShowing()) {
            sBuilder.setNegativeButton(positiveBtnText, clickListener);
        }
        return instance;
    }

    private boolean isShowing() {
        return instance.alertDialog != null && instance.alertDialog.isShowing();
    }

    public DialogHelper setCanCancelOutSide(boolean can) {
        if (!isShowing()) {
            sBuilder.setCancelable(can);
        }
        return instance;
    }

    public void show() {
        if (instance.isShowing()) {
            return;
        }
        instance.sBuilder.setOnDismissListener(dialog -> {
            instance.sBuilder = null;
        });
        instance.alertDialog = instance.sBuilder.create();
        instance.alertDialog.show();
        Objects.requireNonNull(instance.alertDialog.getWindow()).setBackgroundDrawable(instance.alertDialog.getContext().getResources().getDrawable(R.drawable.shape_bg_corner));
    }

}
