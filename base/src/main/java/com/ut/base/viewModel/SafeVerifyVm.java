package com.ut.base.viewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.commoncomponent.CLToast;

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
                });
    }

    public void verifyCode(String phone, String code, Consumer<Result<Void>> subscriber) {
        MyRetrofit.get().getCommonApiService().verifyPhoneCode(phone, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
