package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.Switch;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.database.entity.User;
import com.ut.module_mine.Constant;
import com.ut.module_mine.R;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MineViewModel extends AndroidViewModel {

    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> headImgUrl = new ObservableField<>();
    public MutableLiveData<String> tip = new MutableLiveData<>();
    public ObservableField<Boolean> isWebLoginEnable = new ObservableField<>();
    public ObservableField<Boolean> isOpenLockVolumeEnable = new ObservableField<>();
    private CommonApiService service;
    private User user;

    public MineViewModel(@NonNull Application application) {
        super(application);
        service = MyRetrofit.get().getCommonApiService();
        user = BaseApplication.getUser();
    }

    public void getUserInfo() {
        User user = BaseApplication.getUser();
        phoneNum.set(user.getAccount());
        userName.set(user.getName());
        headImgUrl.set(user.getHeadPic());
        isWebLoginEnable.set(user.enableWebLogin == 1 ? true : false);
        isOpenLockVolumeEnable.set(user.enableSound == 1 ? true : false);
    }

    public void enableWebLogin() {
        changeUserConfig(Constant.CONFIG_TYPE_WEB_LOGIN, isWebLoginEnable.get());
    }

    public void enableOpenLockVolume() {
        changeUserConfig(Constant.CONFIG_TYPE_ENABLE_VOLUME, isOpenLockVolumeEnable.get());
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
                .subscribe(voidResult -> tip.postValue(voidResult.msg),
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void initWebLoginSwitchState(Switch switchView) {
        switchView.setChecked(user.enableWebLogin == 1 ? true : false);
    }

    public void initOpenLockVolumeSwitchState(Switch switchView) {
        switchView.setChecked(user.enableSound == 1 ? true : false);
    }

//    public void getHeadImgUrl() {
//        SharedPreferences sharedPreferences = getApplication()
//                .getSharedPreferences(Constant.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//        String remoteHeadImgUrl = sharedPreferences.getString(Constant.KEY_HEAD_IMG_REMOTE, null);
//        String localHeadImgUrl = sharedPreferences.getString(Constant.KEY_HEAD_IMG_LOCAL, null);
//
//        if (remoteHeadImgUrl == null) {
//
//            if (localHeadImgUrl != null) {
//                headImgUrl.set(localHeadImgUrl);
//            }
//        } else {
//
//            if (remoteHeadImgUrl.equals(localHeadImgUrl)) {
//                headImgUrl.set(localHeadImgUrl);
//            } else {
//                headImgUrl.set(remoteHeadImgUrl);
//            }
//        }
//    }
}
