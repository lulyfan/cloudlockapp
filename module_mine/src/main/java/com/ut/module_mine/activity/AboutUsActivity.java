package com.ut.module_mine.activity;

import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.example.entity.entity.Cloudlockenterpriseinfo;
import com.example.operation.CommonApi;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.PreferenceUtil;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityAboutUsBinding;

import io.reactivex.disposables.Disposable;

public class AboutUsActivity extends BaseActivity {

    private ActivityAboutUsBinding binding;
    private static final String CACHE_INFO_DATA = "cache_info_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);
        initUI();
        initInfo();
    }

    private void initInfo() {
        Disposable disposable = CommonApi.getCloudlockenterpriseinfo()
                .subscribe(info -> {
                    initData(info);
                    PreferenceUtil.getInstance(getBaseContext()).setString(CACHE_INFO_DATA, JSON.toJSONString(info));
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        String cache = PreferenceUtil.getInstance(getBaseContext()).getString(CACHE_INFO_DATA);
                        Cloudlockenterpriseinfo info = JSON.parseObject(cache, Cloudlockenterpriseinfo.class);
                        initData(info);
                    }
                });
    }

    private void initData(Cloudlockenterpriseinfo cloudlockenterpriseinfo) {
        binding.textView41.setText(cloudlockenterpriseinfo.getMobile());
        binding.textView44.setText(cloudlockenterpriseinfo.getUrl());
        binding.textView48.setText(cloudlockenterpriseinfo.getEmail());
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
