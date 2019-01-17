package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reactivex.disposables.CompositeDisposable;

/**
 * author : zhouyubin
 * time   : 2019/01/08
 * desc   :
 * version: 1.0
 */
public class BaseViewModel extends AndroidViewModel {
    protected ScheduledExecutorService mExecutorService = Executors.newSingleThreadScheduledExecutor();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MutableLiveData<String> showTip = new MutableLiveData<>();
    private MutableLiveData<Boolean> showDialog = new MutableLiveData<>();

    public MutableLiveData<String> getShowTip() {
        return showTip;
    }

    public MutableLiveData<Boolean> getShowDialog() {
        return showDialog;
    }

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
        mExecutorService.shutdown();
    }
}
