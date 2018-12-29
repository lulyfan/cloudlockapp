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
        lockMessage = (LockMessage) getIntent().getSerializableExtra(RouterUtil.MsgModulePath.IntentKey.EXTRA_MESSAGE_INFO);
        notMessageVm = ViewModelProviders.of(this).get(NotMessageVm.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifi_info);
        setTitle(lockMessage.getLockName());
        initLightToolbar();
        mAdapter = new ListAdapter<LockMessageInfo>(this, R.layout.item_message_content, notificationMessages, BR.lockMessageInfo);
        mBinding.messageList.setAdapter(mAdapter);
        notMessageVm.getLockMessageInfos(lockMessage.getLockMac()).observe(this, lockMessageInfos -> {
            if (lockMessageInfos != null && !lockMessageInfos.isEmpty()) {
                mAdapter.updateDate(lockMessageInfos);
            }
        });

        mBinding.refreshLayout.setOnRefreshListener(() -> {
            loadData();
            mBinding.refreshLayout.postDelayed(() -> {
                mBinding.refreshLayout.setRefreshing(false);
            }, 1200L);
        });

        loadData();
    }

    private void loadData() {
        notMessageVm.loadMessageInfos(lockMessage.getLockMac());
    }
}
