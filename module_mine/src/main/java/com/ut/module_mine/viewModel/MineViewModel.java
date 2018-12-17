package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.ut.base.BaseApplication;
import com.ut.database.entity.User;
import com.ut.module_mine.Constant;

public class MineViewModel extends AndroidViewModel {

    public ObservableField<String> phoneNum = new ObservableField<>();
    public ObservableField<String> userName = new ObservableField<>();
    public ObservableField<String> headImgUrl = new ObservableField<>();

    public MineViewModel(@NonNull Application application) {
        super(application);
    }

    public void getUserInfo() {
        User user = BaseApplication.getUser();
        phoneNum.set(user.getAccount());
        userName.set(user.getName());
        headImgUrl.set(user.getHeadPic());
    }

    public void getHeadImgUrl() {
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
    }
}
