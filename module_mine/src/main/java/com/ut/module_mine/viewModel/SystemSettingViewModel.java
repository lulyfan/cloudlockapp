package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_mine.activity.SystemSettingActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class SystemSettingViewModel extends BaseViewModel {

    public MutableLiveData<Void> logoutSuccess = new MutableLiveData<>();

    public SystemSettingViewModel(@NonNull Application application) {
        super(application);
    }

    public void logout() {
        Disposable subscribe = service.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(voidResult -> {
                            tip.postValue(voidResult.msg);
                            if (voidResult.isSuccess()) {
                                logoutSuccess.postValue(null);
                                BaseApplication.clearDataWhenLogout();
                                ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).withString("phone", BaseApplication.getUser().account).navigation();
                                AppManager.getAppManager().finishActivity(SystemSettingActivity.class);
                            }
                        },
                        new ErrorHandler());
    }
}
