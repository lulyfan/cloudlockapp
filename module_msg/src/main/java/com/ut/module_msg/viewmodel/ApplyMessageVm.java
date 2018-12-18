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
import com.google.gson.JsonElement;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.UTLog;
import com.ut.commoncomponent.CLToast;
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
                .map(jsonObject -> {
                    String json = jsonObject.toString();
                    Result<List<ApplyMessage>> result = JSON.parseObject(json, new TypeReference<Result<List<ApplyMessage>>>() {
                    });
                    return result;
                })
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        List<ApplyMessage> ams = result.data;
                        applyMessages.setValue(ams);
                    }
                    UTLog.d(String.valueOf(result.toString()));
                }, new ErrorHandler());
    }

    public void ignoreApply(long applyId) {
        if (BaseApplication.getUser() == null) return;
        MyRetrofit.get().getCommonApiService().ignoreApply(BaseApplication.getUser().id, applyId)
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    Log.d("ignoreApply", result.msg);
                    CLToast.showAtBottom(getApplication(), result.msg);
                }, new ErrorHandler());
    }
}
