package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.entity.base.Result;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockKey;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class ChangeLockPermissionViewModel extends BaseViewModel {

    private int currentPage = 1;
    private int pageSize = 10;
    public MutableLiveData<List<LockKey>> locks = new MutableLiveData<>();

    public LiveData<List<LockKey>> lockKeyListLiveData = null;

    public ChangeLockPermissionViewModel(@NonNull Application application) {
        super(application);
        lockKeyListLiveData = LockKeyDaoImpl.get().getAdminLock();
        lockKeyListLiveData.observeForever(mLockKeyObserver);
    }

    private Observer<List<LockKey>> mLockKeyObserver = lockKeys -> locks.setValue(lockKeys);

    @Override
    protected void onCleared() {
        super.onCleared();
        lockKeyListLiveData.removeObserver(mLockKeyObserver);
    }
}
