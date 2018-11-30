package com.ut.module_msg.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ut.base.BaseFragment;
import com.ut.module_msg.BR;
import com.ut.module_msg.R;
import com.ut.module_msg.adapter.ListAdapter;
import com.ut.module_msg.databinding.FragmentApplyBinding;
import com.ut.module_msg.model.ApplyMessage;

import java.util.ArrayList;
import java.util.List;

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
        mApplyFgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
        initView();
        return mApplyFgBinding.getRoot();
    }

    private void initView() {

        ApplyMessage message = new ApplyMessage();
        message.setHint("您收到一把电子钥匙【xxxx】，xxxxxxxxxxxx");
        message.setName("活生生");
        applyMessages.add(message);

        ListAdapter<ApplyMessage> mAdapter = new ListAdapter<>(getContext(), R.layout.item_apply, applyMessages, BR.apply);
        mApplyFgBinding.applyList.setAdapter(mAdapter);
    }
}
