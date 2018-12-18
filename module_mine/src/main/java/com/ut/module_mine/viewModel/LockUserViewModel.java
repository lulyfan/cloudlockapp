package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.database.entity.Lock;
import com.ut.module_mine.R;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class LockUserViewModel extends BaseViewModel {

    public LockUserViewModel(@NonNull Application application) {
        super(application);
    }

    public void loadLockUser(int currentPage, int pageSize) {

    }
}
