package com.ut.base.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.R;

import io.reactivex.functions.Consumer;

public class GrantPermisssionViewModel extends AndroidViewModel {

    private CommonApiService service;
    public MutableLiveData<String> tip = new MutableLiveData<>();
    public static final int KEY_TYPE_FOREVER = 1;
    public static final int KEY_TYPE_LIMI_TTIME = 2;
    public static final int KEY_TYPE_ONCE = 3;
    public static final int KEY_TYPE_LOOP = 4;

    public MutableLiveData<String> receiverPhoneNum = new MutableLiveData<>();
    public MutableLiveData<String> keyName = new MutableLiveData<>();
    public boolean isAdmin;
    public String startTime;
    public String endTime;
    public String startTimeRange;
    public String endTimeRange;
    public String weeks;   //用于循环钥匙

    public GrantPermisssionViewModel(@NonNull Application application) {
        super(application);
        service = MyRetrofit.get().getCommonApiService();
    }


    public void sendForeverKey(String phoneNum, String mac, String keyName, boolean isAdmin) {
        String sIsAdmin = isAdmin ? "1" : "0";
        sendKey(phoneNum, mac, keyName, KEY_TYPE_FOREVER, sIsAdmin, null, null,
                null, null, null);
    }

    public void sendLimitTimeKey(String phoneNum, String mac, String keyName, String startTime, String endTime) {
        sendKey(phoneNum, mac, keyName, KEY_TYPE_LIMI_TTIME, null, startTime, endTime,
                null, null, null);
    }

    public void sendOnceKey(String phoneNum, String mac, String keyName) {
        sendKey(phoneNum, mac, keyName, KEY_TYPE_ONCE, null, null, null,
                null, null, null);
    }

    public void sendLoopKey(String phoneNum, String mac, String keyName, String startTime, String endTime, String weeks,
                            String startTimeRange, String endTimeRange) {
        sendKey(phoneNum, mac, keyName, KEY_TYPE_LOOP, null, startTime, endTime, weeks, startTimeRange, endTimeRange);
    }

    public void sendKey(String phoneNum, String mac, String keyName, int keyType, String isAdmin, String startTime, String endTime,
                        String weeks, String startTimeRange, String endTimeRange) {
        service.sendKey(phoneNum, mac, keyType, keyName, isAdmin,startTime, endTime, weeks, startTimeRange, endTimeRange)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> tip.postValue(voidResult.msg),
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public TextWatcher receiverPhoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            receiverPhoneNum.setValue(s.toString());
        }
    };

    public TextWatcher keyNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            keyName.setValue(s.toString());
        }
    };


}
