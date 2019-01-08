package com.ut.module_login.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_login.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2019/1/8
 * desc   :
 */
@SuppressLint("CheckResult")
public class LoginVm extends AndroidViewModel {
    public LoginVm(@NonNull Application application) {
        super(application);
    }

    public void login(String phone, String password) {
        MyRetrofit.get()
                .getCommonApiService()
                .login(phone, password)
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    if (result.isSuccess()) {
                        deleteAllOldData();
                        CloudLockDatabaseHolder.get().getUserDao().insertUser(result.data);
                    }
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        AppManager.getAppManager().currentActivity().finish();
                        ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                    } else {
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
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
    }

    public void getVerifyCode(String phone) {
        MyRetrofit.get().getCommonApiService().getRegisterVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> CLToast.showAtCenter(getApplication(), result.msg), new ErrorHandler());
    }

    public void register(String phone, String password, String verifyCode) {
        MyRetrofit.get()
                .getCommonApiService()
                .register(phone, password, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        login(phone, password);
                    }
                    CLToast.showAtCenter(getApplication(), result.msg);
                }, new ErrorHandler());
    }

    public void resetPassword(String phone, String password, String verifyCode) {
        MyRetrofit.get()
                .getCommonApiService()
                .resetPassword(phone, password, verifyCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        new AlertDialog.Builder(AppManager.getAppManager().currentActivity())
                                .setMessage(getApplication().getString(R.string.login_pwd_modify_success))
                                .setPositiveButton(getApplication().getString(R.string.login_sure), ((dialog, which) -> AppManager.getAppManager().currentActivity().finish()))
                                .show();
                    } else {
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }

                }, new ErrorHandler());
    }

    public void getForgetPwdCode(String phone) {
        MyRetrofit.get().getCommonApiService().getForgetPwdVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtCenter(getApplication(), result.msg);
                }, new ErrorHandler());
    }
}
