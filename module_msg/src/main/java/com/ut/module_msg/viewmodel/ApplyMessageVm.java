package com.ut.module_msg.viewmodel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.base.dialog.DialogHelper;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_msg.R;
import com.ut.database.entity.ApplyMessage;
import com.ut.unilink.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/12
 * desc   :
 */

@SuppressLint("CheckResult")
public class ApplyMessageVm extends AndroidViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<List<ApplyMessage>> applyMessages = null;

    public ApplyMessageVm(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ApplyMessage>> getApplyMessages() {
        return CloudLockDatabaseHolder.get().getApplyMessageDao().loadApplyMessages();
    }

    public void loadApplyMessages() {
        Disposable subscribe = MyRetrofit.get()
                .getCommonApiService()
                .getKeyApplyList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        saveApplyMessages(result.data);
                    }
                    UTLog.d(String.valueOf(result.toString()));
                }, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    public void ignoreApply(long applyId) {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().ignoreApply(BaseApplication.getUser().id, applyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtBottom(getApplication(), result.msg);
                    AppManager.getAppManager().currentActivity().finish();
                }, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    public void checkApplyStatus(ApplyMessage message) {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().checkKeyStatus(message.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    String json = jsonObject.toString();
                    JSONObject obj = JSON.parseObject(json);
                    Integer code = obj.getInteger("code");
                    String msg = obj.getString("msg");
                    if (code == 200) {
                        ARouter.getInstance().build(RouterUtil.MsgModulePath.APPLY_INFO).withSerializable("applyMessage", message).navigation();
                    } else if (code == 203021) {
                        JSONObject data = obj.getJSONObject("data");
                        String dealer = data.getString("dealUser");
                        long dealTime = data.getLong("dealTime");
                        String phone = data.getString("dealMobile");
                        if (BaseApplication.getUser().account.equals(phone)) {
                            ARouter.getInstance().build(RouterUtil.MsgModulePath.APPLY_INFO).withBoolean("hasDealt", true).withSerializable("applyMessage", message).navigation();
                        } else {
                            String format = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault()).format(new Date(dealTime));
                            DialogHelper.getInstance()
                                    .setTitle(getApplication().getString(R.string.msg_tip))
                                    .setMessage(dealer + getApplication().getString(R.string.msg_has_at) + format + getApplication().getString(R.string.mg_handle_this_message))
                                    .setPositiveButton(getApplication().getString(R.string.msg_ok), null)
                                    .show();
                        }

                    } else {
                        CLToast.showAtCenter(getApplication(), msg);
                    }
                }, new ErrorHandler());
        compositeDisposable.add(subscribe);
    }

    public void saveApplyMessages(List<ApplyMessage> list) {
        Schedulers.io().scheduleDirect(() -> {
            CloudLockDatabaseHolder.get().getApplyMessageDao().deleteAll();
            CloudLockDatabaseHolder.get().getApplyMessageDao().insert(list);
        });
    }

    public void initApplyMessageString(ApplyMessage applyMessage) {
        applyMessage.setApplyTimeStr(applyTimeString(applyMessage));
        applyMessage.setRuleTypeStr(ruleTypeString(applyMessage));
        applyMessage.setDecStr(description(applyMessage));
    }

    public String applyTimeString(ApplyMessage applyMessage) {
        return new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault()).format(new Date(applyMessage.getApplyTime()));
    }

    public String ruleTypeString(ApplyMessage applyMessage) {
        switch (applyMessage.getRuleType()) {
            case 3:
                return getApplication().getString(R.string.once_time);
            case 2:
                return getApplication().getString(R.string.limit_time);
            case 4:
                return getApplication().getString(R.string.loop);
            case 1:
                return getApplication().getString(R.string.permanent);
        }
        return "";
    }

    public String description(ApplyMessage applyMessage) {
        String string = getApplication().getString(R.string.apply_desc);
        return String.format(string, applyMessage.getRuleTypeStr(), applyMessage.getLockName());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
