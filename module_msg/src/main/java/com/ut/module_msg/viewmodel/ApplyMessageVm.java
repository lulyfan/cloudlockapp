package com.ut.module_msg.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.module_msg.model.ApplyMessage;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/12
 * desc   :
 */

@SuppressLint("CheckResult")
public class ApplyMessageVm extends AndroidViewModel {

    private MutableLiveData<List<ApplyMessage>> applyMessages = null;

    public ApplyMessageVm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<ApplyMessage>> getApplyMessages() {
        if (applyMessages == null) {
            applyMessages = new MutableLiveData<>();
            loadApplyMessages();
        }
        return applyMessages;
    }

    public void loadApplyMessages() {
        if (BaseApplication.getUser() == null) return;
        Disposable subscribe = MyRetrofit.get()
                .getCommonApiService()
                .getKeyApplyList(BaseApplication.getUser().id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(json -> {
                    Result<List<ApplyMessage>> result = JSON.parseObject(json,
                            new TypeReference<Result<List<ApplyMessage>>>() {
                            });
                    List<ApplyMessage> ams = result.data;
                    applyMessages.setValue(ams);
                    Log.d("result", result.msg);
                });
    }

    public void ignoreApply(long applyId) {
        if (BaseApplication.getUser() == null) return;
        MyRetrofit.get().getCommonApiService().ignoreApply(BaseApplication.getUser().id, applyId)
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    Log.d("ignoreApply", result.msg);
                });
    }
}
