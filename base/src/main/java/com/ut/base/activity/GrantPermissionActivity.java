package com.ut.base.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.MenuItem;

import com.ut.base.BaseActivity;
import com.ut.base.R;
import com.ut.base.Utils.Util;
import com.ut.base.adapter.GrantPermissionAdapter;
import com.ut.base.databinding.ActivityGrantPermissionBinding;

public class GrantPermissionActivity extends BaseActivity {
    private ActivityGrantPermissionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grant_permission);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.setTabWidth(binding.tabLayout);
    }

    private void initUI() {
        setSupportActionBar(binding.toolbar5);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(null);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_white);

        binding.headContainer.setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        binding.viewPager.setAdapter(new GrantPermissionAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
