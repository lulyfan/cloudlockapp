package com.ut.module_msg;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.adapter.ListAdapter;
import com.ut.module_msg.databinding.ActivityNotifiInfoBinding;
import com.ut.module_msg.model.MessageContent;
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
    private ListAdapter<MessageContent> mAdapter = null;
    private List<MessageContent> messageContents = new ArrayList<>();

    private NotificationMessage mNotificationMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotificationMessage = (NotificationMessage) getIntent().getSerializableExtra("notificationInfo");
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifi_info);
        enableImmersive(R.color.msg_app_statusbar_color, true);
        mBinding.setNotification(mNotificationMessage);
        mBinding.back.setOnClickListener((v) -> finish());
        MessageContent content = new MessageContent();
        content.setDate("2018/09/10");
        content.setContent("您收到了一把电子钥匙【Chan的智能锁】，使用期限为【永久】。");
        messageContents.add(content);
        MessageContent content1 = new MessageContent();
        content1.setDate("2018/09/11");
        content1.setContent("您收到了一把电子钥匙【Chan的智能锁】，使用期限为【单次】。");
        messageContents.add(content1);
        mAdapter = new ListAdapter<>(this, R.layout.item_message_content, messageContents, BR.messageContent);
        mBinding.messageList.setAdapter(mAdapter);
    }
}
