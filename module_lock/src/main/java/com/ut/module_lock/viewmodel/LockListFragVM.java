package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.ut.commoncomponent.CLToast;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.Lock;
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
    private MutableLiveData<Boolean> refreshStatus = new MutableLiveData<>();
    private MutableLiveData<List<LockKey>> mLockKeys = new MutableLiveData<>();
    private volatile boolean mIsReset = false;
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

    private void getLockKeyFromDb() {//监听数据库变化
        lock1 = LockKeyDaoImpl.get().getAllLockKey();
        lock1.observeForever(lockKeys -> {
            assert lockKeys != null;
            if (currentGroupId != -1) return;
            if (mIsReset && lockKeys.size() < 1) return;
            mLockKeys.setValue(lockKeys);
        });
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
                        mIsReset = true;
                        LockKeyDaoImpl.get().deleteAll();
                    }
                    LockKeyDaoImpl.get().insertAll(list.toArray(lockKeys));
                    if (isReset)
                        refreshStatus.postValue(true);
                }, throwable -> {
                    //TODO 获取锁列表失败处理
                    refreshStatus.postValue(false);
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
                    if (isReset)
                        refreshStatus.postValue(true);
                }, throwable -> {
                    throwable.printStackTrace();
                    //TODO
                    refreshStatus.postValue(false);
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
            lock2.observeForever(lockKeys -> {
                if (currentGroupId == -1 || lockKeys == null) return;
                if (mIsReset && lockKeys.size() < 1) return;
                mLockKeys.setValue(lockKeys);
            });
        }
    }


    @Override

    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
//        lock1.removeObservers(getApplication());
//        lock2.removeObserver(getApplication());
    }


}
