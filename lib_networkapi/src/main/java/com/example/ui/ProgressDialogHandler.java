package com.example.ui;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.utbike.testretrofit.R;

/**
 * Created by zhouyubin on 2017/9/22.
 */

public class ProgressDialogHandler extends Handler {
    private Context mContext;
    private CancelListener mCancelListener = new CancelListener() {
        @Override
        public void onCancel() {
            Log.i("ProgressDialog", "onCancel");
        }
    };
    private Dialog mProgressDialog;
    private boolean mCancelable;

    private TextView txtMessage;

    public final static int HANDLER_SHOWDIALOG = 1;
    public final static int HANDLER_DIMISSDIALOG = 2;

    public interface CancelListener {
        void onCancel();
    }

    public ProgressDialogHandler(Context context, CancelListener cancelListener, boolean cancelable) {
        mContext = context;
        mCancelListener = cancelListener;
        mCancelable = cancelable;
    }

    private void show(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new Dialog(mContext);
            View view = mProgressDialog.getLayoutInflater().inflate(R.layout.dialog_progress, null);
            txtMessage = (TextView) view.findViewById(R.id.txt_tip);
            mProgressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setContentView(view);
            mProgressDialog.setCancelable(mCancelable);
            if (mCancelable) {
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        if (mCancelListener != null)
                            mCancelListener.onCancel();
                    }
                });
            }
        }
        if (msg != null)
            txtMessage.setText(msg);
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dissmiss() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void showDialog(String obj){
        obtainMessage(HANDLER_SHOWDIALOG,obj).sendToTarget();
    }

    public void hideDialog(){
        sendEmptyMessage(HANDLER_DIMISSDIALOG);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_SHOWDIALOG:
                show((String) msg.obj);
                break;
            case HANDLER_DIMISSDIALOG:
                dissmiss();
                break;
        }
    }
}
