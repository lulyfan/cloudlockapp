package com.ut.module_lock.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.operation.CommonApi;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.utils.BleOperateManager;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.protocol.data.GateLockKey;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * author : zhouyubin
 * time   : 2019/01/08
 * desc   :
 * version: 1.0
 */
public class DeviceKeyDetailVM extends BaseViewModel implements BleOperateManager.OperateCallback, BleOperateManager.OperateDeviceKeyDetailCallback {
    private MutableLiveData<Boolean> freezeResult = new MutableLiveData();

    public MutableLiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    private MutableLiveData<Boolean> deleteResult = new MutableLiveData();
    public BleOperateManager mBleOperateManager = null;
    private List<GateLockKey> mGateLockKeys = new ArrayList<>();


    private DeviceKey mDeviceKey = null;
    private LockKey mLockKey = null;

    public static final int OPERATETYPE_DELETE = 0;
    public static final int OPERATETYPE_FREEZE = 1;
    private int operateType = OPERATETYPE_DELETE;

    public void setLockKey(LockKey lockKey) {
        mLockKey = lockKey;
    }

    public void setDeviceKey(DeviceKey deviceKey) {
        mDeviceKey = deviceKey;
    }

    public DeviceKeyDetailVM(@NonNull Application application) {
        super(application);
        mBleOperateManager = new BleOperateManager(application);
        mBleOperateManager.setOperateCallback(this);
        mBleOperateManager.setOperateDeviceKeyDetailCallback(this);
    }

    public LiveData<Boolean> getFreezeResult() {
        return freezeResult;
    }

    public void freezeOrUnfreeze(boolean isFreeze, Activity activity) {
        operateType = OPERATETYPE_FREEZE;
        mGateLockKeys.clear();
        GateLockKey gateLockKey = new GateLockKey();
        gateLockKey.setFreezeState(isFreeze);
        gateLockKey.setKeyId(mDeviceKey.getKeyID());
        gateLockKey.setAuthState(mDeviceKey.getIsAuthKey() == 1);
        gateLockKey.setKeyType((byte) mDeviceKey.getKeyType());
        gateLockKey.setInnerNum(mDeviceKey.getKeyInId());
        mGateLockKeys.add(gateLockKey);
        toOperate(activity);
    }

    public void deleteKey(Activity activity) {
        operateType = OPERATETYPE_DELETE;
        toOperate(activity);
    }

    private void toOperate(Activity activity) {
        if (mBleOperateManager.isConnected(mLockKey.getMac())) {
            onConnectSuccess();
        } else {
            mBleOperateManager.scanDevice(mLockKey.getType(), activity);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    @Override
    public void onStartScan() {
        getShowDialog().postValue(true);
    }

    @Override
    public boolean checkScanResult(ScanDevice scanDevice) {
        if (scanDevice.getAddress().equalsIgnoreCase(mLockKey.getMac())) return true;
        return false;
    }

    @Override
    public void onScanSuccess(ScanDevice scanDevice) {
        mBleOperateManager.connect(scanDevice, mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onScanFaile(int errorCode) {
        getShowDialog().postValue(false);
        if (errorCode == BleOperateManager.ERROR_SCANTIMEOUT) {
            showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
        } else if (errorCode == BleOperateManager.ERROR_LOCKRESET) {
            mShowLockResetDialog.postValue(true);
        }
    }

    @Override
    public void onConnectSuccess() {
        if (operateType == OPERATETYPE_FREEZE) {
            mBleOperateManager.writeDeviceKey(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), mGateLockKeys);
        } else {
            mBleOperateManager.deleteDeviceKey(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), mDeviceKey.getKeyID());
        }
    }

    @Override
    public void onConnectFailed(int errorcode, String errorMsg) {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.lock_device_key_operate_failed));
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onElectric(int elect) {

    }

    @Override
    public void onDeleteSuccess() {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.operate_success));
        deleteResult.postValue(true);
        mExecutorService.execute(() -> {
            DeviceKeyDaoImpl.get().delete(mDeviceKey);
        });

        Disposable disposable = CommonApi.delDeviceKeyInfo(mLockKey.getId(), mDeviceKey.getKeyID())
                .subscribe(result -> {
                    getApplication().sendBroadcast(new Intent(RouterUtil.BrocastReceiverAction.ACTION_RELOAD_WEB_DEVICEKEY));
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        getApplication().sendBroadcast(new Intent(RouterUtil.BrocastReceiverAction.ACTION_RELOAD_WEB_DEVICEKEY));
                    }
                });
//        mCompositeDisposable.add(disposable);
    }


    @Override
    public void onDeleteFailed(int errorcode, String errorMsg) {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.lock_device_key_operate_failed));
    }

    @Override
    public void onWriteDeviceKeySuccess() {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.operate_success));
        boolean isFreezen = mDeviceKey.getKeyStatus() == EnumCollection.DeviceKeyStatus.FROZEN.ordinal();
        if (isFreezen) {
            mDeviceKey.setUnfreezeStatus();
        } else {
            mDeviceKey.setKeyStatus(EnumCollection.DeviceKeyStatus.FROZEN.ordinal());
        }
        freezeResult.postValue(true);
        mExecutorService.execute(() -> {
            DeviceKeyDaoImpl.get().insertDeviceKeys(mDeviceKey);
        });
        //向后台更新钥匙信息
        Disposable disposable = CommonApi.updateKeyInfo(mDeviceKey)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(voidResult -> {
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onWriteDeviceKeyFailed(int errCode, String errMsg) {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.lock_device_key_operate_failed));
        freezeResult.postValue(false);
    }
}
