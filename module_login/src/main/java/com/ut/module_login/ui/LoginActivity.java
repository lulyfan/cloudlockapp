package com.ut.module_login.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.commoncomponent.LoadingButton;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * author : chenjiajun
 * time   : 2018/11/15
 * desc   : 登录界面
 */

@SuppressLint("CheckResult")
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
        setTitle(R.string.login_title);
        initLightToolbar();
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
    }

    private void subscribeEvent() {
        RxTextView.afterTextChangeEvents(phoneEdt).debounce(300, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).doOnNext((textViewAfterTextChangeEvent) -> {
            findViewById(R.id.img_clear).setSelected(!TextUtils.isEmpty(Objects.requireNonNull(textViewAfterTextChangeEvent.getEditable()).toString()));
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

        if (LoginUtil.isPhone(phone) && LoginUtil.isPassword(password)) {
            return true;
        }
        return false;
    }

    private void onLogin() {
        SystemUtils.hideKeyboard(this, phoneEdt);
        String phone = phoneEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();
        MyRetrofit.get()
                .getCommonApiService()
                .login(phone, password)
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        CloudLockDatabaseHolder.get().getUserDao().deleteAllUsers();
                        CloudLockDatabaseHolder.get().getUserDao().insertUser(result.data);
                        ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                        finish();
                    } else {
                        mainHandler.post(() -> {
                            CLToast.showAtCenter(LoginActivity.this, result.msg);
                        });
                    }
                }, new ErrorHandler());
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }
}
