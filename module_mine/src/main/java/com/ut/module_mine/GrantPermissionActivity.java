package com.ut.module_mine;

import android.databinding.DataBindingUtil;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;

import com.ut.base.BaseActivity;
import com.ut.module_mine.databinding.ActivityGrantPermissionBinding;

public class GrantPermissionActivity extends BaseActivity {
    private ActivityGrantPermissionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grant_permission);
        initUI();
    }

    private void initUI() {
        setSupportActionBar(binding.toolbar5);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(null);

        binding.viewPager.setAdapter(new GrantPermissionAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }
}
