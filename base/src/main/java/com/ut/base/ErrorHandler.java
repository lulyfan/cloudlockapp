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
            CLToast.showAtBottom(BaseApplication.getAppContext(), "网络连接超时");
        } else if (throwable instanceof ConnectException) {
            CLToast.showAtCenter(AppManager.getAppManager().currentActivity(), "当前网络不可用， 请连接网络");
        }

        throwable.printStackTrace();
    }
}
