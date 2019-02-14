package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockKey;
import com.ut.module_mine.R;

import java.util.List;

public class LockGroupItemViewModel extends BaseViewModel {

    public long groupId = -1;
    public MutableLiveData<List<LockKey>> locks = new MutableLiveData<>();
    public MutableLiveData<Void> delGroupSuccess = new MutableLiveData<>();
    public MutableLiveData<String> updateGroupName = new MutableLiveData<>();

    public LockGroupItemViewModel(@NonNull Application application) {
        super(application);
    }

    private LiveData<List<LockKey>> mlockKeysInGroupLiveData = null;

    public void getLockByGroupId() {
        mlockKeysInGroupLiveData = LockKeyDaoImpl.get().getLockByGroupId((int) groupId);
        mlockKeysInGroupLiveData.observeForever(mLockListObserver);
    }

    Observer<List<LockKey>> mLockListObserver = lockKeys -> {
        locks.setValue(lockKeys);
    };

    public void editGroupName(String groupName) {
        service.updateGroupName(groupId, groupName)
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
                            updateGroupName.postValue(groupName);
                            LockGroupDaoImpl.get().updateGroupName(groupId, groupName);
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void delGroup() {
        if (groupId < 0) {
            return;
        }

        service.delGroup(groupId)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> {
                            LockGroupDaoImpl.get().deleteById(groupId);
                            delGroupSuccess.postValue(null);
                            tip.postValue(voidResult.msg);
                            groupId = -1;

                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mlockKeysInGroupLiveData.removeObserver(mLockListObserver);
    }
}
