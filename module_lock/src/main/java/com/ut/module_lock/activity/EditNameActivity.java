package com.ut.module_lock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SimpleTextWatcher;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.EDIT_NAME)
public class EditNameActivity extends BaseActivity {

    private EditText nameEdt = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        initView();
    }

    private void initView() {
        initDarkToolbar();
        setTitle(R.string.lock_name);
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
            if(!hasFocus) {
                SystemUtils.hideKeyboard(getBaseContext(), nameEdt.getRootView());
            }
        });
        findViewById(R.id.clear).setOnClickListener(v -> nameEdt.setText(""));
        findViewById(R.id.btn_save).setOnClickListener(v -> saveName());
    }

    private void saveName() {
        Intent intent = new Intent();
        intent.putExtra(Constance.EDIT_NAME, nameEdt.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        SystemUtils.hideKeyboard(getBaseContext(), getWindow().getDecorView());
    }
}