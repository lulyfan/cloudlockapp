package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockGroup;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class LockGroupViewModel extends BaseViewModel {

    public MutableLiveData<List<LockGroup>> lockGroups = new MutableLiveData<>();
    public MutableLiveData<Void> addGroupSuccess = new MutableLiveData<>();

    public LockGroupViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadLockGroup() {
        service.getGroup()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> lockGroups.postValue(listResult.data),
                        throwable -> tip.postValue(throwable.getMessage()));

    }

    public void addLockGroup(String groupName) {
        service.addGroup(groupName)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }

                    addGroupSuccess.postValue(null);
                })
                .subscribe(voidResult -> tip.postValue(voidResult.msg),
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
