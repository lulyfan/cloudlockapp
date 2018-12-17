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
import com.ut.base.Utils.UTLog;
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.entity.KeyItem;

import java.util.ArrayList;
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
        if (BaseApplication.getUser() == null) return;
        MyRetrofit.get().getCommonApiService()
                .pageKeys(BaseApplication.getUser().id, mac, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.newThread())
                .map(JsonElement::toString)
                .observeOn(AndroidSchedulers.mainThread())
                .map(json -> JSON.parseObject(json, new TypeReference<Result<List<KeyItem>>>() {
                }))
                .filter(result -> {
                    boolean success = result.isSuccess();
                    if (!success) {
                        UTLog.d(result.msg);
                    }
                    return success;
                })
                .subscribe(result -> {
                    Log.d("pageKeys", result.msg);
                    getKeys().postValue(result.data);
                    currentPage++;
                }, error -> error.printStackTrace());

        /**
         * 假数据
         */
        List<KeyItem> items = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            KeyItem item = new KeyItem();
            item.setKeyName("Chan的钥匙");
            item.setRuleType((i % 4 == 0 ? 4 : i % 4));
            item.setAuthorized((i) % 2 == 0);
            if (item.getRuleType() == 1) {
                item.setDesc("此钥匙没有使用次数限制");
            } else if (item.getRuleType() == 4) {
                item.setDesc("2018/11/26-2018/12/26  每天  9:00-17:00");
            } else if (item.getRuleType() == 3) {
                item.setDesc("钥匙有效期为1小时，使用一次后失效");
            }
            item.setSender("曹哲君");
            item.setSendTime("2018/09/08 10:55");
            item.setAcceptTime("2018/09/08 11:34");
            item.setStartTime("2018/09/09 11:12");
            item.setEndTime("2018/11/11 12:00");
            if (i < 6) {
                item.setStatus(2);
            } else {
                item.setStatus(0);
            }
            item.setAuthorizedType("普通用户");
            items.add(item);
        }
        getKeys().postValue(items);
    }

    public void unFrozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .unForzenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), error -> error.printStackTrace());

    }

    public void frozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService().frozenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), error -> error.printStackTrace());
    }

    public void deleteKey(long keyId) {
        MyRetrofit.get().getCommonApiService().deleteKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), error -> error.printStackTrace());
    }
}
