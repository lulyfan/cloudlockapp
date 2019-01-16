package com.ut.module_mine.viewModel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.VersionUpdateHelper;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_mine.R;
import com.ut.module_mine.activity.SystemSettingActivity;


public class SystemSettingViewModel extends BaseViewModel {
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
                            BaseApplication.clearDataWhenLogout();
                            ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
                            AppManager.getAppManager().finishActivity(SystemSettingActivity.class);
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void checkVersion(String currentVersion) {
    }
}
