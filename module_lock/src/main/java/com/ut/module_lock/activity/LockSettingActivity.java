package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.AcitivityLockSettingBinding;
import com.ut.module_lock.entity.LockKey;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :锁设置界面
 */

@Route(path = RouterUtil.LockModulePath.LOCK_SETTING)
public class LockSettingActivity extends BaseActivity {
    private AcitivityLockSettingBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LockKey lockKey = getIntent().getParcelableExtra("lock_key");
        mBinding = DataBindingUtil.setContentView(this, R.layout.acitivity_lock_setting);
        mBinding.setLockKey(lockKey);
        initDarkToolbar();
        setTitle(R.string.lock_setting);
        mBinding.chooseGroup.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.CHOOSE_KEY_GROUP).navigation());
    }
}
