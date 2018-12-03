package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityEditLimitedTimeBinding;
import com.ut.module_lock.entity.KeyItem;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
@Route(path = RouterUtil.LockModulePath.EDIT_LIMITED_TIME)
public class EditLimitedTimeActivity extends AppCompatActivity {

    private ActivityEditLimitedTimeBinding mBinding = null;
    private KeyItem keyInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_limited_time);
        keyInfo = (KeyItem) getIntent().getSerializableExtra("keyInfo");
        mBinding.setKeyItem(keyInfo);
        mBinding.back.setOnClickListener(v -> finish());
    }
}
