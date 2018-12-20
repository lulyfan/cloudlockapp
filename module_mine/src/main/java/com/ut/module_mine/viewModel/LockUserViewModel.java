package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockUser;
import com.ut.module_mine.R;

import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class LockUserViewModel extends BaseViewModel {
    private int mCurrentPage = 1;
    private static final int PAGE_SIZE = 10;
    public MutableLiveData<List<LockUser>> lockUsers = new MutableLiveData<>();

    public LockUserViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadLockUser() {
        service.pageLockUser(mCurrentPage, PAGE_SIZE)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> lockUsers.postValue(listResult.data),
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
