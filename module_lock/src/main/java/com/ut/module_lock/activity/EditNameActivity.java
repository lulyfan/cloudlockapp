package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.viewmodel.EditNameVM;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.EDIT_NAME)
public class EditNameActivity extends BaseActivity {


    private int nameType = RouterUtil.LockModuleConstParams.NAMETYPE_LOCK;

    private EditText nameEdt = null;
    private String mac = null;
    private long keyId = 0;

    private LoadingButton loadingButton = null;

    private EditNameVM mEditNameVM = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        String title = getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.EDIT_NAME_TITLE);
        nameType = getIntent().getIntExtra(RouterUtil.LockModuleExtraKey.NAME_TYPE, RouterUtil.LockModuleConstParams.NAMETYPE_LOCK);
        mac = getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.MAC);
        keyId = getIntent().getLongExtra(RouterUtil.LockModuleExtraKey.KEY_ID, 0L);
        initView();
        setTitle(title);
        initVM();
    }

    private void initVM() {
        mEditNameVM = ViewModelProviders.of(this).get(EditNameVM.class);
        mEditNameVM.getSetDeviceNameResult().observe(this, result -> {
            if (result) {
                Intent intent = new Intent();
                intent.putExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY, deviceKey);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mEditNameVM.getShowTip().observe(this, tip -> {
            CLToast.showAtBottom(EditNameActivity.this, tip);
        });
    }

    private void initView() {
        initDarkToolbar();
        nameEdt = findViewById(R.id.edt_name);
        nameEdt.setOnEditorActionListener((v, actionId, event) -> {
            if(event != null) {
                //当event不为null，多次触发（点击，移动）
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    saveName();
                }
            } else if(actionId == EditorInfo.IME_ACTION_DONE) {
                saveName();
            }
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
        if (getIntent().hasExtra(RouterUtil.LockModuleExtraKey.NAME)) {
            nameEdt.setText(getIntent().getStringExtra(RouterUtil.LockModuleExtraKey.NAME));
        }
        findViewById(R.id.clear).setOnClickListener(v -> nameEdt.setText(""));
        loadingButton = findViewById(R.id.btn_save);
        loadingButton.setOnClickListener(v -> saveName());
    }

    @SuppressLint("CheckResult")
    private void saveName() {
        String name = nameEdt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            CLToast.showAtBottom(this, getString(R.string.lock_name_not_allow_null));
            return;
        }

        if (nameType == RouterUtil.LockModuleConstParams.NAMETYPE_LOCK) {
            if (TextUtils.isEmpty(mac)) return;

            if(!SystemUtils.isLetterDigitOrChinese(nameEdt.getText().toString().trim())) {
                CLToast.showAtCenter(getBaseContext(), "锁名称不能含有符号");
                return;
            }

            Intent intent = new Intent();
            intent.putExtra(Constance.EDIT_NAME, nameEdt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        } else if (nameType == RouterUtil.LockModuleConstParams.NAMETYPE_KEY && keyId != 0) {
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
                    }, new ErrorHandler() {
                        @Override
                        public void accept(Throwable throwable) {
                            super.accept(throwable);
                            loadingButton.endLoading();
                        }
                    });
        } else if (nameType == RouterUtil.LockModuleConstParams.NAMETYPE_DEVICE_KEY) {
            deviceKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY);
            deviceKey.setName(name);
            mEditNameVM.setDeviceName(deviceKey);
        }
    }

    DeviceKey deviceKey;

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }
}
