package com.ut.module_msg.viewmodel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.google.gson.JsonElement;
import com.ut.base.AppManager;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.commoncomponent.CLToast;
import com.ut.module_msg.model.ApplyMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/12
 * desc   :
 */

@SuppressLint("CheckResult")
public class ApplyMessageVm extends AndroidViewModel {

    private MutableLiveData<List<ApplyMessage>> applyMessages = null;

    public ApplyMessageVm(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<ApplyMessage>> getApplyMessages() {
        if (applyMessages == null) {
            applyMessages = new MutableLiveData<>();
            loadApplyMessages();
        }
        return applyMessages;
    }

    public void loadApplyMessages() {
        Disposable subscribe = MyRetrofit.get()
                .getCommonApiService()
                .getKeyApplyList(BaseApplication.getUser().id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(jsonObject -> {
                    String json = jsonObject.toString();
                    Result<List<ApplyMessage>> result = JSON.parseObject(json, new TypeReference<Result<List<ApplyMessage>>>() {
                    });
                    return result;
                })
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        applyMessages.setValue(result.data);
                    }
                    UTLog.d(String.valueOf(result.toString()));
                }, new ErrorHandler());
    }

    public void ignoreApply(long applyId) {
        if (BaseApplication.getUser() == null) return;
        MyRetrofit.get().getCommonApiService().ignoreApply(BaseApplication.getUser().id, applyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.d("ignoreApply", result.msg);
                    CLToast.showAtBottom(getApplication(), result.msg);
                    AppManager.getAppManager().currentActivity().finish();
                }, new ErrorHandler());
    }

    public void checkApplyStatus(ApplyMessage message) {
        MyRetrofit.get().getCommonApiService().checkKeyStatus(message.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonObject -> {
                    String json = jsonObject.toString();
                    JSONObject obj = JSON.parseObject(json);
                    Integer code = obj.getInteger("code");
                    String msg = obj.getString("msg");
                    if (code == 200) {
                        ARouter.getInstance().build(RouterUtil.MsgModulePath.APPLY_INFO).withSerializable("applyMessage", message).navigation();
                    } else if (code == 409) {
                        JSONObject data = obj.getJSONObject("data");
                        String dealer = data.getString("dealUser");
                        long dealTime = data.getLong("dealTime");
                        String phone = data.getString("dealMobile");
                        if(BaseApplication.getUser().account.equals(phone)) {
                            ARouter.getInstance().build(RouterUtil.MsgModulePath.APPLY_INFO).withBoolean("hasDealt", true).withSerializable("applyMessage", message).navigation();
                        } else {
                            String format = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault()).format(new Date(dealTime));
                            //TODO
                            new AlertDialog.Builder(AppManager.getAppManager().currentActivity())
                                    .setTitle("提示")
                                    .setMessage(dealer + " 已于 " + format + " 处理这条申请")
                                    .setPositiveButton("好的", null)
                                    .show();
                        }

                    } else {
                        CLToast.showAtCenter(getApplication(), msg);
                    }
                }, new ErrorHandler());
    }
}
