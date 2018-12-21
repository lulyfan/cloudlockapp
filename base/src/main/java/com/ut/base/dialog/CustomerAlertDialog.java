package com.ut.base.dialog;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.base.BaseDialog;
import com.ut.base.R;
import com.ut.base.databinding.DialogCustomerAlertBinding;

/**
 * author : zhouyubin
 * time   : 2018/12/18
 * desc   :
 * version: 1.0
 */
public class CustomerAlertDialog extends BaseDialog {
    private DialogCustomerAlertBinding mBinding;

    private View.OnClickListener cancelListener;
    private View.OnClickListener confirmListener;

    public CustomerAlertDialog(Context context, boolean isShowTitle) {
        super(context, isShowTitle);
        initView();
        setCanceledOnTouchOutside(false);
    }

    public CustomerAlertDialog setMsg(String msg) {
        mBinding.setMsg(msg);
        return this;
    }

    public CustomerAlertDialog setCancelText(String cancelText) {
        mBinding.tvAlertCancel.setText(cancelText);
        return this;
    }

    public CustomerAlertDialog setConfirmText(String confirmText) {
        mBinding.tvAlertConfirm.setText(confirmText);
        return this;
    }

    public CustomerAlertDialog setCancelLister(View.OnClickListener lister) {
        this.cancelListener = lister;
        return this;
    }

    public CustomerAlertDialog setConfirmListener(View.OnClickListener listener) {
        this.confirmListener = listener;
        return this;
    }

    public CustomerAlertDialog hideCancel() {
        mBinding.tvAlertCancel.setVisibility(View.GONE);
        mBinding.lineAlert.setVisibility(View.GONE);
        return this;
    }

    public CustomerAlertDialog hideConfirm() {
        mBinding.tvAlertConfirm.setVisibility(View.GONE);
        mBinding.lineAlert.setVisibility(View.GONE);
        return this;
    }

    private void initView() {
        View view = getLayoutInflater().inflate(R.layout.dialog_customer_alert, null);
        mBinding = DataBindingUtil.bind(view);
        mBinding.setPresent(new AlertPrestent());
        setContentView(view);
    }


    public class AlertPrestent {
        public void onClickCancel(View view) {
            if (cancelListener != null) {
                cancelListener.onClick(view);
            }
            dismiss();
        }

        public void onClickConfirm(View view) {
            if (confirmListener != null) {
                confirmListener.onClick(view);
            }
            dismiss();
        }
    }
}
