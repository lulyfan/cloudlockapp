package com.ut.module_msg.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.ut.module_msg.databinding.FragmentApplyBinding;
import com.ut.module_msg.model.ApplyMessage;
import com.ut.module_msg.viewmodel.ApplyMessageVm;

import java.util.ArrayList;
import java.util.List;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class ApplyFragment extends BaseFragment {
    private FragmentApplyBinding mApplyFgBinding = null;
    private List<ApplyMessage> applyMessages = new ArrayList<>();
    private ListAdapter<ApplyMessage> mAdapter = null;
    private ApplyMessageVm mApplyMessageVm = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mApplyFgBinding == null) {
            mApplyFgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
            initView();
            mApplyMessageVm = ViewModelProviders.of(this).get(ApplyMessageVm.class);
            mApplyMessageVm.getApplyMessages().observe(this, ams -> {
                mApplyFgBinding.swipeRefreshLayout.setRefreshing(false);
                if (ams == null || ams.isEmpty()) return;
                mAdapter.updateDate(ams);
            });
        }
        return mApplyFgBinding.getRoot();
    }

    private void initView() {
        mAdapter = new ListAdapter<ApplyMessage>(getContext(), R.layout.item_apply, applyMessages, BR.apply) {
            @Override
            public void addBadge(ViewDataBinding binding, int position) {
                Badge badge = null;
                ViewGroup icon = binding.getRoot().findViewById(R.id.icon_layout);
                if (icon.getTag() == null) {
                    badge = new QBadgeView(getActivity());
                    icon.setTag(badge);
                } else {
                    badge = (Badge) icon.getTag();
                }

                ApplyMessage message = applyMessages.get(position);
                badge.bindTarget(icon)
                        .setShowShadow(false)
                        .setBadgeBackgroundColor(Color.parseColor("#F55D54"))
                        .setBadgeTextColor(Color.WHITE)
                        .setGravityOffset(0, 0, true)
                        .setBadgeTextSize(9, true)
                        .setBadgeText(message.getStatus());
            }
        };
        mApplyFgBinding.applyList.setAdapter(mAdapter);
        mApplyFgBinding.applyList.setOnItemClickListener((parent, view, position, id) -> {
            mApplyMessageVm.checkApplyStatus(applyMessages.get(position));
        });

        mApplyFgBinding.swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mApplyFgBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            mApplyFgBinding.swipeRefreshLayout.setRefreshing(true);
            mApplyMessageVm.loadApplyMessages();
            mApplyFgBinding.swipeRefreshLayout.postDelayed(() -> mApplyFgBinding.swipeRefreshLayout.setRefreshing(false), 3000L);
        });
    }

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
        if(mApplyMessageVm != null) {
            mApplyMessageVm.loadApplyMessages();
        }
    }
}
