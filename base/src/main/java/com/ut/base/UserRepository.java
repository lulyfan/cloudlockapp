package com.ut.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;

import com.alibaba.fastjson.JSON;
import com.example.operation.MyRetrofit;
import com.ut.base.Utils.UTLog;
import com.ut.database.dao.UserDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/17
 * desc   :
 */

public class UserRepository {

    private static UserRepository instance;
    private UserDao userDao = null;

    private UserRepository() {

    }

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                instance = new UserRepository();
                instance.userDao = CloudLockDatabaseHolder.get().getUserDao();
            }
        }
        return instance;
    }

    public LiveData<User> getUser() {
        return userDao.findLastOne();
    }

    public LiveData<List<User>> getAllUser() {
        return userDao.findAllUsers();
    }

    @SuppressLint("CheckResult")
    public void refreshUser() {
        MyRetrofit.get().getCommonApiService().getUserInfo()
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        userDao.updateUser(result.data);
                    }
                    UTLog.d("update user info ---> " + JSON.toJSONString(result.data));
                }, new ErrorHandler());
    }
}
