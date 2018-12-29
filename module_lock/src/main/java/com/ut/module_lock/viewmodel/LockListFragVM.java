package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
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
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    public LockListFragVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<LockKey>> getLockList() {
        return LockKeyDaoImpl.get().getAllLockKey();
    }

    public LiveData<List<LockGroup>> getLockGroupList() {
        return LockGroupDaoImpl.get().getAllLockGroup();
    }

    public void toGetLockAllList(boolean isReset) {
        Disposable disposable = CommonApi.pageUserLock(1, -1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockKeyResults -> {
                    List<LockKey> list = lockKeyResults.getData();
                    LockKey[] lockKeys = new LockKey[list.size()];
                    //TODO 先清除数据,后面再做优化
                    if (isReset) {
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockKeyDaoImpl.get().insertAll(list.toArray(lockKeys));
                }, throwable -> {
                    //TODO 获取锁列表失败处理
                });
        mCompositeDisposable.add(disposable);
    }


    public void toGetAllGroupList(boolean isReset) {
        Disposable disposable = CommonApi.getGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockGroupResult -> {
                    List<LockGroup> list = lockGroupResult.getData();
                    LockGroup[] lockGroups = new LockGroup[list.size()];
                    if (isReset) {//刷新时清除所有组数据
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockGroupDaoImpl.get().insertAll(list.toArray(lockGroups));
                }, throwable -> {
                    throwable.printStackTrace();
                    //TODO
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 获取分组的锁
     *
     * @param lockGroup
     * @return
     */
    public LiveData<List<LockKey>> getGroupLockList(LockGroup lockGroup) {
        if (lockGroup.getId() == -1) {
            return LockKeyDaoImpl.get().getAllLockKey();
        } else {
            return LockKeyDaoImpl.get().getLockByGroupId(lockGroup.getId());
        }
    }


    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
    }
}
