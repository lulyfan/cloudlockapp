package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.google.gson.JsonElement;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.entity.KeyItem;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@SuppressLint("CheckResult")
public class KeyManagerVM extends AndroidViewModel {
    private MutableLiveData<List<KeyItem>> keys = null;
    private MutableLiveData<String> feedbackMessage = null;
    private int currentPage = 0;
    private static int DEFAULT_PAGE_SIZE = 10;
    private String mac;

    public KeyManagerVM(@NonNull Application application) {
        super(application);
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public MutableLiveData<List<KeyItem>> getKeys() {
        if (keys == null) {
            keys = new MutableLiveData<>();
            loadKeyItems();
        }
        return keys;
    }

    public MutableLiveData<String> getFeedbackMessage() {
        if (feedbackMessage == null) {
            feedbackMessage = new MutableLiveData<>();
        }
        return feedbackMessage;
    }

    public void loadKeyItems() {
        MyRetrofit.get().getCommonApiService()
                .pageKeys(BaseApplication.getUser().id, mac, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(jsonObject -> {
                    String json = jsonObject.toString();
                    return JSON.parseObject(json, new TypeReference<Result<List<KeyItem>>>() {
                    });
                })
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        if (!result.data.isEmpty()) {
                            getKeys().postValue(result.data);
                            currentPage++;
                        }
                    } else {
                        CLToast.showAtBottom(getApplication(), result.msg);
                    }
                    Log.d("pageKeys", result.msg);
                }, new ErrorHandler());
    }

    public void updateKeyItems() {
        MyRetrofit.get().getCommonApiService()
                .pageKeys(BaseApplication.getUser().id, mac, 0, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .map(JsonElement::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .map(json -> JSON.parseObject(json, new TypeReference<Result<List<KeyItem>>>() {
                }))
                .subscribe( result -> {
                    Log.d("pageKeys", result.msg);
                    if (result.isSuccess()) {
                        if (!result.data.isEmpty()) {
                            getKeys().postValue(result.data);
                            currentPage++;
                        }
                    } else {
                        CLToast.showAtBottom(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
    }


    public void unFrozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .unFrozenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), new ErrorHandler());

    }

    public void frozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService().frozenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), new ErrorHandler());
    }

    public void deleteKey(long keyId) {
        MyRetrofit.get().getCommonApiService().deleteKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), new ErrorHandler());
    }

    public int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public void editKey(KeyItem keyItem) {
        //ToDo
        MyRetrofit.get().getCommonApiService().editKey("33-33-22-A1-B0-34", keyItem.getKeyId(), keyItem.getStartTime(), keyItem.getEndTime(), keyItem.getWeeks(), keyItem.getStartTimeRange(), keyItem.getEndTimeRange())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtBottom(getApplication(), result.msg);
                }, new ErrorHandler());
    }
}
