package com.ut.module_msg.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.ErrorHandler;
import com.ut.commoncomponent.CLToast;
import com.ut.module_msg.model.NotifyCarrier;
import com.ut.module_msg.model.NotificationMessage;

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
public class NotificationMessageVm extends AndroidViewModel {

    private MutableLiveData<List<NotifyCarrier>> notifications;

    public NotificationMessageVm(@NonNull Application application) {
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
        Disposable getMessage = MyRetrofit.get().getCommonApiService().getMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(jsonObject -> {
                    String json = jsonObject.toString();
                    Result<List<NotificationMessage>> result = JSON.parseObject(json, new TypeReference<Result<List<NotificationMessage>>>() {
                    });
                    return result;
                }).subscribe(result -> {
                    if (result.isSuccess()) {
                        getNotifications().postValue(handleData(result.data));
                    } else {
                        CLToast.showAtBottom(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
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
