package com.ut.module_msg.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.module_msg.adapter.ListAdapter;
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
                applyMessages.clear();
                applyMessages.addAll(ams);
                mAdapter.notifyDataSetChanged();
            });
        }
        return mApplyFgBinding.getRoot();
    }

    private void initView() {
        mAdapter = new ListAdapter<ApplyMessage>(getContext(), R.layout.item_apply, applyMessages, BR.apply) {
            @Override
            public void addBadge(ViewDataBinding binding, int position) {
                Badge badge = null;
                ImageView icon = binding.getRoot().findViewById(R.id.icon);
                if (icon.getTag() == null) {
                    badge = new QBadgeView(getActivity());
                    icon.setTag(badge);
                } else {
                    badge = (Badge) icon.getTag();
                }
                badge.bindTarget((View) icon.getParent())
                        .setShowShadow(false)
                        .setBadgeBackgroundColor(Color.parseColor("#F55D54"))
                        .setBadgeTextColor(Color.WHITE)
                        .setGravityOffset(0, -2, true)
                        .setBadgeTextSize(9, true)
                        .setBadgeText("待处理");
            }
        };
        mApplyFgBinding.applyList.setAdapter(mAdapter);
        mApplyFgBinding.applyList.setOnItemClickListener((parent, view, position, id) -> {
            ARouter.getInstance().build(RouterUtil.MsgModulePath.APPLY_INFO).withSerializable("applyMessage", applyMessages.get(position)).navigation();
        });

        mApplyFgBinding.swipeRefreshLayout.setOnRefreshListener(() -> {
            mApplyFgBinding.swipeRefreshLayout.setRefreshing(true);
            mApplyMessageVm.loadApplyMessages();
            new Handler().postDelayed(() -> mApplyFgBinding.swipeRefreshLayout.setRefreshing(false), 3000L);
        });
    }
}
