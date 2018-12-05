package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
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
public class KeyInfoActivity extends BaseActivity {

    private KeyItem keyInfo;
    private ActivityKeyInfoBinding mBinding = null;
    private static final int REQUEST_EDIT_KEY_NAME = 1111;
    private static final int REQUEST_EDIT_LIMITED_TIME = 1112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#00BDCF"));
            getWindow().getDecorView().setFitsSystemWindows(true);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_key_info);
        keyInfo = (KeyItem) getIntent().getSerializableExtra("keyInfo");
        mBinding.setKeyItem(keyInfo);
        mBinding.back.setOnClickListener(v -> finish());
        mBinding.operationRecord.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD).navigation());
        mBinding.tvKeyName.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_KEY_NAME).navigation(this, REQUEST_EDIT_KEY_NAME));
        mBinding.tvKeyType.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_LIMITED_TIME).withSerializable("keyInfo", keyInfo).navigation(this, REQUEST_EDIT_LIMITED_TIME));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_EDIT_KEY_NAME:
                    if (data == null) return;
                    String keyName = data.getStringExtra("edit_key_name");
                    keyInfo.setCaption(keyName);
                    mBinding.setKeyItem(keyInfo);
                    break;
            }
        }
    }
}
