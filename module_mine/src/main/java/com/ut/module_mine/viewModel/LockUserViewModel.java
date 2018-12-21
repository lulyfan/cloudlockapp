package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ut.database.daoImpl.LockUserDaoImpl;
import com.ut.database.entity.LockUser;
import com.ut.module_mine.R;

import java.util.List;

public class LockUserViewModel extends BaseViewModel {
    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = 10;
    public MutableLiveData<List<LockUser>> mLockUsers = new MutableLiveData<>();

    public LockUserViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadLockUser() {
        LockUserDaoImpl.get().getAll().observeForever(new Observer<List<LockUser>>() {
            @Override
            public void onChanged(@Nullable List<LockUser> lockUsers) {
                mLockUsers.postValue(lockUsers);
            }
        });

        service.pageLockUser(mCurrentPage, PAGE_SIZE)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> {
                            mLockUsers.postValue(listResult.data);
                            LockUserDaoImpl.get().insert(listResult.data);
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
