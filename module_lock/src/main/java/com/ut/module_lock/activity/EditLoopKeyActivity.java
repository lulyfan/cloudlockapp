package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLoopBinding;
import com.ut.module_lock.entity.KeyItem;

/**
 * author : chenjiajun
 * time   : 2018/12/6
 * desc   :修改循环钥匙页面
 */

@Route(path = RouterUtil.LockModulePath.EDIT_LOOP_TIME)
public class EditLoopKeyActivity extends BaseActivity {

    private ActivityEditLoopBinding mBinding;
    private KeyItem mKeyItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_loop);
        initDarkToolbar();
        setTitle(R.string.lock_loop_key);
        mKeyItem = (KeyItem) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(mKeyItem);
    }
}
