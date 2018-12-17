package com.ut.base;

import android.widget.Toast;

import java.net.SocketException;

import io.reactivex.functions.Consumer;

/**
 * author : chenjiajun
 * time   : 2018/12/17
 * desc   :
 */
public class ErrorHandler implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) throws Exception {
        if(throwable instanceof SocketException) {
            Toast.makeText(BaseApplication.getAppContext(), "网络连接超时",Toast.LENGTH_SHORT).show();
        }
    }
}
