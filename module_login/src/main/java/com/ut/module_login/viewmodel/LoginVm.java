package com.ut.module_login.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_login.R;
import com.ut.module_login.common.LoginUtil;
import com.ut.module_login.ui.LoginActivity;
import com.ut.module_login.ui.RegisterActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void login(String phone, String password) {
        Disposable subscribe = MyRetrofit.get()
                .getCommonApiService()
                .login(phone, password, SystemUtils.getMacAddress())
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
                        ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                        finishLoginActs();
                    } else if (result.code == 411) {
                        ARouter.getInstance().build(RouterUtil.BaseModulePath.SAFEVERIFY).withString("phone", phone).navigation();
                    } else {
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    private void finishLoginActs() {
        AppManager.getAppManager().finishActivity(RegisterActivity.class);
        AppManager.getAppManager().finishActivity(LoginActivity.class);
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

    public void getVerifyCode(String phone, Consumer<Result<Void>> subscriber) {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().getRegisterVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    public void register(String phone, String password, String verifyCode) {
        Disposable subscribe = MyRetrofit.get()
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
        compositeDisposable.add(subscribe);
    }

    public void resetPassword(String phone, String password, String verifyCode) {
        Disposable subscribe = MyRetrofit.get()
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
        compositeDisposable.add(subscribe);
    }

    public void getForgetPwdCode(String phone, Consumer<Result<Void>> subscriber) {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().getForgetPwdVerifyCode(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    public int checkPhoneBg(String phone) {
        if (LoginUtil.isPhone(phone) || TextUtils.isEmpty(phone)) {
            return R.drawable.selector_highlight_case;
        }
        return R.drawable.selector_highlight_red;
    }

    public int checkPwdBg(String pwd) {
        if (LoginUtil.isPassword(pwd) || TextUtils.isEmpty(pwd)) {
            return R.drawable.selector_highlight_case;
        }
        return R.drawable.selector_highlight_red;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
