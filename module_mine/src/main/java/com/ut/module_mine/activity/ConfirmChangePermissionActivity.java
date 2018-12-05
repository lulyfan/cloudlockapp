package com.ut.module_mine.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityConfirmChangePermissionBinding;

public class ConfirmChangePermissionActivity extends BaseActivity {

    private ActivityConfirmChangePermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.appBarColor, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_change_permission);
        initUI();
    }

    private void initUI() {
        setActionBar();
    }

    private void setActionBar() {
        setSupportActionBar(binding.toolbar11);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_black);
        actionBar.setTitle(null);
    }
}
