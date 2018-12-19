package com.ut.module_msg;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.adapter.ListAdapter;
import com.ut.module_msg.databinding.ActivityNotifiInfoBinding;
import com.ut.module_msg.model.NotifyCarrier;
import com.ut.module_msg.model.NotificationMessage;

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
    private ListAdapter<NotificationMessage> mAdapter = null;
    private List<NotificationMessage> notificationMessages = new ArrayList<>();

    private NotifyCarrier notifyCarrier;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifyCarrier = (NotifyCarrier) getIntent().getSerializableExtra("notificationInfo");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifi_info);
        mBinding.setNotifyCarrier(notifyCarrier);
        setTitle(notifyCarrier.getName());
        initLightToolbar();
        mAdapter = new ListAdapter<>(this, R.layout.item_message_content, notificationMessages, BR.notificationMessage);
        mBinding.messageList.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
        mAdapter.updateDate(notifyCarrier.getNotificationMessages());
    }
}
