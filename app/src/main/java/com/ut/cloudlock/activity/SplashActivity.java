package com.ut.cloudlock.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
public class SplashActivity extends AppCompatActivity {
    Handler mHandler = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setLightStatusBar();
        getWindow().setBackgroundDrawableResource(R.mipmap.splash);
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 100:
                        ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                        break;
                    case 200:
                        ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
                        break;
                }
                finish();
            }
        };
        mHandler.sendEmptyMessageDelayed(100, 2000L);
        UserRepository.getInstance().getAllUser().observe(this, users -> {
            if (users == null || users.size() < 1) {
                mHandler.removeMessages(100);
                mHandler.sendEmptyMessageDelayed(200, 800);
            }
        });
    }
}
