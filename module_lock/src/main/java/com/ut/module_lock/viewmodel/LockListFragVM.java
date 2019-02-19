package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.base.ErrorHandler;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/17
 * desc   :
 * version: 1.0
 */
public class LockListFragVM extends BaseViewModel {
    private MutableLiveData<Boolean> refreshStatus = new MutableLiveData<>();
    private MutableLiveData<List<LockKey>> mLockKeys = new MutableLiveData<>();
    private LiveData<List<LockKey>> lock1 = null;//全部分组的锁
    private LiveData<List<LockKey>> lock2 = null;//当前分组的锁
    private long currentGroupId = -1;

    public LockListFragVM(@NonNull Application application) {
        super(application);
        getLockKeyFromDb();//先从数据库获取数据
    }

    public MutableLiveData<Boolean> getRefreshStatus() {
        return refreshStatus;
    }

    public LiveData<List<LockKey>> getLockList() {
        return mLockKeys;
    }

    private Observer<List<LockKey>> mLockKeyListObserver1 = lockKeys -> {
        assert lockKeys != null;
        if (currentGroupId != -1) return;
        mLockKeys.setValue(lockKeys);
    };

    private void getLockKeyFromDb() {//监听数据库变化
        lock1 = LockKeyDaoImpl.get().getAllLockKey();
        lock1.observeForever(mLockKeyListObserver1);
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
                    if (isReset)
                        refreshStatus.postValue(true);
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        //TODO 获取锁列表失败处理
                        refreshStatus.postValue(false);
                    }
                });
        mCompositeDisposable.add(disposable);
    }


    public void toGetAllGroupList(boolean isReset) {
        Disposable disposable = CommonApi.getGroup()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(lockGroupResult -> {
                    List<LockGroup> list = lockGroupResult.getData();
                    if (list == null) return;
                    LockGroup[] lockGroups = new LockGroup[list.size()];
                    if (isReset) {//刷新时清除所有组数据
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockGroupDaoImpl.get().insertAll(list.toArray(lockGroups));
                    if (isReset)
                        refreshStatus.postValue(true);
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        //TODO 获取锁列表失败处理
                        refreshStatus.postValue(false);
                    }
                });
        mCompositeDisposable.add(disposable);
    }

    /**
     * 获取分组的锁
     *
     * @param lockGroup
     * @return
     */
    public void getGroupLockList(LockGroup lockGroup) {
        currentGroupId = lockGroup.getId();
        if (lockGroup.getId() == -1) {//当组id为-1时获取全部锁
            getLockKeyFromDb();
        } else {
            lock2 = LockKeyDaoImpl.get().getLockByGroupId(lockGroup.getId());
            lock2.observeForever(mLockKeyListObserver2);
        }
    }

    private Observer<List<LockKey>> mLockKeyListObserver2 = lockKeys -> {
        if (currentGroupId == -1 || lockKeys == null) return;
        mLockKeys.setValue(lockKeys);
    };


    public long getCurrentGroupId() {
        return currentGroupId;
    }

    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
        if (lock1 != null)
            lock1.removeObserver(mLockKeyListObserver1);
        if (lock2 != null)
            lock2.removeObserver(mLockKeyListObserver2);
    }

}
