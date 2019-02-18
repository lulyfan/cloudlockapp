package com.ut.module_mine.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

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

    @SuppressLint("CheckResult")
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

    public String getKeyStatusStr(int keyStatus) {
        //status 1,"发送中" 2,"冻结中" 3,"解除冻结中" 4,"删除中" 5,"授权中" 6,"取消授权中" 7,"修改中 8,"正常"  9,"已冻结" 10,"已删除" 11,"已失效" 12,"已过期"
        switch (keyStatus) {
            case 1:
                return getApplication().getString(R.string.status_sending);
            case 2:
                return getApplication().getString(R.string.status_frozening);
            case 3:
                return getApplication().getString(R.string.status_cancel_frozen);
            case 4:
                return getApplication().getString(R.string.status_delete);
            case 5:
                return getApplication().getString(R.string.status_authorize);
            case 6:
                return getApplication().getString(R.string.status_cancel_authorize);
            case 7:
                return getApplication().getString(R.string.status_fix);
            case 8:
                return getApplication().getString(R.string.status_normal);
            case 9:
                return getApplication().getString(R.string.status_has_frozen);
            case 10:
                return getApplication().getString(R.string.status_has_deleted);
            case 11:
                return getApplication().getString(R.string.status_has_invailed);
            case 12:
                return getApplication().getString(R.string.status_out_of_date);
        }
        return "";
    }

}
