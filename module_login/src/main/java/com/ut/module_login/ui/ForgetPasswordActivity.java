package com.ut.module_login.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = RouterUtil.LoginModulePath.FORGET_PWD)
public class ForgetPasswordActivity extends BaseActivity {

    private static final int CHECK_PHONE_AND_PASSWORD = 1000;
    private static final int RECIPROCAL = 1001;
    private EditText phoneEdt = null;
    private TextView getVerifyCodeTv = null;
    private EditText passwordEdt = null;
    private Button sureBtn = null;

    private Handler mainHandler = new Handler((this::handleMessage));
    private static final int DEFAULT_TIME_COUNT = 60;
    private int timeCount = DEFAULT_TIME_COUNT;
    private boolean isReciprocal = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        initUI();
    }

    private void initUI() {
        phoneEdt = (EditText) findViewById(R.id.edt_phone);
        getVerifyCodeTv = (TextView) findViewById(R.id.tv_get_verify_code);
        RxTextView.afterTextChangeEvents(phoneEdt).observeOn(AndroidSchedulers.mainThread()).doOnNext((event) -> {
            String value = Objects.requireNonNull(event.getEditable()).toString();
            if (!isReciprocal) {
                getVerifyCodeTv.setEnabled(!TextUtils.isEmpty(value));
            }
            mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
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
            mainHandler.sendEmptyMessage(CHECK_PHONE_AND_PASSWORD);
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
        sureBtn = (Button) findViewById(R.id.sure);
        sureBtn.setOnClickListener(v -> {

        });

        findViewById(R.id.back).setOnClickListener(v ->  supportFinishAfterTransition());
    }

    private boolean handleMessage(Message msg) {
        if (msg.what == CHECK_PHONE_AND_PASSWORD) {
            sureBtn.setEnabled(LoginUtil.isPhone(phoneEdt.getText().toString()) && LoginUtil.isPassword(passwordEdt.getText().toString()));
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

    private void getVerifyCode(String phone) {

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }
}
