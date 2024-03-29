package com.ut.module_login.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.entity.base.Result;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.commoncomponent.LoadingButton;
import com.ut.commoncomponent.ZpPhoneEditText;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;
import com.ut.module_login.viewmodel.LoginVm;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

@Route(path = RouterUtil.LoginModulePath.FORGET_PWD)
public class ForgetPasswordActivity extends BaseActivity {

    private static final int CHECK_PHONE_AND_PASSWORD = 1000;
    private static final int RECIPROCAL = 1001;
    private ZpPhoneEditText phoneEdt = null;
    private TextView getVerifyCodeTv = null;
    private EditText passwordEdt = null;
    private EditText verifyCodeEdt = null;
    private LoadingButton sureBtn = null;

    private Handler mainHandler = new Handler((this::handleMessage));
    private static final int DEFAULT_TIME_COUNT = 60;
    private int timeCount = DEFAULT_TIME_COUNT;
    private boolean isReciprocal = false;

    private LoginVm loginVm;

    private String mAction = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initUI();
        loginVm = ViewModelProviders.of(this).get(LoginVm.class);
    }

    @Override
    protected void initNoLoginListener() {
    }

    private void initUI() {
        initLightToolbar();
        phoneEdt = findViewById(R.id.edt_phone);
        passwordEdt = findViewById(R.id.edt_password);
        getVerifyCodeTv = findViewById(R.id.tv_get_verify_code);
        getVerifyCodeTv.setEnabled(false);
        verifyCodeEdt = findViewById(R.id.edt_verify_code);
        mAction = getIntent().getAction();

        passwordEdt.setOnFocusChangeListener((v, hasFocus) -> {
            ViewGroup parent = (ViewGroup) passwordEdt.getParent();
            parent.setSelected(hasFocus);
        });

        if (RouterUtil.LoginModuleAction.action_login_resetPW.equals(mAction)) {
            setTitle(getString(R.string.reset_password));
            phoneEdt.setText(getIntent().getStringExtra("phone"));
            phoneEdt.setSelected(false);
            phoneEdt.setEnabled(false);
            phoneEdt.clearFocus();
            findViewById(R.id.phone_layout).setSelected(false);
            findViewById(R.id.phone_layout).setEnabled(false);
            findViewById(R.id.img_clear).setEnabled(false);
            findViewById(R.id.img_clear).setVisibility(View.INVISIBLE);
            passwordEdt.requestFocus();
        } else {
            setTitle(R.string.login_forget_password);
            phoneEdt.setOnFocusChangeListener((v, hasFocus) -> {
                ViewGroup parent = (ViewGroup) phoneEdt.getParent();
                parent.setSelected(hasFocus);
            });
        }

        findViewById(R.id.img_clear).setOnClickListener((v) -> {
            if (v.isSelected()) {
                phoneEdt.setText("");
            }
        });

        RxTextView.afterTextChangeEvents(phoneEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            if (phoneEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
            }
        }).subscribe();

        RxTextView.afterTextChangeEvents(passwordEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            if (passwordEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
            }

        }).subscribe();

        findViewById(R.id.see_password).setOnClickListener(v -> {
            if (v instanceof CheckBox) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    passwordEdt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwordEdt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD | InputType.TYPE_CLASS_TEXT);
                }
                passwordEdt.setSelection(passwordEdt.getText().length());
            }
        });

        findViewById(R.id.see_password_layout).setOnClickListener(v -> {
            findViewById(R.id.see_password).performClick();
        });

        getVerifyCodeTv.setOnClickListener(v -> {
            ((ViewGroup) getVerifyCodeTv.getParent()).setSelected(true);
            getVerifyCode(phoneEdt.getPhoneText(), result -> {
                CLToast.showAtCenter(getApplication(), result.msg);
                mainHandler.sendEmptyMessage(RECIPROCAL);
            });
        });

        RxTextView.afterTextChangeEvents(verifyCodeEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            if (verifyCodeEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
            }
        }).subscribe();

        sureBtn = findViewById(R.id.sure);
        sureBtn.setEnabled(false);
        sureBtn.setOnClickListener(v -> commit());

        findViewById(R.id.root).setOnClickListener(v -> SystemUtils.hideKeyboard(getBaseContext(), v));

        mainHandler.postDelayed(() -> {
            SystemUtils.showKeyboard(this, phoneEdt.isEnabled() ? phoneEdt : passwordEdt);
        }, 500L);
    }

    private boolean handleMessage(Message msg) {
        if (msg.what == CHECK_PHONE_AND_PASSWORD) {
            boolean verifyResult = LoginUtil.isPhone(phoneEdt.getPhoneText()) && LoginUtil.isPassword(passwordEdt.getText().toString());
            sureBtn.setEnabled(verifyResult && verifyCodeEdt.getText().length() > 0);
            getVerifyCodeTv.setEnabled(!isReciprocal && verifyResult);
            ((ViewGroup) phoneEdt.getParent()).setBackgroundResource(loginVm.checkPhoneBg(phoneEdt.getPhoneText()));
            ((ViewGroup) passwordEdt.getParent()).setBackgroundResource(loginVm.checkPwdBg(passwordEdt.getText().toString()));
        } else if (RECIPROCAL == msg.what) {
            timeCount--;
            isReciprocal = timeCount > 0;
            if (isReciprocal) {
                getVerifyCodeTv.setEnabled(false);
                getVerifyCodeTv.setText(String.valueOf(getString(R.string.wait) + "（" + timeCount + "s）"));
                mainHandler.sendEmptyMessageDelayed(RECIPROCAL, 1000L);
            } else {
                getVerifyCodeTv.setEnabled(true);
                getVerifyCodeTv.setText(getText(R.string.get_verify_code));
                timeCount = DEFAULT_TIME_COUNT;
            }
        }
        return false;
    }

    private void getVerifyCode(String phone, Consumer<Result<Void>> subscriber) {
        if (LoginUtil.isPhone(phone)) {
            loginVm.getForgetPwdCode(phone, subscriber);
        } else {
            CLToast.showAtCenter(this, getString(R.string.login_please_input_right_num));
        }
    }


    private void commit() {
        String phone = phoneEdt.getPhoneText();
        String password = passwordEdt.getText().toString();
        String verifyCode = verifyCodeEdt.getText().toString();
        if (TextUtils.isEmpty(verifyCode)) {
            CLToast.showAtCenter(this, getString(R.string.login_place_input_verify_code));
            return;
        }
        sureBtn.startLoading();
        loginVm.resetPassword(phone, password, verifyCode,
                RouterUtil.LoginModuleAction.action_login_resetPW.equals(mAction));
        mainHandler.postDelayed(() -> sureBtn.endLoading(), 500L);
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), findViewById(R.id.root));
    }
}
