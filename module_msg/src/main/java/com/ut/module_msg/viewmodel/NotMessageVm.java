package com.ut.module_msg.viewmodel;

import android.annotation.SuppressLint;
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
import com.ut.database.entity.LockMessage;
import com.ut.database.entity.LockMessageInfo;
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

@SuppressLint("CheckResult")
public class NotMessageVm extends AndroidViewModel {

    private MutableLiveData<List<LockMessage>> lockMessages;

    private MutableLiveData<List<LockMessageInfo>> lockMessageInfos;

    public NotMessageVm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<LockMessageInfo>> getLockMessageInfos(String mac) {
        if (lockMessageInfos == null) {
            lockMessageInfos = new MutableLiveData<>();
            loadMessageInfos(mac);
        }
        return lockMessageInfos;
    }

    public MutableLiveData<List<LockMessage>> getLockMessages() {
        if (lockMessages == null) {
            lockMessages = new MutableLiveData<>();
            loadNotifications();
        }
        return lockMessages;
    }

    public void loadNotifications() {
        NotMessageRepo.getInstance()
                .getNotificationMessages()
                .observe(AppManager.getAppManager().currentActivity(), msgs -> {
                    if (msgs != null) {
                        getLockMessages().postValue(msgs);
                    }
                });
    }

    public void loadMessageInfos(String mac) {
        MyRetrofit.get().getCommonApiService().getLockMessageInfos(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listResult -> {
                    if (listResult.isSuccess()) {
                        lockMessageInfos.postValue(listResult.data);
                    }
                }, new ErrorHandler());
    }

    public void readMessages(String mac) {
        MyRetrofit.get().getCommonApiService().readMessages(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                }, new ErrorHandler());
    }
}
