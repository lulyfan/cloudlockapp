package com.ut.module_lock.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SimpleTextWatcher;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.EDIT_KEY_NAME)
public class EditKeyNameActivity extends BaseActivity {

    private EditText nameEdt = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);
        enableImmersive(R.color.title_bar_bg, false);
        initView();
    }

    private void initView() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        nameEdt = findViewById(R.id.edt_key_name);
        nameEdt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        findViewById(R.id.clear).setOnClickListener(v -> nameEdt.setText(""));
        findViewById(R.id.btn_save).setOnClickListener(v -> saveName());
    }

    private void saveName() {
        Intent intent = new Intent();
        intent.putExtra(Constance.EDIT_KEY_NAME, nameEdt.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}
