package com.ut.module_msg;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

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

//    @Autowired(name = "notificationInfo")
    private NotificationMessage notificationMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        notificationMessage = (NotificationMessage) getIntent().getSerializableExtra("notificationInfo");

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notifi_info);
        mBinding.setNotification(notificationMessage);
        mBinding.back.setOnClickListener((v) -> finish());
        for (int i = 0; i < 10; i++) {
            MessageContent content = new MessageContent();
            content.setDate("2018/09/1" + i);
            content.setContent("Executing tasks: [clean, :module_login:generateDebugSources, :module_msg:generateDebugSources, :commoncomponent:generateDebugSources, :module_lock:generateDebugSources, :base:generateDebugSources, :module_mine:generateDebugSources, :module_mall:generateDebugSources, :app:generateDebugSources]");
            messageContents.add(content);
        }
        mAdapter = new ListAdapter<>(this, R.layout.item_message_content, messageContents, BR.messageContent);
        mBinding.messageList.setAdapter(mAdapter);
    }
}
