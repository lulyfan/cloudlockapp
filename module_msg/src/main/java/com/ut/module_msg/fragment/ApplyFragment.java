package com.ut.module_msg.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.module_msg.adapter.ListAdapter;
import com.ut.module_msg.databinding.FragmentApplyBinding;
import com.ut.module_msg.model.ApplyMessage;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mApplyFgBinding == null) {
            mApplyFgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
            initView();
        }
        return mApplyFgBinding.getRoot();
    }

    private void initView() {
        ApplyMessage message = new ApplyMessage();
        message.setName("曹哲军");
        message.setHint("您收到了一把电子钥匙【Chan的智能锁】，使用期限...");
        message.setApplicant("程佳佳");
        message.setTime("2019/12/20");
        message.setMessage("你好！我是王撕葱，可以给我分配把钥匙吗？");
        applyMessages.add(message);
        ApplyMessage message1 = new ApplyMessage();
        message1.setName("王撕葱");
        message1.setHint("您收到了一把电子钥匙【公司门锁】，使用期限【永...");
        message1.setApplicant("Chan的智能锁");
        message1.setTime("2019/12/20");
        message1.setMessage("你好！我是王撕葱，可以给我分配把钥匙吗？");
        applyMessages.add(message1);

        ListAdapter<ApplyMessage> mAdapter = new ListAdapter<ApplyMessage>(getContext(), R.layout.item_apply, applyMessages, BR.apply) {
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
    }
}
