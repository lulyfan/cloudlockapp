package com.ut.module_lock.dialog;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Looper;
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

    public UnlockSuccessDialog(Context context, boolean isShowTitle) {
        super(context, isShowTitle);
        initView();
        setTimout(5000);
    }

    public void setTimout(int timout){
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            if (this.isShowing())
            dismiss();
        },timout);
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
}
