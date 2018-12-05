package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.AcitivityLockSettingBinding;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :锁设置界面
 */
public class LockSettingActivity extends BaseActivity {
    private AcitivityLockSettingBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.title_bar_bg, false);
        mBinding = DataBindingUtil.setContentView(this, R.layout.acitivity_lock_setting);
        setTitle(R.string.lock_setting);
        enableImmersive();
        mBinding.layoutChooseGroup.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.CHOOSE_KEY_GROUP).navigation());
    }
}
