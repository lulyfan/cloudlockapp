package com.ut.module_mine.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.base.ErrorHandler;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.entity.LockGroup;
import com.ut.module_mine.R;

import java.util.List;

public class LockGroupViewModel extends BaseViewModel {

    public LiveData<List<LockGroup>> mLockGroups;
    public MutableLiveData<Void> addGroupSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> loadLockGroupState = new MutableLiveData<>();

    public LockGroupViewModel(@NonNull Application application) {
        super(application);
        mLockGroups = LockGroupDaoImpl.get().getAllLockGroup();
    }

    @SuppressLint("CheckResult")
    public void loadLockGroup(boolean isShowTip) {
        service.getGroup()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> {
                            LockGroupDaoImpl.get().deleteAll();
                            LockGroupDaoImpl.get().insertAll(listResult.data);
                            loadLockGroupState.postValue(true);
                        },
                           new ErrorHandler(){
                               @Override
                               public void accept(Throwable throwable) {
                                   super.accept(throwable);
                                   loadLockGroupState.postValue(false);
                               }
                           }
                        );

    }

    @SuppressLint("CheckResult")
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
                        new ErrorHandler());
    }
}
