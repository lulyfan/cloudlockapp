package com.ut.module_login.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.LoadingButton;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;


@Route(path = RouterUtil.LoginModulePath.REGISTER)
public class RegisterActivity extends BaseActivity {

    private static final int CHECK_PHONE_AND_PASSWORD = 1000;
    private static final int RECIPROCAL = 1001;
    private static final int REQ_COUNTRY_AREA_CODE = 1002;
    private EditText phoneEdt = null;
    private TextView getVerifyCodeTv = null;
    private EditText passwordEdt = null;
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
            registerBtn.startLoading();
            register();

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

        findViewById(R.id.root).setOnClickListener(v->{
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


    private void getVerifyCode(String phone) {

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }


    private void register() {

    }
}
