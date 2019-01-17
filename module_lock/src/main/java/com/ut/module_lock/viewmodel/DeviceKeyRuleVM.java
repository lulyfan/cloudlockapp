package com.ut.module_lock.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.utils.BleOperateManager;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.protocol.data.AuthInfo;
import com.ut.unilink.cloudLock.protocol.data.GateLockKey;

import java.util.ArrayList;
import java.util.List;

/**
 * author : zhouyubin
 * time   : 2019/01/14
 * desc   :
 * version: 1.0
 */
public class DeviceKeyRuleVM extends BaseViewModel implements BleOperateManager.OperateCallback, BleOperateManager.OperateDeviceRuleCallback,
        BleOperateManager.OperateDeviceKeyDetailCallback {
    public MutableLiveData<DeviceKey> mDeviceKey = new MutableLiveData<>();
    private List<GateLockKey> mGateLockKeys = new ArrayList<>();
    public BleOperateManager mBleOperateManager = null;

    private int mFirstDeviceAuthType = 0;
    private LockKey mLockKey = null;

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    public MutableLiveData<Boolean> saveResult = new MutableLiveData<>();

    public DeviceKeyRuleVM(@NonNull Application application) {
        super(application);
        mBleOperateManager = new BleOperateManager(application);
        mBleOperateManager.setOperateCallback(this);
        mBleOperateManager.setOperateDeviceKeyDetailCallback(this);
        mBleOperateManager.setOperateDeviceRuleCallback(this);
    }

    public void setDeviceKey(DeviceKey deviceKey) {
        this.mDeviceKey.setValue(deviceKey);
    }

    public void setFirstDeviceAuthType(int firstDeviceAuthType) {
        mFirstDeviceAuthType = firstDeviceAuthType;
    }


    public void setLockKey(LockKey lockKey) {
        mLockKey = lockKey;
    }

    public DeviceKey getDeviceKey() {
        return mDeviceKey.getValue();
    }

    public LiveData<DeviceKey> getDeviceKeyLiveData() {
        return mDeviceKey;
    }

    public String openCntTimeStringByDeviceKey(int cnt) {
        if (cnt == 255) {
            return getApplication().getString(R.string.device_key_time_unlimit);
        }
        return String.valueOf(cnt);
    }

    public void saveDeviceKey(Activity activity) {
        mGateLockKeys.clear();
        DeviceKey deviceKey = mDeviceKey.getValue();
        if (deviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal()) {
            if (deviceKey.getTimeStart() >= deviceKey.getTimeEnd()) {
                getShowTip().postValue(getApplication().getString(R.string.lock_device_key_tip_auth_error1));
                return;
            }
        } else if (deviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.CYCLE.ordinal()) {
            if (deviceKey.getTimeStart() >= deviceKey.getTimeEnd()) {
                getShowTip().postValue(getApplication().getString(R.string.lock_device_key_tip_auth_error2));
                return;
            }
            if (deviceKey.getTimeStart() % 86400000 >= deviceKey.getTimeEnd() % 86400000) {
                getShowTip().postValue(getApplication().getString(R.string.lock_device_key_tip_auth_error3));
                return;
            }
        }
        //TODO 调用蓝牙写入成功后上传后台
        GateLockKey gateLockKey = new GateLockKey();
        gateLockKey.setFreezeState(deviceKey.getKeyType() == EnumCollection.DeviceKeyStatus.FROZEN.ordinal());
        gateLockKey.setKeyId(deviceKey.getKeyID());
        gateLockKey.setAuthState(deviceKey.getIsAuthKey());
        gateLockKey.setKeyType((byte) deviceKey.getKeyType());
        gateLockKey.setInnerNum(deviceKey.getKeyInId());
        UTLog.i("====mmmm" + gateLockKey.toString());
        mGateLockKeys.add(gateLockKey);
        if (mBleOperateManager.isConnected(mLockKey.getMac())) {
            onConnectSuccess();
        } else {
            mBleOperateManager.scanDevice(mLockKey.getType(), activity);
        }
    }

    @Override
    public void onStartScan() {
        getShowDialog().postValue(true);
    }

    @Override
    public boolean checkScanResult(ScanDevice scanDevice) {
        if (scanDevice.getAddress().equals(mLockKey.getMac())) return true;
        return false;
    }

    @Override
    public void onScanSuccess(ScanDevice scanDevice) {
        mBleOperateManager.connect(scanDevice, mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onScanFaile(int errorCode) {
        getShowTip().postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
    }

    @Override
    public void onConnectSuccess() {
        mBleOperateManager.writeDeviceKey(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), mGateLockKeys);
    }

    @Override
    public void onConnectFailed(int errorcode, String errorMsg) {
        operateFail();
    }

    @Override
    public void onElectric(int elect) {

    }

    @Override
    public void onAddDevicekeyAuthSuccess(int authId) {
        mDeviceKey.getValue().setAuthId(authId);
        operateSuccess();
    }


    @Override
    public void onAddDeviceKeyAuthFailed(int errCode, String errMsg) {
        operateFail();
    }

    @Override
    public void onUpdateDeviceKeyAuthSuccess() {
        operateSuccess();
    }

    @Override
    public void onUpdateDeviceKeyAuthFailed(int errCode, String errMsg) {
        operateFail();
    }

    @Override
    public void onDeleteSuccess() {

    }

    @Override
    public void onDeleteFailed(int errorcode, String errorMsg) {

    }

    @Override
    public void onWriteDeviceKeySuccess() {
        if (mDeviceKey.getValue().getKeyAuthType() != EnumCollection.DeviceKeyAuthType.FOREVER.ordinal()) {
            AuthInfo authInfo = new AuthInfo();
            authInfo.setAuthId(mDeviceKey.getValue().getAuthId());
            authInfo.setAuthDay(mDeviceKey.getValue().getTimeICtlIntArr());
            authInfo.setStartTime(mDeviceKey.getValue().getTimeStart());
            authInfo.setEndTime(mDeviceKey.getValue().getTimeEnd());
            authInfo.setKeyId(mDeviceKey.getValue().getKeyID());
            authInfo.setOpenLockCount(mDeviceKey.getValue().getOpenLockCnt());
            UTLog.i("====mm" + authInfo.toString());
            if (mFirstDeviceAuthType != EnumCollection.DeviceKeyAuthType.FOREVER.ordinal()) {
                mBleOperateManager.addDeviceKeyAuth(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), authInfo);
            } else {
                mBleOperateManager.updateDeviceKeyAuth(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), authInfo);
            }
        } else {
            //todo 删除授权或提示成功
            operateSuccess();
        }
    }

    @Override
    public void onWriteDeviceKeyFailed(int errCode, String errMsg) {
        operateFail();
    }

    private void operateFail() {
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.lock_device_key_operate_failed));
    }

    private void operateSuccess() {
        UTLog.i("====mm" + mDeviceKey.getValue().toString());
        getShowDialog().postValue(false);
        getShowTip().postValue(getApplication().getString(R.string.operate_success));
        //写入数据库前设置钥匙状态
        mDeviceKey.getValue().setUnfreezeStatus();
        mExecutorService.execute(() -> {
            DeviceKeyDaoImpl.get().insertDeviceKeys(mDeviceKey.getValue());
        });
        saveResult.postValue(true);
    }
}
