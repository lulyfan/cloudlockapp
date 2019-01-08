package com.ut.module_msg.repo;

import android.arch.lifecycle.LiveData;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.dao.LockMessageDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockMessage;

import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.Response;

/**
 * author : chenjiajun
 * time   : 2018/12/19
 * desc   :
 */
public class NotMessageRepo {

    private static NotMessageRepo instance;
    private Executor executor;
    private LockMessageDao messageDao;

    private NotMessageRepo() {
    }

    public static NotMessageRepo getInstance() {
        synchronized (NotMessageRepo.class) {
            if (instance == null) {
                instance = new NotMessageRepo();
                instance.executor = command -> new Thread(command).start();
                instance.messageDao = CloudLockDatabaseHolder.get().getLockMessageDao();
            }
        }
        return instance;
    }

    public LiveData<List<LockMessage>> getNotificationMessages() {
        loadNotificationMessages();
        return messageDao.lockMessages();
    }

    void loadNotificationMessages() {
        executor.execute(() -> {
            try {
                Response<Result<List<LockMessage>>> response = MyRetrofit.get().getCommonApiService().getMessage().execute();
                Result<List<LockMessage>> result = response.body();
                if(result != null) {
                    if (result.isSuccess()) {
                        LockMessage[] tmp = new LockMessage[result.data.size()];
//                        messageDao.deleteAll();
                        messageDao.insert(result.data.toArray(tmp));
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
