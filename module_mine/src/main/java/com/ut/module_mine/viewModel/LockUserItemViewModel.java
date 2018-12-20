package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.ut.database.entity.LockUserKey;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.functions.Consumer;

public class LockUserItemViewModel extends BaseViewModel{
    private int mCurrentPage;
    private static final int PAGE_SIZE = 10;
    public long userId;

    public MutableLiveData<List<LockUserKey>> lockUserKeys = new MutableLiveData<>();

    public LockUserItemViewModel(@NonNull Application application) {
        super(application);
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
                .subscribe(listResult -> lockUserKeys.postValue(listResult.data),
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void deleteKey(long keyId) {
        service.deleteKey(keyId)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> tip.postValue(voidResult.msg),
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
