package com.ut.module_login.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.AppManager;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.LoadingButton;
import com.ut.commoncomponent.ZpPhoneEditText;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;
import com.ut.module_login.viewmodel.LoginVm;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * author : chenjiajun
 * time   : 2018/11/15
 * desc   : 登录界面
 */

@SuppressLint("CheckResult")
@Route(path = RouterUtil.LoginModulePath.Login)
public class LoginActivity extends BaseActivity {
    private ZpPhoneEditText phoneEdt;
    private EditText passwordEdt;
    private LoginVm loginVm;

    private final int CHECK_PHONE_PASSWORD = 1000;
    private Handler mainHandler = new Handler(msg -> {
        if (msg.what == CHECK_PHONE_PASSWORD) {
            boolean result = verifyPhoneAndPassword(phoneEdt.getPhoneText(), passwordEdt.getText().toString());
            findViewById(R.id.btn_login).setEnabled(result);
            if (!TextUtils.isEmpty(phoneEdt.getPhoneText())) {
                ((ViewGroup) phoneEdt.getParent()).setBackgroundResource(loginVm.checkPhoneBg(phoneEdt.getPhoneText()));
            }

            if (!TextUtils.isEmpty(passwordEdt.getText().toString())) {
                ((ViewGroup) passwordEdt.getParent()).setBackgroundResource(loginVm.checkPwdBg(passwordEdt.getText().toString()));
            }
        }
        return false;
    });

    @Override
    protected void initNoLoginListener() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppManager.getAppManager().finishAllActivity();//TODO 暂时退到登录界面时清除所有界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLoginUI();
        subscribeEvent();
        loginVm = ViewModelProviders.of(this).get(LoginVm.class);
    }

    private void initLoginUI() {
        setTitle(R.string.login_title);
        initLightToolbar();
        hideNavigationIcon();
        phoneEdt = findViewById(R.id.edt_phone);
        phoneEdt.setOnFocusChangeListener((view, isFocus) -> {
            ViewGroup parent = (ViewGroup) phoneEdt.getParent();
            parent.setSelected(isFocus);
        });

        phoneEdt.requestFocus();

        findViewById(R.id.img_clear).setOnClickListener((v) -> {
            if (v.isSelected()) {
                phoneEdt.setText("");
            }
        });
        passwordEdt = findViewById(R.id.edt_password);
        passwordEdt.setOnFocusChangeListener((view, isFocus) -> {
            ViewGroup parent = (ViewGroup) passwordEdt.getParent();
            parent.setSelected(isFocus);
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

        findViewById(R.id.register).setOnClickListener(v -> {
                    ARouter.getInstance().build(RouterUtil.LoginModulePath.REGISTER).navigation();
                }
        );
        findViewById(R.id.forget_pwd).setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LoginModulePath.FORGET_PWD).navigation();
        });

        findViewById(R.id.btn_login).setEnabled(false);
        findViewById(R.id.btn_login).setOnClickListener(v -> {
            LoadingButton loadingButton = (LoadingButton) v;
            loadingButton.startLoading();
            onLogin();
            mainHandler.postDelayed(() -> loadingButton.endLoading(), 3000L);
        });

        findViewById(R.id.root).setOnClickListener(v -> {
            SystemUtils.hideKeyboard(getBaseContext(), v);
        });

        mainHandler.postDelayed(() -> {
            SystemUtils.showKeyboard(this, phoneEdt);
        }, 500L);
    }

    private void subscribeEvent() {
        RxTextView.afterTextChangeEvents(phoneEdt).debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            findViewById(R.id.img_clear).setSelected(!TextUtils.isEmpty(Objects.requireNonNull(event.getEditable()).toString()));
            if (phoneEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_PASSWORD);
            }
        }).subscribe();

        RxTextView.afterTextChangeEvents(passwordEdt).debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext((textViewAfterTextChangeEvent) -> {
            if (passwordEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_PASSWORD);
            }
        }).subscribe();
    }

    private boolean verifyPhoneAndPassword(String phone, String password) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            return false;
        }

        return LoginUtil.isPhone(phone) && LoginUtil.isPassword(password);
    }

    private void onLogin() {
        SystemUtils.hideKeyboard(this, phoneEdt);
        String phone = phoneEdt.getPhoneText();
        String password = passwordEdt.getText().toString().trim();
        loginVm.login(phone, password);
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }

}
