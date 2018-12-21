package com.ut.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.view.Window;
import android.view.WindowManager;

/**
 * author : zhouyubin
 * time   : 2018/11/28
 * desc   :
 * version: 1.0
 */
public class BaseDialog extends AppCompatDialog {
    private static BaseDialog currentDialog;
    private Context mContext;

    public BaseDialog(Context context, boolean isShowTitle) {
        super(context);
        mContext = context;
        if (!isShowTitle) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void show() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        if (mContext instanceof Activity) {
            if (!((Activity) mContext).isFinishing()) {
                super.show();
            }
        } else {
            super.show();
        }
        currentDialog = this;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        currentDialog = null;
    }

    @Override
    public void cancel() {
        super.cancel();
        currentDialog = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        currentDialog = null;
    }
}
