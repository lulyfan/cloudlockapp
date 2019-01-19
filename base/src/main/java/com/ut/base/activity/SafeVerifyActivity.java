package com.ut.base.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.databinding.ActivitySafeVerifyBinding;
import com.ut.base.viewModel.SafeVerifyVm;

import java.util.Objects;

/**
 * author : chenjiajun
 * time   : 2019/1/10
 * desc   :
 */
@Route(path = RouterUtil.BaseModulePath.SAFEVERIFY)
public class SafeVerifyActivity extends BaseActivity {

    private ActivitySafeVerifyBinding mBinding = null;
    private SafeVerifyVm safeVerifyVm;
    private int timeCount = 60;
    private static final int HANDLE_TIME_COUNT = 1;

    private Handler handler = new Handler(msg -> {
        if (HANDLE_TIME_COUNT == msg.what) {
            countTime();
        }
        return false;
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_safe_verify);
        safeVerifyVm = ViewModelProviders.of(this).get(SafeVerifyVm.class);
        setTitle(getString(R.string.base_safe_verify));
        initUI();
    }

    @SuppressLint("CheckResult")
    private void initUI() {
        initLightToolbar();
        mBinding.edtPhone.setText(getIntent().getStringExtra("phone"));
        mBinding.edtPhone.setEnabled(false);
        mBinding.verifyCodeLayout.setSelected(true);
        mBinding.tvGetVerifyCode.setEnabled(true);
        mBinding.edtVerifyCode.requestFocus();
        mBinding.edtVerifyCode.postDelayed(() -> {
            SystemUtils.showKeyboard(getBaseContext(), mBinding.edtVerifyCode);
        }, 500L);
        RxTextView.afterTextChangeEvents(mBinding.edtVerifyCode).subscribe(textViewAfterTextChangeEvent -> {
            String code = Objects.requireNonNull(textViewAfterTextChangeEvent.getEditable()).toString();
            mBinding.verifyBtn.setEnabled(!TextUtils.isEmpty(code));
        });

        mBinding.verifyBtn.setOnClickListener(v -> safeVerifyVm.verifyCodeAndLogin(mBinding.edtPhone.getPhoneText(), mBinding.edtVerifyCode.getText().toString(), result -> {
            if (result.isSuccess()) {
                finish();
                ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
            }
        }));

        mBinding.tvGetVerifyCode.setOnClickListener(v -> {
            safeVerifyVm.obtainVerifyCode(mBinding.edtPhone.getPhoneText());
            mBinding.tvGetVerifyCode.setEnabled(false);
            countTime();
        });

    }

    private void countTime() {
        if (timeCount == 0) {
            timeCount = 60;
            mBinding.tvGetVerifyCode.setEnabled(true);
            mBinding.tvGetVerifyCode.setText(getString(R.string.get_verify_code));
        } else {
            timeCount--;
            mBinding.tvGetVerifyCode.setText(String.valueOf(getString(R.string.waiting) + "（" + timeCount + "s）"));
            handler.sendEmptyMessageDelayed(HANDLE_TIME_COUNT, 1000L);
        }
    }

}
