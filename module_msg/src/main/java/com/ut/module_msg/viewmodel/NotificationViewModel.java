package com.ut.module_msg.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ut.module_msg.model.NotificationMessage;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/27
 * desc   :
 */
public class NotificationViewModel extends ViewModel {

    private MutableLiveData<List<NotificationMessage>> notifications;

    public MutableLiveData<List<NotificationMessage>> getNotifications() {
        if (notifications == null) {
            notifications = new MutableLiveData<>();
            loadNotifications();
        }

        return notifications;
    }

    private void loadNotifications() {
        // do aysnc task
    }
}
