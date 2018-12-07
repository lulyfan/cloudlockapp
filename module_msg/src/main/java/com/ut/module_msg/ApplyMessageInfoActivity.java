package com.ut.module_msg;

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

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */

@Route(path = RouterUtil.MsgModulePath.APPLY_INFO)
public class ApplyMessageInfoActivity extends BaseActivity {

    private ActivityApplyMessageInfoBinding mBinding = null;
    private ApplyMessage mApplyMessage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.msg_app_statusbar_color, true);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_message_info);
        mApplyMessage = (ApplyMessage) getIntent().getSerializableExtra("applyMessage");
        mBinding.setApplyMessage(mApplyMessage);
        mBinding.back.setOnClickListener(v -> finish());
    }
}
