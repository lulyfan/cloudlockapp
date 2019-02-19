package com.ut.module_login.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.alibaba.android.arouter.launcher.ARouter;
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

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

@Route(path = RouterUtil.LoginModulePath.REGISTER)
public class RegisterActivity extends BaseActivity {

    private static final int CHECK_PHONE_AND_PASSWORD = 1000;
    private static final int RECIPROCAL = 1001;
    private static final int REQ_COUNTRY_AREA_CODE = 1002;
    private ZpPhoneEditText phoneEdt = null;
    private TextView getVerifyCodeTv = null;
    private EditText passwordEdt = null;
    private EditText verifyCodeEdt = null;
    private LoadingButton registerBtn = null;

    private Handler mainHandler = new Handler((this::handleMessage));
    private static final int DEFAULT_TIME_COUNT = 60;
    private int timeCount = DEFAULT_TIME_COUNT;
    private boolean isReciprocal = false;
    private String countryAreaCode = null;

    private LoginVm loginVm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
        loginVm = ViewModelProviders.of(this).get(LoginVm.class);
    }

    @Override
    protected void initNoLoginListener() {
    }

    private void initUI() {
        initLightToolbar();
        setTitle(R.string.register);
        countryAreaCode = getText(R.string.default_country_code).toString();
        phoneEdt = findViewById(R.id.edt_phone);
        verifyCodeEdt = findViewById(R.id.edt_verify_code);
        getVerifyCodeTv = findViewById(R.id.tv_get_verify_code);
        getVerifyCodeTv.setEnabled(false);
        RxTextView.afterTextChangeEvents(phoneEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            if (phoneEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
            }
        }).subscribe();
        phoneEdt.setOnFocusChangeListener((v, hasFocus) -> {
            ViewGroup parent = (ViewGroup) phoneEdt.getParent();
            parent.setSelected(hasFocus);
        });
        findViewById(R.id.img_clear).setOnClickListener((v) -> {
            if (v.isSelected()) {
                phoneEdt.setText("");
            }
        });
        phoneEdt.requestFocus();
        passwordEdt = findViewById(R.id.edt_password);
        RxTextView.afterTextChangeEvents(passwordEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            if (passwordEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
            }
        }).subscribe();
        passwordEdt.setOnFocusChangeListener((v, hasFocus) -> {
            ViewGroup parent = (ViewGroup) passwordEdt.getParent();
            parent.setSelected(hasFocus);
        });
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

        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(v -> {
            String phone = phoneEdt.getPhoneText();
            String password = passwordEdt.getText().toString().trim();
            String verifyCode = verifyCodeEdt.getText().toString().trim();
            register(phone, password, verifyCode);
        });

//        findViewById(R.id.tel_belong_of_place).setOnClickListener(v -> {
//                    ViewGroup parent = (ViewGroup) v.getParent();
//                    parent.requestFocus();
//                    ARouter.getInstance()
//                            .build(RouterUtil
//                                    .LoginModulePath.SELECT_COUNTRY_AREA_CODE)
//                            .navigation(RegisterActivity.this, REQ_COUNTRY_AREA_CODE);
//                }
//        );

//        findViewById(R.id.location_layout).setOnFocusChangeListener(View::setSelected);

        findViewById(R.id.root).setOnClickListener(v -> {
            SystemUtils.hideKeyboard(getBaseContext(), v);
        });

        TextView registerRuleTv = findViewById(R.id.tv_register_rule);
        registerRuleTv.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mainHandler.postDelayed(() -> {
            SystemUtils.showKeyboard(this, phoneEdt);
        }, 500L);
        registerRuleTv.setOnClickListener(v ->
                ARouter.getInstance().build(RouterUtil.BaseModulePath.WEB)
                        .withString("load_url", "https://smarthome.zhunilink.com/realtimeadmin/api/buss/executeBackString?scriptName=cloudlockprivate")
                        .navigation());
    }

    private boolean handleMessage(Message msg) {
        if (msg.what == CHECK_PHONE_AND_PASSWORD) {
            boolean verifyResult = LoginUtil.isPhone(phoneEdt.getPhoneText()) && LoginUtil.isPassword(passwordEdt.getText().toString());
            registerBtn.setEnabled(verifyResult && verifyCodeEdt.getText().length() > 0);
            getVerifyCodeTv.setEnabled(!isReciprocal && verifyResult);
            if (!TextUtils.isEmpty(phoneEdt.getPhoneText())) {
                ((ViewGroup) phoneEdt.getParent()).setBackgroundResource(loginVm.checkPhoneBg(phoneEdt.getPhoneText()));
            }

            if (!TextUtils.isEmpty(passwordEdt.getText().toString())) {
                ((ViewGroup) passwordEdt.getParent()).setBackgroundResource(loginVm.checkPwdBg(passwordEdt.getText().toString()));
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_COUNTRY_AREA_CODE:
                    countryAreaCode = data.getStringExtra("code");
                    TextView telEdt = findViewById(R.id.tel_belong_of_place);
                    telEdt.setText(countryAreaCode);
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void getVerifyCode(String phone, Consumer<Result<Void>> subscriber) {
        if (LoginUtil.isPhone(phone)) {
            loginVm.getVerifyCode(phone, subscriber);
        } else {
            CLToast.showAtCenter(this, getString(R.string.login_please_input_right_num));
        }
    }

    private void register(String phone, String password, String verifyCode) {
        if (TextUtils.isEmpty(verifyCode)) {
            CLToast.showAtCenter(this, getString(R.string.login_place_input_verify_code));
            return;
        }
        loginVm.register(phone, password, verifyCode);
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }
}
