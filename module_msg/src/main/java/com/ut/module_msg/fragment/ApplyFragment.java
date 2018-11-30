package com.ut.module_msg.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ut.base.BaseFragment;
import com.ut.module_msg.R;
import com.ut.module_msg.database.MessageBase;
import com.ut.module_msg.databinding.FragmentApplyBinding;
import com.ut.module_msg.model.MessageNotification;
import com.ut.module_msg.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class ApplyFragment extends BaseFragment {
    private FragmentApplyBinding mApplyFgBinding = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mApplyFgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
        mApplyFgBinding.apply.setOnClickListener((v) -> {
            insertNotifications();
        });
        return mApplyFgBinding.getRoot();
    }

    private void insertNotifications() {
        List<MessageNotification> mns = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MessageNotification nm = new MessageNotification();
            nm.setContent("content " + i);
            nm.setTitle("title " + i);
            nm.setIcon("https://pic.92to.com/anv/201512/12/bl3yadmemy2.jpg");
            mns.add(nm);
        }
        Disposable subscribe = Observable.just(mns)
                .flatMap(messageNotifications -> {
                    MessageBase.getInstance(getContext())
                            .getMessageNotificationDao()
                            .insert(messageNotifications.toArray(new MessageNotification[messageNotifications.size()]));
                    return Observable.just(messageNotifications);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageNotifications -> {
                    NotificationViewModel viewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NotificationViewModel.class);
                    viewModel.getNotifications().setValue(messageNotifications);
                });
    }
}
