package com.ut.module_mine.viewModel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Switch;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.base.Utils.UTLog;
import com.ut.database.entity.User;
import com.ut.module_mine.Constant;
import com.ut.module_mine.R;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MineViewModel extends BaseViewModel {

    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> headImgUrl = new ObservableField<>();
    public ObservableField<Boolean> isWebLoginEnable = new ObservableField<>();
    public ObservableField<Boolean> isOpenLockVolumeEnable = new ObservableField<>();

    public MineViewModel(@NonNull Application application) {
        super(application);

        isWebLoginEnable.set(user.enableWebLogin == 1);
        isOpenLockVolumeEnable.set(user.enableSound == 1);

        isWebLoginEnable.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                Log.d("debug", "onPropertyChanged isWebLoginEnable");
                enableWebLogin(isWebLoginEnable.get());
            }
        });

        isOpenLockVolumeEnable.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                Log.d("debug","onPropertyChanged isOpenLockVolumeEnable");
                enableOpenLockVolume(isOpenLockVolumeEnable.get());
            }
        });
    }

    public void getUserInfo() {
        User user = BaseApplication.getUser();

        String mobile = user.getAccount();
        if (mobile != null && mobile.length() >= 11) {
            String first = mobile.substring(0, 3) + " ";
            String second = mobile.substring(3, 7) + " ";
            String third = mobile.substring(7, 11);
            mobile = first + second + third;
        }

        phoneNum.set(mobile);
        userName.set(user.getName());
        headImgUrl.set(user.getHeadPic());
    }

    public void enableWebLogin(boolean isEnable) {
        changeUserConfig(Constant.CONFIG_TYPE_WEB_LOGIN, isEnable);
    }

    public void enableOpenLockVolume(boolean isEnable) {
        changeUserConfig(Constant.CONFIG_TYPE_ENABLE_VOLUME, isEnable);
    }

    public void changeUserConfig(String type, boolean enable) {
        String operval = enable ? "1" : "0";
        service.changeUserConfig(type, operval)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> {
                    Log.d("debug", voidResult.msg);
                    tip.postValue(voidResult.msg);
                    if (Constant.CONFIG_TYPE_WEB_LOGIN.equals(type)) {
                        user.enableWebLogin = enable ? 1 : 0;
                    } else if (Constant.CONFIG_TYPE_ENABLE_VOLUME.equals(type)) {
                        user.enableSound = enable ? 1 : 0;
                    }
                },
                    throwable -> {
                        Log.d("debug", throwable.getMessage());
                        tip.postValue(throwable.getMessage());

                });
    }
}
