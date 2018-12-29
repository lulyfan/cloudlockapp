package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SimpleTextWatcher;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.commoncomponent.LoadingButton;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.EDIT_NAME)
public class EditNameActivity extends BaseActivity {

    private EditText nameEdt = null;
    private boolean isLock = false;
    private String mac = null;
    private long keyId = 0;

    private LoadingButton loadingButton = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        String title = getIntent().getStringExtra("edit_name_title");
        isLock = getIntent().getBooleanExtra("is_lock", false);
        mac = getIntent().getStringExtra("mac");
        keyId = getIntent().getLongExtra("key_id", 0L);
        initView();
        setTitle(title);
    }

    private void initView() {
        initDarkToolbar();
        nameEdt = findViewById(R.id.edt_name);
        nameEdt.setOnEditorActionListener((v, actionId, event) -> {
            saveName();
            return false;
        });
        nameEdt.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            protected void afterChanged(Editable s) {
                super.afterChanged(s);
                findViewById(R.id.btn_save).setEnabled(!TextUtils.isEmpty(s));
            }
        });
        nameEdt.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                SystemUtils.hideKeyboard(getBaseContext(), nameEdt.getRootView());
            }
        });
        findViewById(R.id.clear).setOnClickListener(v -> nameEdt.setText(""));
        loadingButton = findViewById(R.id.btn_save);
        loadingButton.setOnClickListener(v -> saveName());
    }

    @SuppressLint("CheckResult")
    private void saveName() {
        String name = nameEdt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            CLToast.showAtBottom(this, "名称不能为空");
            return;
        }

        if (isLock) {
            if (TextUtils.isEmpty(mac)) return;
            Intent intent = new Intent();
            intent.putExtra(Constance.EDIT_NAME, nameEdt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if (keyId != 0) {
            loadingButton.startLoading();
            MyRetrofit.get().getCommonApiService().editKeyName(keyId, name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                            loadingButton.endLoading();
                            Intent intent = new Intent();
                            intent.putExtra(Constance.EDIT_NAME, nameEdt.getText().toString());
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                        CLToast.showAtBottom(getBaseContext(), result.msg);
                    }, new ErrorHandler(){
                        @Override
                        public void accept(Throwable throwable) {
                            super.accept(throwable);
                            loadingButton.endLoading();
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }
}
