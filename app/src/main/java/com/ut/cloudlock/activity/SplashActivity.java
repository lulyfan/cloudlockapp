package com.ut.cloudlock.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UserRepository;
import com.ut.cloudlock.R;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.User;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/12
 * desc   :
 */
@SuppressLint("Registered")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLightStatusBar();
        getWindow().setBackgroundDrawableResource(R.mipmap.splash);

        new Handler().postDelayed(() -> {
//            Observable.just(this).subscribeOn(Schedulers.io()).map(context -> {
//                List<User> allUsers = CloudLockDatabaseHolder.get().getUserDao().findAllUsers();
//                String url = null;
//                if (allUsers.isEmpty()) {
//                    url = RouterUtil.LoginModulePath.Login;
//                } else {
//                    User user = allUsers.get(allUsers.size() - 1);
//                    url = RouterUtil.MainModulePath.Main_Module;
//                    BaseApplication.setUser(user);
//                }
//                return url;
//            }).observeOn(AndroidSchedulers.mainThread()).subscribe(url -> {
//                ARouter.getInstance().build(url).navigation();
//                finish();
//            });

            ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
            finish();
        }, 2000L);
    }
}
