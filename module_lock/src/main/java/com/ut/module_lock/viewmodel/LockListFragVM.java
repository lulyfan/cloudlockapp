package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.database.dao.LockKeyDao;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockKey;

import java.util.Arrays;
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
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LockListFragVM(@NonNull Application application) {
        super(application);
        mLockList = LockKeyDaoImpl.get().getAllLockKey();
    }

    public LiveData<List<LockKey>> getLockList() {
        return mLockList;
    }

    public void toGetLockList() {
        Disposable disposable = CommonApi.pageUserLock(1, -1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockKeyResults -> {
                    List<LockKey> list = lockKeyResults.getData();
                    LockKey[] lockKeys = new LockKey[list.size()];
                    LockKeyDaoImpl.get().insert(list.toArray(lockKeys));
                }, throwable -> {
                    //TODO 获取锁列表失败处理
                });
        mCompositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
    }
}
