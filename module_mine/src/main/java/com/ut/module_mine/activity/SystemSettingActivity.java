package com.ut.module_mine.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivitySystemSettingBinding;

public class SystemSettingActivity extends BaseActivity {

    private ActivitySystemSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_system_setting);
        initUI();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.systemSetting));

        binding.aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(SystemSettingActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });
    }
}
