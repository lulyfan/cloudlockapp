package com.ut.module_mine.viewModel;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.User;
import com.ut.module_mine.GlobalData;
import com.ut.module_mine.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class ConfirmChangePermissionViewModel extends BaseViewModel {

    private static final int TIME = 20;
    public ObservableField<String> receiverPhone = new ObservableField<>();
    public ObservableField<String> receiverName = new ObservableField<>();
    public ObservableField<String> receiverHeadImgUrl = new ObservableField<>();

    public ConfirmChangePermissionViewModel(@NonNull Application application) {
        super(application);

        String mobile = GlobalData.getInstance().receiverPhone;
        if (mobile != null && mobile.length() >= 11) {
            String first = mobile.substring(0, 3) + " ";
            String second = mobile.substring(3, 7) + " ";
            String third = mobile.substring(7, 11);
            mobile = first + second + third;
        }
        receiverPhone.set(mobile);
    }

    public void getChangeAdminCode() {
        service.getChangeAdminCode()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> tip.postValue(voidResult.msg),
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void changeLockAdmin(String verifyCode) {
        String macs = GlobalData.getInstance().changeLockMacs;
        String phoneNum = receiverPhone.get();

        service.changeLockAdmin(macs, phoneNum, verifyCode)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(voidResult -> {
                            tip.postValue(voidResult.msg);
                            String[] macArray = macs.split(",");
                            for (String mac : macArray) {
                                LockKeyDaoImpl.get().deleteByMac(mac);
                            }
                            ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                        },
                        throwable -> tip.postValue(throwable.getMessage()));
    }

    public void startCount(TextView textView) {
        textView.setEnabled(false);
        Observable.timer(1, TimeUnit.SECONDS)
                .intervalRange(0, TIME - 1, 0, 1, TimeUnit.SECONDS)
                .observeOn(BaseApplication.getUiScheduler())
                .doOnComplete(() -> {
                    textView.setText(getApplication().getString(R.string.sendAgain));
                    textView.setEnabled(true);
                })
                .subscribe(aLong -> textView.setText((TIME - aLong) + "s"));
    }

    public void getUserInfoByMobile() {
        service.getUserInfoByMobile(GlobalData.getInstance().receiverPhone)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException(getApplication().getString(R.string.serviceErr));
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(userResult -> {
                    User user = userResult.data;
                    receiverName.set(user.getName());
                    receiverHeadImgUrl.set(user.getHeadPic());
                }, throwable -> tip.postValue(throwable.getMessage()));
    }
}
