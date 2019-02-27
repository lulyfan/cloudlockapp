package com.ut.base.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.activity.SafeVerifyActivity;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2019/1/10
 * desc   :
 */
@SuppressLint("CheckResult")
public class SafeVerifyVm extends AndroidViewModel {

    public SafeVerifyVm(@NonNull Application application) {
        super(application);
    }

    public void obtainVerifyCode(String phone) {
        MyRetrofit.get().getCommonApiService().sendMobileCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
    }

    public void verifyCodeAndLogin(String phone, String code) {
        MyRetrofit.get().getCommonApiService().loginByCode(phone, code, SystemUtils.getMacAddress())
                .subscribeOn(Schedulers.io())
                .map(result -> {
                            if (result.isSuccess()) {
                                User user = result.data;
                                deleteAllOldData();
                                CloudLockDatabaseHolder.get().getUserDao().insertUser(user);
                            }
                            return result;
                        }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        AppManager.getAppManager().finishAllActivity();
                        ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                    } else {
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }
                });
    }

    private void deleteAllOldData() {
        CloudLockDatabaseHolder holder = CloudLockDatabaseHolder.get();
        holder.getUserDao().deleteAllUsers();
        holder.getLockGroupDao().deleteAll();
        holder.getLockMessageInfoDao().deleteAll();
        holder.getLockMessageDao().deleteAll();
        holder.getLockUserDao().deleteAll();
        holder.getLockKeyDao().deleteAll();
        holder.getKeyDao().deleteAll();
        holder.recordDao().deleteAll();
    }

}
