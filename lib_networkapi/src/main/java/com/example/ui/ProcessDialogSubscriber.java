package com.example.ui;

import android.content.Context;

import rx.Subscriber;

/**
 * Created by zhouyubin on 2017/9/22.
 */

public class ProcessDialogSubscriber<T> extends Subscriber<T> implements ProgressDialogHandler.CancelListener {
    private ProgressDialogHandler mProgressDialogHandler;
    private Context mContext;
    private OnextListener<T> mOnnextListener = new OnextListener<T>() {
        @Override
        public void onnext(T t) {
        }
    };

    public interface OnextListener<T> {
        void onnext(T t);
    }

    public ProcessDialogSubscriber(Context context, OnextListener<T> onextListener, boolean cancelable) {
        mContext = context;
        mOnnextListener = onextListener;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, cancelable);
    }


    private void showDialog() {
        mProgressDialogHandler.obtainMessage(ProgressDialogHandler.HANDLER_SHOWDIALOG, mContext.getString(com.utbike.testretrofit.R.string.progress_tip)).sendToTarget();
    }

    private void dimissDialog() {
        mProgressDialogHandler.sendEmptyMessage(ProgressDialogHandler.HANDLER_DIMISSDIALOG);
        mProgressDialogHandler = null;
    }

    @Override
    public void onStart() {
        System.out.println("====onStart thread id:" + Thread.currentThread().getId());
        showDialog();
    }

    @Override
    public void onCompleted() {
        System.out.println("====onCompleted");
        dimissDialog();
    }

    @Override
    public void onError(Throwable e) {
        System.out.println("====onError");
        dimissDialog();
    }

    @Override
    public void onNext(T o) {
        mOnnextListener.onnext(o);
    }

    @Override
    public void onCancel() {
        if (!isUnsubscribed()) {
            unsubscribe();
        }
    }
}
