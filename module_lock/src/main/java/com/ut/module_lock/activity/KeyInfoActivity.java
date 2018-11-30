package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityKeyInfoBinding;
import com.ut.module_lock.entity.KeyItem;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.KEY_INFO)
public class KeyInfoActivity extends AppCompatActivity {

    private KeyItem keyInfo;
    private ActivityKeyInfoBinding mBinding = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_key_info);
        keyInfo = (KeyItem) getIntent().getSerializableExtra("keyInfo");
        mBinding.setKeyItem(keyInfo);

        mBinding.back.setOnClickListener(v -> finish());
    }
}
