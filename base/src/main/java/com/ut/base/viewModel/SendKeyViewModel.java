package com.ut.base.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.ErrorHandler;
import com.ut.base.R;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SendKeyViewModel extends AndroidViewModel {

    private CommonApiService service;

    public static final int KEY_TYPE_FOREVER = 1;
    public static final int KEY_TYPE_LIMI_TTIME = 2;
    public static final int KEY_TYPE_ONCE = 3;
    public static final int KEY_TYPE_LOOP = 4;

    public MutableLiveData<String> tip = new MutableLiveData<>();
    public MutableLiveData<Boolean> isSendKeySuccess = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendingKey = new MutableLiveData<>();
    public MutableLiveData<String> receiverPhoneNum = new MutableLiveData<>();
    public MutableLiveData<String> keyName = new MutableLiveData<>();
    public boolean isAdmin;
    public MutableLiveData<String> limitStartTime = new MutableLiveData<>();
    public MutableLiveData<String> limitEndTime = new MutableLiveData<>();
    public MutableLiveData<String> loopStartTime = new MutableLiveData<>();
    public MutableLiveData<String> loopEndTime = new MutableLiveData<>();
    public MutableLiveData<String> startTimeRange = new MutableLiveData<>();
    public MutableLiveData<String> endTimeRange = new MutableLiveData<>();
    public MutableLiveData<String> weeks = new MutableLiveData<>();  //用于循环钥匙
    public String mac;
    public int userType = EnumCollection.UserType.ADMIN.ordinal();

    public SendKeyViewModel(@NonNull Application application) {
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

    @SuppressLint("CheckResult")
    public void sendKey(String phoneNum, String mac, String keyName, int keyType, String isAdmin, String startTime, String endTime,
                        String weeks, String startTimeRange, String endTimeRange) {
        sendingKey.postValue(true);
        service.sendKey(phoneNum, mac, keyType, keyName, isAdmin, startTime, endTime, weeks, startTimeRange, endTimeRange)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voidResult -> {
                    if (voidResult.isSuccess()) {
                        tip.postValue(voidResult.msg);
                        isSendKeySuccess.setValue(true);
                    } else {
                        CLToast.showAtCenter(getApplication(), voidResult.msg);
                        isSendKeySuccess.setValue(false);
                    }
                    sendingKey.postValue(false);
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        isSendKeySuccess.setValue(false);
                        sendingKey.postValue(false);
                    }
                });
    }

    private String afterChangedPhone = "";
    private String afterChangedName = "";

    public TextWatcher receiverPhoneWatcher = new TextWatcher() {
        CharSequence beforeChange = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            beforeChange = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            afterChangedPhone = s.toString();
            receiverPhoneNum.postValue(afterChangedPhone);
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
            afterChangedName = s.toString();
            keyName.postValue(afterChangedName);
        }
    };


    public void setPhoneAndName() {
        receiverPhoneNum.postValue(afterChangedPhone);
        keyName.postValue(afterChangedName);
    }


}
