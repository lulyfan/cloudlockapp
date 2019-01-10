package com.ut.module_msg.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.entity.LockMessage;
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.base.adapter.ListAdapter;
import com.ut.module_msg.databinding.FragmentNotificationBinding;
import com.ut.module_msg.viewmodel.NotMessageVm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class NotificationFragment extends BaseFragment {

    private FragmentNotificationBinding mNotifyFgBinding = null;
    private List<LockMessage> list = new ArrayList<>();
    private ListAdapter<LockMessage> listAdapter = null;
    private NotMessageVm notificationViewModel = null;

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
        listAdapter = new ListAdapter<LockMessage>(getContext(), R.layout.item_notification_carrier, list, BR.lockMessage) {
            @Override
            public void handleItem(ViewDataBinding binding, int position) {
                super.handleItem(binding, position);
                Badge badge = null;
                ImageView icon = binding.getRoot().findViewById(R.id.icon);
                if (icon.getTag() == null) {
                    badge = new QBadgeView(getActivity());
                    icon.setTag(badge);
                } else {
                    badge = (Badge) icon.getTag();
                }
                LockMessage lockMessage = list.get(position);
                badge.bindTarget((View) icon.getParent())
                        .setBadgeBackgroundColor(Color.parseColor("#F55D54"))
                        .setBadgeTextColor(Color.WHITE)
                        .setShowShadow(false)
                        .setBadgeTextSize(9, true)
                        .setBadgeNumber(lockMessage.getUnReadCount());
            }
        };
        mNotifyFgBinding.notificationList.setAdapter(listAdapter);
        notificationViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NotMessageVm.class);
        notificationViewModel.getLockMessages().observe(getActivity(), carriers -> {
            mNotifyFgBinding.noData.setVisibility(carriers == null || carriers.isEmpty() ? View.VISIBLE : View.GONE);
            Collections.sort(carriers, ((o1, o2) -> o1.getUnReadCount() > o2.getUnReadCount() ? -1 : 0));
            listAdapter.updateDate(carriers);
        });

        mNotifyFgBinding.notificationList.setOnItemClickListener((parent, view, position, id) -> {

            ARouter.getInstance()
                    .build(RouterUtil.MsgModulePath.NOTIFICATION_INFO)
                    .withSerializable(RouterUtil.MsgModulePath.IntentKey.EXTRA_MESSAGE_INFO, list.get(position))
                    .navigation();

            readMessages(list.get(position).getLockMac());
        });
        mNotifyFgBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light);
        mNotifyFgBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            notificationViewModel.loadNotifications();
            mNotifyFgBinding.swipeRefreshLayout.postDelayed(() -> {
                mNotifyFgBinding.swipeRefreshLayout.setRefreshing(false);
            }, 2000L);
        });
    }

    private void readMessages(String mac) {
        notificationViewModel.readMessages(mac);
    }


    @Override
    protected void onUserVisible() {
        super.onUserVisible();
        if (notificationViewModel != null) {
            notificationViewModel.loadNotifications();
        }
    }
}
