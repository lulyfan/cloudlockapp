package com.ut.cloudlock;

import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class MyApplication extends BaseApplication {


    private AlertDialog onLoginDialog;
    @Override
    public void onCreate() {
        super.onCreate();

        MyRetrofit.get().setNoLoginListener(() -> {
            Observable.just(RouterUtil.LoginModulePath.Login).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(url -> {
                if (onLoginDialog != null) onLoginDialog.dismiss();
                onLoginDialog = new AlertDialog.Builder(AppManager.getAppManager().currentActivity()).setTitle("还未登录").setMessage("请重新登录").setPositiveButton("好的", (dialog1, which) -> {
                    ARouter.getInstance().build(url).navigation();
                }).create();
                if (!onLoginDialog.isShowing()) {
                    onLoginDialog.show();
                }
            });
        });

//        MyRetrofit.get().setLoadingListener(new MyRetrofit.ILoadingListener() {
//            @Override
//            public void onLoadingStart() {
//                Disposable loading_start = Observable.just(getAppContext())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(c -> Toast.makeText(c, "loading start", Toast.LENGTH_SHORT).show());
//            }
//
//            @Override
//            public void onLoadingEnd() {
//                Disposable loading_start = Observable.just(getAppContext())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(c -> Toast.makeText(c, "loading end", Toast.LENGTH_SHORT).show());
//            }
//        });
    }
}
