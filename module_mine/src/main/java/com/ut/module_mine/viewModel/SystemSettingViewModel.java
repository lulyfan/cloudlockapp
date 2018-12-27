package com.ut.module_mine.viewModel;

import android.app.Application;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.entity.base.Result;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_mine.R;

import io.reactivex.functions.Consumer;

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
                            CloudLockDatabaseHolder.get().getUserDao().deleteAllUsers();
                            CloudLockDatabaseHolder.get().getUUIDDao().deleteUUID();
                            ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
                            BaseApplication.deleteJpushAlias();
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }
}
