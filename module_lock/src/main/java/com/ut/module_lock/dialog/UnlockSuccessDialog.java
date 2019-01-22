package com.ut.module_lock.dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.ut.base.BaseDialog;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.DialogAddLockSuccessBinding;
import com.ut.module_lock.databinding.DialogUnlockSuccessBinding;


/**
 * author : zhouyubin
 * time   : 2018/11/28
 * desc   :
 * version: 1.0
 */
public class UnlockSuccessDialog extends BaseDialog {
    private DialogUnlockSuccessBinding mBinding;
    private Handler mHandler = null;

    public UnlockSuccessDialog(Context context, boolean isShowTitle) {
        super(context, isShowTitle);
        initView();
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (UnlockSuccessDialog.this.isShowing())
                    dismiss();
            }
        };
        setTimout(3000);
        setOnDismissListener(dialog -> mHandler.removeMessages(0));
    }

    public void setTimout(int timout) {
        mHandler.sendEmptyMessageDelayed(0, timout);
    }

    private void initView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_unlock_success, null);
        mBinding = DataBindingUtil.bind(view);
        mBinding.iBtnClose.setOnClickListener(mOnClickListener);
        setContentView(view);
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(0);
    }
}
