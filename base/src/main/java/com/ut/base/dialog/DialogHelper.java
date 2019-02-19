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
        if(instance == null) {
            synchronized (DialogHelper.class) {
                instance = new DialogHelper();
            }
        }
        if (instance.isShowing()) {
            instance.alertDialog.dismiss();
            instance.alertDialog = null;
        }
        instance.sBuilder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
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

    public DialogHelper setCanCancelOutSide(boolean can) {
        sBuilder.setCancelable(can);
        return instance;
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
        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(alertDialog.getContext().getResources().getDrawable(R.drawable.shape_bg_corner));
    }

}
