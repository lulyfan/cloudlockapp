package com.ut.module_msg.repo;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.dao.LockMessageDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockMessage;

import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * author : chenjiajun
 * time   : 2018/12/19
 * desc   :
 */
public class NotMessageRepo {

    private static NotMessageRepo instance;
    private LockMessageDao messageDao;

    private NotMessageRepo() {
    }

    public static NotMessageRepo getInstance() {
        synchronized (NotMessageRepo.class) {
            if (instance == null) {
                instance = new NotMessageRepo();
                instance.messageDao = CloudLockDatabaseHolder.get().getLockMessageDao();
            }
        }
        return instance;
    }

    public LiveData<List<LockMessage>> getNotificationMessages() {
        return messageDao.lockMessages();
    }

}
