package com.ut.base;

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

import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * author : chenjiajun
 * time   : 2018/12/17
 * desc   :
 */

public class UserRepository {

    private static UserRepository instance;
    private Executor executor = null;
    private UserDao userDao = null;

    private UserRepository() {

    }

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                instance = new UserRepository();
                instance.userDao = CloudLockDatabaseHolder.get().getUserDao();
                instance.executor = (command) -> Schedulers.io().scheduleDirect(command);
            }
        }
        return instance;
    }

    public LiveData<User> getUser() {
        refreshUser();
        return userDao.findLastOne();
    }


    public LiveData<List<User>> getAllUser() {
        return userDao.findAllUsers();
    }

    public void refreshUser() {
        executor.execute(() -> {
            try {
                Response<Result<User>> response = MyRetrofit.get().getCommonApiService().getUserInfo().execute();
                if (response.body() != null && response.body().isSuccess()) {
                    userDao.updateUser(response.body().data);
                }
                Log.d("refreshUser", "refresher" + response.body().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
