package com.ut.module_msg.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.database.dao.LockMessageInfoDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockMessage;
import com.ut.database.entity.LockMessageInfo;
import com.ut.module_msg.repo.NotMessageRepo;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/27
 * desc   :
 */

@SuppressLint("CheckResult")
public class NotMessageVm extends AndroidViewModel {

    private MutableLiveData<List<LockMessage>> lockMessages;

    private LockMessageInfoDao lockMessageInfoDao;

    public NotMessageVm(@NonNull Application application) {
        super(application);
        lockMessageInfoDao = CloudLockDatabaseHolder.get().getLockMessageInfoDao();
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
                .subscribe(listResult -> {
                    if (listResult.isSuccess()) {
                        if (listResult.data != null) {
                            LockMessageInfo[] tmp = new LockMessageInfo[listResult.data.size()];
                            lockMessageInfoDao.insert(listResult.data.toArray(tmp));
                            tmp = null;
                        }

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


    public LiveData<List<LockMessageInfo>> getLockMessageInfos(String mac) {
        return CloudLockDatabaseHolder.get().getLockMessageInfoDao().findLMessageDetailsByMac(mac);
    }
}
