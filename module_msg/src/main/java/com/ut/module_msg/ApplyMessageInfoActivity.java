package com.ut.module_msg;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.databinding.ActivityApplyMessageInfoBinding;
import com.ut.database.entity.ApplyMessage;
import com.ut.module_msg.viewmodel.ApplyMessageVm;

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */

@Route(path = RouterUtil.MsgModulePath.APPLY_INFO)
public class ApplyMessageInfoActivity extends BaseActivity {

    private static final int REQUEST_CODE_SEND_KEY = 2000;
    private ActivityApplyMessageInfoBinding mBinding = null;
    private ApplyMessage mApplyMessage = null;
    private ApplyMessageVm applyMessageVm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_message_info);
        initLightToolbar();
        mApplyMessage = (ApplyMessage) getIntent().getSerializableExtra("applyMessage");
        boolean hasDealt = getIntent().getBooleanExtra("hasDealt", false);
        if (hasDealt) {
            mBinding.btnLayout.setVisibility(View.GONE);
        }
        setTitle(mApplyMessage.getLockName());
        mBinding.setApplyMessage(mApplyMessage);
        applyMessageVm = ViewModelProviders.of(this).get(ApplyMessageVm.class);
        mBinding.btnIgnoreApply.setOnClickListener(v -> applyMessageVm.ignoreApply(mApplyMessage.getId()));

        mBinding.btnSendKey.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.BaseModulePath.SEND_KEY)
                    .withInt(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_RULER_TYPE, mApplyMessage.getRuleType())
                    .withString(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MAC, mApplyMessage.getMac())
                    .withString(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MOBILE, mApplyMessage.getMobile())
                    .withBoolean(RouterUtil.LockModuleExtraKey.EXTRA_CANT_EDIT_PHONE, true)
                    .navigation(ApplyMessageInfoActivity.this, REQUEST_CODE_SEND_KEY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SEND_KEY) {
            finish();
        }
    }
}
