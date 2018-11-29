package com.ut.module_login.ui;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.commoncomponent.CLToast;
import com.ut.commoncomponent.LoadingButton;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * author : chenjiajun
 * time   : 2018/11/15
 * desc   : 登录界面
 */

@Route(path = RouterUtil.LoginModulePath.Login)
public class LoginActivity extends BaseActivity {

    private EditText phoneEdt;
    private EditText passwordEdt;

    private final int CHECK_PHONE_PASSWORD = 1000;

    private Handler mainHandler = new Handler(msg -> {
        if (msg.what == CHECK_PHONE_PASSWORD) {
            boolean result = verifyPhoneAndPassword(phoneEdt.getText().toString(), passwordEdt.getText().toString());
            findViewById(R.id.btn_login).setEnabled(result);
        }
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initLoginUI();
        subscribeEvent();
    }

    private void initLoginUI() {
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
        phoneEdt.setOnFocusChangeListener((view, isFocus) -> {
            ViewGroup parent = (ViewGroup) phoneEdt.getParent();
            parent.setSelected(isFocus);
        });

        findViewById(R.id.img_clear).setOnClickListener((v) -> {
            if (v.isSelected()) {
                phoneEdt.setText("");
            }
        });
        passwordEdt = (EditText) findViewById(R.id.edt_password);
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

        findViewById(R.id.register).setOnClickListener(v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, v, "register");
                        ARouter.getInstance().build(RouterUtil.LoginModulePath.REGISTER).withOptionsCompat(options).navigation(LoginActivity.this);
                    } else {
                        ARouter.getInstance().build(RouterUtil.LoginModulePath.REGISTER).navigation();
                    }
                }
        );
        findViewById(R.id.forget_pwd).setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this, v, "forgetPassword");
                ARouter.getInstance().build(RouterUtil.LoginModulePath.FORGET_PWD).withOptionsCompat(options).navigation(LoginActivity.this);
            } else {
                ARouter.getInstance().build(RouterUtil.LoginModulePath.FORGET_PWD).navigation();
            }
        });

        findViewById(R.id.btn_login).setEnabled(false);
        findViewById(R.id.btn_login).setOnClickListener(v->{
            LoadingButton loadingButton = (LoadingButton) v;
            loadingButton.startLoading();
            onLogin();
            mainHandler.postDelayed(()->loadingButton.endLoading(), 3000L);
        });
    }

    private void onLogin() {
        CLToast.showAtCenter(this, "hahahahahahahha");
    }

    private void subscribeEvent() {
        RxTextView.afterTextChangeEvents(phoneEdt).debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext((textViewAfterTextChangeEvent) -> {
            findViewById(R.id.img_clear).setSelected(!TextUtils.isEmpty(Objects.requireNonNull(textViewAfterTextChangeEvent.getEditable()).toString()));
            if(phoneEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_PASSWORD);
            }
        }).subscribe();

        RxTextView.afterTextChangeEvents(passwordEdt).debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext((textViewAfterTextChangeEvent) -> {
            if(passwordEdt.isFocused()) {
                mainHandler.sendEmptyMessage(CHECK_PHONE_PASSWORD);
            }
        }).subscribe();
    }

    private boolean verifyPhoneAndPassword(String phone, String password) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            return false;
        }

        if (LoginUtil.isPhone(phone) && LoginUtil.isPassword(password)) {
            return true;
        }
        return false;
    }

}
