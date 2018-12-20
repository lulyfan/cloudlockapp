package com.ut.module_msg.repo;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.google.gson.JsonObject;
import com.ut.database.dao.NotifyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.NotificationMessage;

import java.io.IOException;
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
    private NotifyDao notifyDao;

    private NotMessageRepo() {
    }

    public static NotMessageRepo getInstance() {
        synchronized (NotMessageRepo.class) {
            if (instance == null) {
                instance = new NotMessageRepo();
                instance.executor = command -> new Thread(command).start();
                instance.notifyDao = CloudLockDatabaseHolder.get().getNotiDao();
            }
        }
        return instance;
    }

    public LiveData<List<NotificationMessage>> getNotificationMessages() {
        loadNotificationMessages();
        return notifyDao.getNotificationMessages();
    }

    void loadNotificationMessages() {
        executor.execute(() -> {
            try {
                Response<JsonObject> response = MyRetrofit.get().getCommonApiService().getMessage().execute();
                String json = response.body().toString();
                Result<List<NotificationMessage>> result = JSON.parseObject(json, new TypeReference<Result<List<NotificationMessage>>>() {
                });
                if (result.isSuccess()) {
                    NotificationMessage[] tmp = new NotificationMessage[result.data.size()];
                    notifyDao.insertNotiMessage(result.data.toArray(tmp));
                    tmp = null;
                    //toDo
                    NotificationMessage message = new NotificationMessage();
                    message.setMessageId(System.currentTimeMillis() / 1000L);
                    message.setName("Chan的钥匙");
                    message.setDescription("我开了锁，你知道吗?");
                    message.setCreateTime("2018/01/01 00:20");
                    notifyDao.insertNotiMessage(message);
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
