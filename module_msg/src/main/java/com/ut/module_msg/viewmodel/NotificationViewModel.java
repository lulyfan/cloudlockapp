package com.ut.module_msg.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ut.base.BaseApplication;
import com.ut.module_msg.database.MessageBase;
import com.ut.module_msg.model.MessageNotification;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/27
 * desc   :
 */
public class NotificationViewModel extends ViewModel {

    private MutableLiveData<List<MessageNotification>> notifications;

    public MutableLiveData<List<MessageNotification>> getNotifications() {
        if (notifications == null) {
            notifications = new MutableLiveData<>();
            loadNotifications();
        }

        return notifications;
    }

    private void loadNotifications() {
        // do aysnc task
        Disposable subscribe = Observable.just(MessageBase.getInstance(BaseApplication.getAppContext()))
                .flatMap(instance -> Observable.just(instance.getMessageNotificationDao().getNotifications()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ns -> notifications.setValue(ns));
    }
}
