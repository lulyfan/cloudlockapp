package com.ut.module_mine;

import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

public abstract class PreventShakeObserver<T> implements Observer<T>, MyObserver<T> {

    private MyHandler handler = new MyHandler(Looper.getMainLooper());
    private int mCurrentVersion;

    @Override
    public void onChanged(@Nullable T t) {
        mCurrentVersion++;
        Message msg = Message.obtain(handler, mCurrentVersion, t);
        handler.sendMessageDelayed(msg, 50);
    }

    private class MyHandler extends Handler{

        public MyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int version = msg.what;
            if (version == mCurrentVersion) {
                onChange((T) msg.obj);
            }
        }
    }
}
