package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.ut.database.entity.Lock;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class ChangeLockPermissionViewModel extends BaseViewModel{

    private int currentPage = 1;
    private int pageSize = 10;
    public MutableLiveData<List<Lock>> locks = new MutableLiveData<>();

    public ChangeLockPermissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadAdminLock() {
        service.pageAdminLock(currentPage, pageSize)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> locks.postValue(listResult.data),
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
