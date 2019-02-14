package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
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
    private static final int PAGE_SIZE = -1;
    public MutableLiveData<List<LockUser>> mLockUsers = new MutableLiveData<>();
    public MutableLiveData<Boolean> loadLockUserState = new MutableLiveData<>();

    private LiveData<List<LockUser>> mLockUserListLiveData = null;

    public LockUserViewModel(@NonNull Application application) {
        super(application);
        mLockUserListLiveData = LockUserDaoImpl.get().getAll();
        mLockUserListLiveData.observeForever(mLockUserObserver);
    }

    private Observer<List<LockUser>> mLockUserObserver = lockUsers -> mLockUsers.postValue(lockUsers);

    public void loadLockUser(boolean isShowTip) {
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
                            LockUserDaoImpl.get().deleteAll();
                            LockUserDaoImpl.get().insert(listResult.data);
                            loadLockUserState.postValue(true);
                        },
                        throwable -> {
                            if (isShowTip) {
                                tip.postValue(throwable.getMessage());
                            }
                            loadLockUserState.postValue(false);
                        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mLockUserListLiveData.removeObserver(mLockUserObserver);
    }
}
