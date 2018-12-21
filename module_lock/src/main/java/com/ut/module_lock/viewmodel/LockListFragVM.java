package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.database.dao.LockGroupDao_Impl;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/17
 * desc   :
 * version: 1.0
 */
public class LockListFragVM extends AndroidViewModel {
    private LiveData<List<LockKey>> mLockList = null;
    private LiveData<List<LockGroup>> mLockGroupList = null;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LockListFragVM(@NonNull Application application) {
        super(application);
        mLockList = LockKeyDaoImpl.get().getAllLockKey();
        mLockGroupList = LockGroupDaoImpl.get().getAllLockGroup();
    }

    public LiveData<List<LockKey>> getLockList() {
        return mLockList;
    }

    public LiveData<List<LockGroup>> getLockGroupList() {
        return mLockGroupList;
    }

    public void toGetLockAllList() {
        Disposable disposable = CommonApi.pageUserLock(1, -1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockKeyResults -> {
                    List<LockKey> list = lockKeyResults.getData();
                    LockKey[] lockKeys = new LockKey[list.size()];
                    LockKeyDaoImpl.get().insertAll(list.toArray(lockKeys));
                }, throwable -> {
                    //TODO 获取锁列表失败处理
                });
        mCompositeDisposable.add(disposable);
    }

    public void toGetAllGroupList() {
        Disposable disposable = CommonApi.getGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockGroupResult -> {
                    List<LockGroup> list = lockGroupResult.getData();
                    LockGroup[] lockGroups = new LockGroup[list.size()];
                    LockGroupDaoImpl.get().insertAll(lockGroups);
                }, throwable -> {
                    //TODO
                });
        mCompositeDisposable.add(disposable);
    }

    public LiveData<List<LockKey>> toGetGroupLockList(LockGroup lockGroup) {
        if (lockGroup.getId() == -1){
            return LockKeyDaoImpl.get().getAllLockKey();
        }else{
            return LockKeyDaoImpl.get().getLockByGroupId(lockGroup.getId());
        }
    }


    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
    }
}
