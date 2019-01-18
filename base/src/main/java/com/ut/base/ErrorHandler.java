package com.ut.base;

import android.widget.Toast;

import com.ut.commoncomponent.CLToast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import io.reactivex.functions.Consumer;

/**
 * author : chenjiajun
 * time   : 2018/12/17
 * desc   :
 */
public class ErrorHandler implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            BaseActivity baseActivity = AppManager.getAppManager().currentActivity();
            baseActivity.getMainHandler().post(()->{
                CLToast.showAtBottom(BaseApplication.getAppContext(), "网络连接超时");
            });
        } else if (throwable instanceof ConnectException) {
            BaseActivity baseActivity = AppManager.getAppManager().currentActivity();
            baseActivity.getMainHandler().post(()->{
                CLToast.showAtCenter(BaseApplication.getAppContext(), "当前网络不可用， 请连接网络");
            });
        }
        throwable.printStackTrace();
    }
}
