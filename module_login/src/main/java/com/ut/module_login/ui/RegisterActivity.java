package com.ut.module_login.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.commoncomponent.LoadingButton;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
@Route(path = RouterUtil.LoginModulePath.REGISTER)
public class RegisterActivity extends BaseActivity {

    private static final int CHECK_PHONE_AND_PASSWORD = 1000;
    private static final int RECIPROCAL = 1001;
    private static final int REQ_COUNTRY_AREA_CODE = 1002;
    private EditText phoneEdt = null;
    private TextView getVerifyCodeTv = null;
    private EditText passwordEdt = null;
    private EditText verifyCodeEdt = null;
    private LoadingButton registerBtn = null;

    private Handler mainHandler = new Handler((this::handleMessage));
    private static final int DEFAULT_TIME_COUNT = 60;
    private int timeCount = DEFAULT_TIME_COUNT;
    private boolean isReciprocal = false;
    private String countryAreaCode = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(R.string.register);
        countryAreaCode = getText(R.string.default_country_code).toString();
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
        verifyCodeEdt = findViewById(R.id.edt_verify_code);
        getVerifyCodeTv = (TextView) findViewById(R.id.tv_get_verify_code);
        RxTextView.afterTextChangeEvents(phoneEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            String value = Objects.requireNonNull(event.getEditable()).toString();
            if (!isReciprocal) {
                getVerifyCodeTv.setEnabled(!TextUtils.isEmpty(value));
            }
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
        passwordEdt = (EditText) findViewById(R.id.edt_password);
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
        getVerifyCodeTv.setOnClickListener(v -> {
            ((ViewGroup) getVerifyCodeTv.getParent()).setSelected(true);
            getVerifyCode(phoneEdt.getText().toString());
            mainHandler.sendEmptyMessage(RECIPROCAL);
        });
        registerBtn = findViewById(R.id.register);
        registerBtn.setOnClickListener(v -> {
            String phone = phoneEdt.getText().toString().trim();
            String password = passwordEdt.getText().toString().trim();
            String verifyCode = verifyCodeEdt.getText().toString().trim();
            register(phone, password, verifyCode);
        });

        findViewById(R.id.icon_of_county).setOnClickListener(v -> {
                    ViewGroup parent = (ViewGroup) v.getParent();
                    parent.requestFocus();
                    ARouter.getInstance()
                            .build(RouterUtil
                                    .LoginModulePath.SELECT_COUNTRY_AREA_CODE)
                            .navigation(RegisterActivity.this, REQ_COUNTRY_AREA_CODE);
                }
        );

        findViewById(R.id.location_layout).setOnFocusChangeListener(View::setSelected);

        findViewById(R.id.root).setOnClickListener(v -> {
            SystemUtils.hideKeyboard(getBaseContext(), v);
        });
    }

    private boolean handleMessage(Message msg) {
        if (msg.what == CHECK_PHONE_AND_PASSWORD) {
            registerBtn.setEnabled(LoginUtil.isPhone(phoneEdt.getText().toString()) && LoginUtil.isPassword(passwordEdt.getText().toString()));
        } else if (RECIPROCAL == msg.what) {
            timeCount--;
            isReciprocal = timeCount > 0;
            if (isReciprocal) {
                getVerifyCodeTv.setEnabled(false);
                getVerifyCodeTv.setText(String.valueOf("等待（" + timeCount + "s）"));
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
                    TextView telEdt = (TextView) findViewById(R.id.tel_belong_of_place);
                    telEdt.setText(countryAreaCode);
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void getVerifyCode(String phone) {
        MyRetrofit.get().getCommonApiService().getRegisterVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtCenter(getBaseContext(), result.msg);
                },((error)->{
                    error.printStackTrace();
                }));
    }

    private void register(String phone, String password, String verifyCode) {
        MyRetrofit.get()
                .getCommonApiService()
                .register(phone, password, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if(result.isSuccess()) {
                        CLToast.showAtCenter(getBaseContext(), result.msg);
                        registerBtn.endLoading();
                        finish();
                    } else {
                        Log.d("register", result.msg);
                    }
                }, ((error)->{
                    error.printStackTrace();
                }));
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), findViewById(R.id.root));
    }
}
