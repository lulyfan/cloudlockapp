package com.ut.module_mine.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ut.base.ErrorHandler;
import com.ut.database.daoImpl.LockUserKeyDaoImpl;
import com.ut.database.entity.LockUserKey;
import com.ut.module_mine.R;

import java.util.List;

public class LockUserItemViewModel extends BaseViewModel {
    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = -1;
    public long userId;
    public MutableLiveData<Boolean> loadLockUserKeyState = new MutableLiveData<>();
    public LiveData<List<LockUserKey>> mLockUserKeys;

    public LockUserItemViewModel(@NonNull Application application) {
        super(application);
        mLockUserKeys = LockUserKeyDaoImpl.get().getAll();
    }

    @SuppressLint("CheckResult")
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
                            new ErrorHandler() {
                                @Override
                                public void accept(Throwable throwable) {
                                    super.accept(throwable);
                                    loadLockUserKeyState.postValue(false);
                                }
                            });
    }

    @SuppressLint("CheckResult")
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
                        }, new ErrorHandler());
    }
}
