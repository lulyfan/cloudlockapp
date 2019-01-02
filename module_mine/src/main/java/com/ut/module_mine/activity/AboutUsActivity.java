package com.ut.module_mine.activity;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityAboutUsBinding;

public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        initUI();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.aboutUs));

        try {
            binding.appName.setText(getString(R.string.smartLock) + "\n" +
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
