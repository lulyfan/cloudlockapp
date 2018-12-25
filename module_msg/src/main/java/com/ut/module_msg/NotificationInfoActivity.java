package com.ut.module_msg;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.adapter.ListAdapter;
import com.ut.database.entity.LockMessage;
import com.ut.database.entity.LockMessageInfo;
import com.ut.module_msg.databinding.ActivityNotifiInfoBinding;
import com.ut.module_msg.model.NotifyCarrier;
import com.ut.database.entity.NotificationMessage;
import com.ut.module_msg.viewmodel.NotMessageVm;

import java.util.ArrayList;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */

@Route(path = RouterUtil.MsgModulePath.NOTIFICATION_INFO)
public class NotificationInfoActivity extends BaseActivity {

    private ActivityNotifiInfoBinding mBinding = null;
    private ListAdapter<LockMessageInfo> mAdapter = null;
    private List<LockMessageInfo> notificationMessages = new ArrayList<>();
    private NotMessageVm notMessageVm;

    private LockMessage lockMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockMessage = (LockMessage) getIntent().getSerializableExtra("notificationInfo");
        notMessageVm = ViewModelProviders.of(this).get(NotMessageVm.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifi_info);
        setTitle(lockMessage.getName());
        initLightToolbar();
        mAdapter = new ListAdapter<LockMessageInfo>(this, R.layout.item_message_content, notificationMessages, BR.lockMessageInfo);
        mBinding.messageList.setAdapter(mAdapter);
        notMessageVm.getLockMessageInfos(lockMessage.getLockMac()).observe(this, lockMessageInfos -> {
            if (lockMessageInfos != null && !lockMessageInfos.isEmpty()) {
                mAdapter.updateDate(lockMessageInfos);
            }
        });
        loadData();
        readMessages();
    }

    private void readMessages() {
        notMessageVm.readMessags(lockMessage.getLockMac());
    }

    private void loadData() {
        notMessageVm.getLockMessageInfos(lockMessage.getLockMac());
    }
}
