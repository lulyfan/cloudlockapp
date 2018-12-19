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
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.base.adapter.ListAdapter;
import com.ut.module_msg.databinding.FragmentNotificationBinding;
import com.ut.module_msg.model.NotifyCarrier;
import com.ut.module_msg.viewmodel.NotificationMessageVm;

import java.util.ArrayList;
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
    private List<NotifyCarrier> list = new ArrayList<>();
    private ListAdapter<NotifyCarrier> listAdapter = null;
    private NotificationMessageVm notificationViewModel = null;

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
        listAdapter = new ListAdapter<NotifyCarrier>(getActivity(), R.layout.item_notification_carrier, list, BR.notifyCarrier) {
            @Override
            public void addBadge(ViewDataBinding binding, int position) {
                super.addBadge(binding, position);
                Badge badge = null;
                ImageView icon = binding.getRoot().findViewById(R.id.icon);
                if (icon.getTag() == null) {
                    badge = new QBadgeView(getActivity());
                    icon.setTag(badge);
                } else {
                    badge = (Badge) icon.getTag();
                }
                NotifyCarrier notifyCarrier = list.get(position);
                badge.bindTarget((View) icon.getParent())
                        .setBadgeBackgroundColor(Color.parseColor("#F55D54"))
                        .setBadgeTextColor(Color.WHITE)
                        .setShowShadow(false)
                        .setBadgeTextSize(9, true)
                        .setBadgeNumber(notifyCarrier.countUnRead());
            }
        };
        mNotifyFgBinding.notificationList.setAdapter(listAdapter);
        notificationViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NotificationMessageVm.class);
        notificationViewModel.getNotifications().observe(getActivity(), messages -> {
            listAdapter.updateDate(messages);
        });

        mNotifyFgBinding.notificationList.setOnItemClickListener((parent, view, position, id) -> {
            ARouter.getInstance().build(RouterUtil.MsgModulePath.NOTIFICATION_INFO).withSerializable("notificationInfo", list.get(position)).navigation();
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
}
