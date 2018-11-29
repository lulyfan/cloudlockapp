package com.ut.module_lock.dialog;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.ut.base.BaseDialog;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.DialogAddLockSuccessBinding;

/**
 * author : zhouyubin
 * time   : 2018/11/28
 * desc   :
 * version: 1.0
 */
public class AddSuccessDialog extends BaseDialog {
    private String mLockName = null;
    private DialogAddLockSuccessBinding mBinding;

    public AddSuccessDialog(Context context, boolean isShowTitle, String lockName) {
        super(context, isShowTitle);
        this.mLockName = lockName;
        initView();

    }

    private void initView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_lock_success, null);
        mBinding = DataBindingUtil.bind(view);
        mBinding.btnAddSuccessConfirm.setOnClickListener(mOnClickListener);
        mBinding.iBtnClose.setOnClickListener(mOnClickListener);
        mBinding.tvLockAddName.setText(mLockName);
        setContentView(view);
    }


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };
}
