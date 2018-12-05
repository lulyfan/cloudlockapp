package com.ut.module_msg.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.module_msg.adapter.ListAdapter;
import com.ut.module_msg.databinding.FragmentNotificationBinding;
import com.ut.module_msg.model.NotificationMessage;
import com.ut.module_msg.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class NotificationFragment extends BaseFragment {

    private FragmentNotificationBinding mNotifyFgBinding = null;
    private List<NotificationMessage> list = null;
    private ListAdapter<NotificationMessage> listAdapter = null;
    private NotificationViewModel notificationViewModel = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mNotifyFgBinding == null) {
            mNotifyFgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
            initView();
        }
        return mNotifyFgBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {

        list = new ArrayList<>();
        NotificationMessage notification = new NotificationMessage();
        notification.setTitle("chan的智能锁");
        notification.setContent("您收到了一把电子钥匙【Chan的智能锁】，使用期限...");
        notification.setTime("2018/11/26 14:20");
        list.add(notification);

        listAdapter = new ListAdapter<>(getActivity(), R.layout.item_notification_msg, list, BR.notification);
        mNotifyFgBinding.notificationList.setAdapter(listAdapter);
        notificationViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NotificationViewModel.class);
        notificationViewModel.getNotifications().observe(getActivity(), notifications -> {
            listAdapter.updateData(notifications);
        });

        mNotifyFgBinding.notificationList.setOnItemClickListener((parent, view, position, id) -> {
            ARouter.getInstance().build(RouterUtil.MsgModulePath.NOTIFICATION_INFO).withSerializable("notificationInfo", list.get(position)).navigation();
        });
    }
}
