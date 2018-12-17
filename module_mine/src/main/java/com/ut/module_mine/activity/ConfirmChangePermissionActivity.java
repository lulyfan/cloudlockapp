package com.ut.module_mine.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ut.base.BaseActivity;
import com.ut.base.customView.CheckCodeView;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityConfirmChangePermissionBinding;

public class ConfirmChangePermissionActivity extends BaseActivity {

    private ActivityConfirmChangePermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_change_permission);
        initUI();
    }

    private void initUI() {
        initLightToolbar();

        binding.checkCodeView.setInputListener(new CheckCodeView.InputListener() {
            @Override
            public void onInput(String checkCOde) {
                binding.confirm.setEnabled(false);
            }

            @Override
            public void onFinish(String checkCode) {
                binding.confirm.setEnabled(true);
            }
        });
    }
}
