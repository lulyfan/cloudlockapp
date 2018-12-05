package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ut.base.BaseActivity;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityApplyKeyBinding;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :
 */

public class ApplyKeyActivity extends BaseActivity {

    private ActivityApplyKeyBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_key);
        setTitle(R.string.lock_apply_key);
    }
}
