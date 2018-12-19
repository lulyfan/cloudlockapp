package com.ut.module_msg.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.commoncomponent.CLToast;
import com.ut.module_msg.model.NotifyCarrier;
import com.ut.database.entity.NotificationMessage;
import com.ut.module_msg.repo.NotMessageRepo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/27
 * desc   :
 */
public class NotMessageVm extends AndroidViewModel {

    private MutableLiveData<List<NotifyCarrier>> notifications;

    public NotMessageVm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<NotifyCarrier>> getNotifications() {
        if (notifications == null) {
            notifications = new MutableLiveData<>();
            loadNotifications();
        }
        return notifications;
    }

    public void loadNotifications() {
        // do aysnc task
        NotMessageRepo.getInstance().getNotificationMessages().observe(AppManager.getAppManager().currentActivity(), notifications->{
            if(notifications != null) {
                getNotifications().postValue(handleData(notifications));
            }
        });
    }

    private List<NotifyCarrier> handleData(List<NotificationMessage> nms) {
        LinkedHashMap<String, List<NotificationMessage>> map = new LinkedHashMap<>();
        for (NotificationMessage nm : nms) {
            if (map.containsKey(nm.getName())) {
                List<NotificationMessage> tmp = map.get(nm.getName());
                tmp.add(nm);
            } else {
                List<NotificationMessage> tmp = new ArrayList<>();
                tmp.add(nm);
                map.put(nm.getName(), tmp);
            }
        }

        Set<String> keys = map.keySet();
        List<NotifyCarrier> notifyCarriers = new ArrayList<>();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            NotifyCarrier notifyCarrier = new NotifyCarrier();
            notifyCarrier.setName(key);
            notifyCarrier.setNotificationMessages(map.get(key));
            notifyCarriers.add(notifyCarrier);
        }

        return notifyCarriers;
    }
}
