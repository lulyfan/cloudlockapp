package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivitySetNameBinding;
import com.ut.module_lock.dialog.AddSuccessDialog;

public class SetNameActivity extends BaseActivity {
    private ActivitySetNameBinding mSetNameBinding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_set_name);
        enableImmersive();
        mSetNameBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_name);
        setLightStatusBar();
        setTitle(R.string.lock_title_set_name);
        initView();
    }

    private void initView() {
        mSetNameBinding.clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSetNameBinding.btnSetNameConfirm.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onXmlClick(View view) {
        super.onXmlClick(view);
        int i = view.getId();
        if (i == R.id.btn_setName_confirm) {
            new AddSuccessDialog(this, false, "Chan的智能锁/这里名字总共可以设置20位字符").show();

        }
    }
}
