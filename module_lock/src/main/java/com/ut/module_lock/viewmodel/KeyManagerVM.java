package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.MyRetrofit;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.commoncomponent.CLToast;
import com.ut.database.dao.KeyDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_lock.R;
import com.ut.database.entity.Key;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@SuppressLint("CheckResult")
public class KeyManagerVM extends AndroidViewModel {
    private MutableLiveData<List<Key>> keys = null;
    private MutableLiveData<String> feedbackMessage = null;
    private int currentPage = 1;
    private static int DEFAULT_PAGE_SIZE = 10;
    private String mac;
    private Key key;

    private KeyDao keyDao;

    public void setKey(Key k) {
        key = (Key) k.clone();
    }

    public Key getKey() {
        return (Key) key.clone();
    }

    public KeyManagerVM(@NonNull Application application) {
        super(application);
        keyDao = CloudLockDatabaseHolder.get().getKeyDao();
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public LiveData<List<Key>> getKeys(String mac) {
        return keyDao.findKeysByMac(mac);
    }

    public MutableLiveData<String> getFeedbackMessage() {
        if (feedbackMessage == null) {
            feedbackMessage = new MutableLiveData<>();
        }
        return feedbackMessage;
    }

    public void loadKeyItems() {
        MyRetrofit.get().getCommonApiService()
                .pageKeys(BaseApplication.getUser().id, mac, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        if (!result.data.isEmpty()) {
                            initKey(result.data);
                            currentPage++;
                            saveKeys(result.data);
                        }
                    } else {
                        CLToast.showAtBottom(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
    }

    private void saveKeys(List<Key> data) {
        Schedulers.io().scheduleDirect(() -> {
            Key[] tmps = new Key[data.size()];
            keyDao.insertKeys(data.toArray(tmps));
            tmps = null;
        });
    }

    public void updateKeyItems() {
        MyRetrofit.get().getCommonApiService()
                .pageKeys(BaseApplication.getUser().id, mac, 1, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        if (!result.data.isEmpty()) {
                            initKey(result.data);
                            currentPage++;
                            saveKeys(result.data);
                        }
                    } else {
                        CLToast.showAtBottom(getApplication(), result.msg);
                    }
                }, new ErrorHandler());
    }


    public void unFrozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .unFrozenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), new ErrorHandler());

    }

    public void frozenKey(long keyId) {
        MyRetrofit.get().getCommonApiService().frozenKey(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getFeedbackMessage().postValue(String.valueOf(result.msg)), new ErrorHandler());
    }

    public void deleteKey(long keyId) {
        MyRetrofit.get().getCommonApiService().deleteKey(keyId, 0)
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    keyDao.deleteKeyByKeyId((int) keyId);
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if(result.isSuccess()) {
                        updateKeyItems();
                    }
                    getFeedbackMessage().postValue(String.valueOf(result.msg));
                }, new ErrorHandler());
    }

    public int getDefaultPageSize() {
        return DEFAULT_PAGE_SIZE;
    }

    public void editKeyName(Key key, String name) {
        key.setKeyName(name);
        ArrayList<Key> keys = new ArrayList<>();
        keys.add(key);
        saveKeys(keys);
    }

    public void editKey(Key keyItem) {
        //ToDo
        MyRetrofit.get().getCommonApiService().editKey(keyItem.getMac(),
                keyItem.getKeyId(),
                keyItem.getStartTime(),
                keyItem.getEndTime(),
                keyItem.getWeeks(),
                keyItem.getStartTimeRange(),
                keyItem.getEndTimeRange())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        ArrayList<Key> keys = new ArrayList<>();
                        keys.add(keyItem);
                        saveKeys(keys);
                    }
                    CLToast.showAtBottom(getApplication(), result.msg);
                }, new ErrorHandler());
    }

    public void clearKeys(String mac) {
        MyRetrofit.get().getCommonApiService().clearAllKey(mac)
                .subscribeOn(Schedulers.io())
                .map(result -> {
                    CloudLockDatabaseHolder.get().getKeyDao().deleteAllByMac(mac);
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    getFeedbackMessage().postValue(String.valueOf(result.msg));
                }, new ErrorHandler());
    }

    public void toAuth(long keyId) {
        MyRetrofit.get().getCommonApiService().toAuth(keyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    getFeedbackMessage().postValue(String.valueOf(result.msg));
                }, new ErrorHandler());
    }

    public void cancelAuth(long keyId) {
        MyRetrofit.get().getCommonApiService().cancelAuth(keyId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    getFeedbackMessage().postValue(String.valueOf(result.msg));
                }, new ErrorHandler());
    }

    public void initKey(List<Key> keys) {
        for (Key key : keys) {
            key.setRuleTypeDrawableId(getRuleTypeDrawableId(key));
            key.setStatusString(getStateString(key));
            key.setRuleTypeString(getRuleTypeString(key));
            key.setMac(mac);
        }
    }

    private int getRuleTypeDrawableId(Key key) {
        int[] rids = {R.mipmap.permanent, R.mipmap.limited_time, R.mipmap.once, R.mipmap.loop};
        return rids[key.getRuleType() - 1];
    }

    public String getStateString(Key key) {
        //status 1,"发送中" 2,"冻结中" 3,"解除冻结中" 4,"删除中" 5,"授权中" 6,"取消授权中" 7,"修改中 8,"正常"  9,"已冻结" 10,"已删除" 11,"已失效" 12,"已过期"
        switch (key.getStatus()) {
            case 1:
                return getApplication().getString(R.string.lock_key_status_sending);
            case 2:
                return getApplication().getString(R.string.lock_key_status_frozening);
            case 3:
                return getApplication().getString(R.string.lock_key_status_cancel_frozen);
            case 4:
                return getApplication().getString(R.string.lock_key_status_delete);
            case 5:
                return getApplication().getString(R.string.lock_key_status_authorize);
            case 6:
                return getApplication().getString(R.string.lock_key_status_cancel_authorize);
            case 7:
                return getApplication().getString(R.string.lock_key_status_fix);
            case 8:
                return "";
            case 9:
                return getApplication().getString(R.string.lock_key_status_has_frozen);
            case 10:
                return getApplication().getString(R.string.lock_key_status_has_deleted);
            case 11:
                return getApplication().getString(R.string.lock_key_status_has_invailed);
            case 12:
                return getApplication().getString(R.string.lock_key_status_out_of_date);
        }
        return "";
    }

    public String getRuleTypeString(Key key) {
//        1永久 2限时 3单次 4循环
        switch (key.getRuleType()) {
            case 1:
                return BaseApplication.getAppContext().getString(R.string.permanent);
            case 2:
                return BaseApplication.getAppContext().getString(R.string.limit_time);
            case 3:
                return BaseApplication.getAppContext().getString(R.string.once_time);
            case 4:
                return BaseApplication.getAppContext().getString(R.string.loop);
        }
        return "";
    }

    public LiveData<Key> getKeyById(long id) {
        return keyDao.getKeyById(id);
    }
}
