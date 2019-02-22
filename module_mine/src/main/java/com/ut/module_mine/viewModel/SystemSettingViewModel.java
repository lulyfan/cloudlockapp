package com.ut.module_mine.viewModel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_mine.R;
import com.ut.module_mine.activity.MainActivity;
import com.ut.module_mine.activity.SystemSettingActivity;


public class SystemSettingViewModel extends BaseViewModel {

    public MutableLiveData<Void> logoutSuccess = new MutableLiveData<>();

    public SystemSettingViewModel(@NonNull Application application) {
        super(application);
    }

    public void logout() {
        service.logout()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> {
                            tip.postValue(voidResult.msg);
                            logoutSuccess.postValue(null);
                            BaseApplication.clearDataBase();
                            BaseApplication.clearDataWhenLogout();
//                            ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
                            ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).withString("phone", BaseApplication.getUser().account).navigation();
                            AppManager.getAppManager().finishActivity(SystemSettingActivity.class);
                        },
                        new ErrorHandler());
    }
}
