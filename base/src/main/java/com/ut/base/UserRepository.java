package com.ut.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.dao.UserDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        userDao.updateUser(result.data);
                    }
                    Log.d("refreshUser", "refresher" + result.data.toString());
                }, new ErrorHandler());
    }
}
