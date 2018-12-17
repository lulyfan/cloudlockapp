package com.ut.module_msg;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.databinding.ActivityApplyMessageInfoBinding;
import com.ut.module_msg.model.ApplyMessage;
import com.ut.module_msg.viewmodel.ApplyMessageVm;

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */

@Route(path = RouterUtil.MsgModulePath.APPLY_INFO)
public class ApplyMessageInfoActivity extends BaseActivity {

    private ActivityApplyMessageInfoBinding mBinding = null;
    private ApplyMessage mApplyMessage = null;
    private ApplyMessageVm applyMessageVm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_message_info);
        initLightToolbar();
        mApplyMessage = (ApplyMessage) getIntent().getSerializableExtra("applyMessage");
        setTitle(mApplyMessage.getLockName());
        mBinding.setApplyMessage(mApplyMessage);
        applyMessageVm = ViewModelProviders.of(this).get(ApplyMessageVm.class);
        mBinding.btnIgnoreApply.setOnClickListener(v -> applyMessageVm.ignoreApply(mApplyMessage.getId()));

        mBinding.btnSendKey.setOnClickListener(v -> {
        });
    }

}
