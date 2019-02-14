package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockGroup;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class LockGroupViewModel extends BaseViewModel {

    public MutableLiveData<List<LockGroup>> mLockGroups = new MutableLiveData<>();
    public MutableLiveData<Void> addGroupSuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> loadLockGroupState = new MutableLiveData<>();

    private LiveData<List<LockGroup>> mAllLockGroupLiveData = null;

    public LockGroupViewModel(@NonNull Application application) {
        super(application);
        mAllLockGroupLiveData = LockGroupDaoImpl.get().getAllLockGroup();
        mAllLockGroupLiveData.observeForever(mLockGroupsObserver);
    }

    Observer<List<LockGroup>> mLockGroupsObserver = lockGroups -> {
        mLockGroups.postValue(lockGroups);
    };


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
                            mLockGroups.postValue(listResult.data);
                            loadLockGroupState.postValue(true);
                            LockGroupDaoImpl.get().deleteAll();
                            LockGroupDaoImpl.get().insertAll(listResult.data);
                        },
                        throwable -> {
                            if (isShowTip) {
                                tip.postValue(throwable.getMessage());
                            }
                            loadLockGroupState.postValue(false);
                        });

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

    @Override
    protected void onCleared() {
        super.onCleared();
        mAllLockGroupLiveData.removeObserver(mLockGroupsObserver);
    }
}
