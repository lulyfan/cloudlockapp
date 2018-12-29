package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ut.database.daoImpl.LockUserKeyDaoImpl;
import com.ut.database.entity.LockUserKey;
import com.ut.module_mine.R;

import java.util.List;

public class LockUserItemViewModel extends BaseViewModel {
    private int mCurrentPage = -1;
    private static final int PAGE_SIZE = 10;
    public long userId;
    public MutableLiveData<Boolean> loadLockUserKeyState = new MutableLiveData<>();

    public MutableLiveData<List<LockUserKey>> mLockUserKeys = new MutableLiveData<>();

    public LockUserItemViewModel(@NonNull Application application) {
        super(application);
        LockUserKeyDaoImpl.get().getAll()
                .observeForever(lockUserKeys -> mLockUserKeys.postValue(lockUserKeys));
    }

    public void loadLockUserKey() {

        service.pageLockUserKey(userId, mCurrentPage, PAGE_SIZE)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> {
                            LockUserKeyDaoImpl.get().deleteAll();
                            LockUserKeyDaoImpl.get().insert(listResult.data);
                            loadLockUserKeyState.postValue(true);
                        },
                        throwable -> {
                            tip.postValue(throwable.getMessage());
                            loadLockUserKeyState.postValue(false);
                        });
    }

    public void deleteKey(long keyId) {
        service.deleteKey(keyId, 0)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> {
                            tip.postValue(voidResult.msg);
                            LockUserKeyDaoImpl.get().deleteById((int) keyId);
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
