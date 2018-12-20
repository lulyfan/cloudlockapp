package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.api.CommonApiService;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.database.entity.User;

public class BaseViewModel extends AndroidViewModel {

    public MutableLiveData<String> tip = new MutableLiveData<>();
    protected CommonApiService service;
    protected User user;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        service = MyRetrofit.get().getCommonApiService();
        user = BaseApplication.getUser();
    }
}
