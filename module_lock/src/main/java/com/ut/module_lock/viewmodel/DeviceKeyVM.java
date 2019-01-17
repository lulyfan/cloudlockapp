package com.ut.module_lock.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.ut.base.BaseApplication;
import com.ut.base.Utils.UTLog;
import com.ut.database.dao.ORecordDao;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.Record;
import com.ut.module_lock.R;
import com.ut.module_lock.utils.BleOperateManager;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.protocol.data.AuthCountInfo;
import com.ut.unilink.cloudLock.protocol.data.AuthInfo;
import com.ut.unilink.cloudLock.protocol.data.GateLockKey;
import com.ut.unilink.cloudLock.protocol.data.GateLockOperateRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * author : zhouyubin
 * time   : 2019/01/08
 * desc   :
 * version: 1.0
 */
public class DeviceKeyVM extends BaseViewModel implements BleOperateManager.OperateCallback, BleOperateManager.OperateDeviceKeyCallback {

    private List<DeviceKey> mAllDeviceKey = new ArrayList<>();
    private List<DeviceKeyAuth> mAllDeviceKeyAuth = new ArrayList<>();
    private Observer<List<DeviceKey>> observer1 = null;

    public LockKey getLockKey() {
        return mLockKey;
    }

    private LockKey mLockKey = null;
    public BleOperateManager mBleOperateManager = null;
    private int processTickInt = 0;

    private List<Record> mRecordList = new ArrayList<>();

    private MutableLiveData<String> showTip = new MutableLiveData<>();

    private MutableLiveData<Integer> processTick = new MutableLiveData<>();

    public MutableLiveData<String> getShowTip() {
        return showTip;
    }

    public MutableLiveData<Integer> getProcessTick() {
        return processTick;
    }

    public DeviceKeyVM(@NonNull Application application) {
        super(application);
        mBleOperateManager = new BleOperateManager(application);
        mBleOperateManager.setOperateCallback(this);
        mBleOperateManager.setOperateDeviceKeyCallback(this);
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public LiveData<List<DeviceKey>> getDeviceKeys(int deviceKeyType) {
        return DeviceKeyDaoImpl.get().findDeviceKeysByType(deviceKeyType);
    }

    public void connectAndGetData(int type, Activity activity) {
        if (mBleOperateManager.isConnected(mLockKey.getMac())) {
            onConnectSuccess();
        } else {
            mBleOperateManager.scanDevice(type, activity);
            UTLog.i("to scan");
        }
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        DeviceKeyDaoImpl.get().getAll().removeObserver(observer1);
        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    @Override
    public void onStartScan() {
        UTLog.i("onStartScan");
        processTickInt = 0;
        processTick.postValue(processTickInt++);
    }

    @Override
    public boolean checkScanResult(ScanDevice scanDevice) {
        if (scanDevice.getAddress().equals(mLockKey.getMac())) return true;
        return false;
    }

    @Override
    public void onScanSuccess(ScanDevice scanDevice) {
        processTick.postValue(processTickInt++);
        mBleOperateManager.connect(scanDevice, mLockKey.getType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onScanFaile(int errorCode) {
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
    }

    @Override
    public void onConnectSuccess() {
        processTick.postValue(processTickInt++);
        //todo 后台获取时间后进行对时
        long time = 0;
        mBleOperateManager.updateTime(mLockKey.getMac(), mLockKey.getType(), mLockKey.getEncryptKey(), time);
    }

    @Override
    public void onConnectFailed(int errorcode, String errorMsg) {
        UTLog.i("i:" + errorcode + " msg:" + errorMsg);
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_connect_failed));
    }

    @Override
    public void onElectric(int elect) {

    }

    @Override
    public void onWriteTimeSuccess() {
        processTick.postValue(processTickInt++);
        mBleOperateManager.readKeyInfo(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onWriteTimeFailed(int code, String msg) {
        UTLog.i("i:" + code + " msg:" + msg);
        processTick.postValue(processTickInt++);
        mBleOperateManager.readKeyInfo(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onReadKeyInfoSuccess(List<GateLockKey> data) {
        processTick.postValue(processTickInt++);

        mAllDeviceKey.clear();
        for (int i = 0; i < data.size(); i++) {
            GateLockKey key = data.get(i);
            UTLog.i("====mm:" + key);
            DeviceKey deviceKey = new DeviceKey(i, key.getKeyId(),
                    "", key.getKeyType(), key.getAttribute(), key.getInnerNum());
            deviceKey.initName(getApplication().getResources().getStringArray(R.array.deviceTypeName));
            deviceKey.setIsAuthKey(key.isAuthKey());
            deviceKey.setLockId(Integer.valueOf(mLockKey.getId()));
            if (key.isFreeze()) {
                deviceKey.setKeyStatus(EnumCollection.DeviceKeyStatus.FROZEN.ordinal());
            }
            mAllDeviceKey.add(deviceKey);
        }
        //从蓝牙获取信息后存入数据库
        mExecutorService.execute(() -> DeviceKeyDaoImpl.get().insertDeviceKeys(mAllDeviceKey));
        //再获取授权信息
        mBleOperateManager.queryAllAuth(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onReadKeyInfoFailed(int code, String msg) {
        UTLog.i("i:" + code + " msg:" + msg);
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    @Override
    public void onQueryAllAuthSuccess(List<AuthInfo> data) {
        processTick.postValue(processTickInt++);

        //从蓝牙获取信息后处理
        mAllDeviceKeyAuth.clear();
        for (int i = 0; i < data.size(); i++) {
            AuthInfo authInfo = data.get(i);
            UTLog.i("====mm:" + authInfo);
            DeviceKeyAuth deviceKeyAuth = new DeviceKeyAuth(authInfo.getAuthId(), authInfo.getKeyId(),
                    authInfo.getOpenLockCount(), authInfo.getStartTime(), authInfo.getEndTime());
            deviceKeyAuth.setTimeICtl(authInfo.getAuthDay());
            mAllDeviceKeyAuth.add(deviceKeyAuth);
        }
        //再从蓝牙获取授权次数信息
        mBleOperateManager.readAuthCountInfo(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onQueryAllAuthFailed(int code, String msg) {
        UTLog.i("i:" + code + " msg:" + msg);
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    @Override
    public void onReadAuthCountSuccess(List<AuthCountInfo> data) {
        processTick.postValue(processTickInt++);

        //再从蓝牙获取授权次数信息
        for (DeviceKeyAuth auth : mAllDeviceKeyAuth) {
            for (AuthCountInfo info : data) {
                UTLog.i("====mm:" + info);
                if (auth.getAuthId() == info.getAuthId()) {
                    auth.setOpenLockCnt(info.getAuthCount());
                    auth.setOpenLockCntUsed(info.getOpenLockCount());
                }
            }
            DeviceKey key = getKeyByKeyId(auth.getKeyID());
            if (key != null) {
                key.setDeviceKeyAuthData(auth);
            }
        }
        //从蓝牙获取信息后存入数据库
        mExecutorService.execute(() -> {
            DeviceKeyDaoImpl.get().insertDeviceKeys(mAllDeviceKey);
            //todo 提交数据到后台
        });
        mRecordIndex = 0;
        mRecordList.clear();
        toGetLockRecord();
    }

    @Override
    public void onReadAuthCountFailed(int code, String msg) {
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
        mBleOperateManager.disconnect(mLockKey.getMac());
        UTLog.i("i:" + code + " msg:" + msg);
    }

    @Override
    public void onReadRecordSuccess(List<GateLockOperateRecord> data) {
        processTick.postValue(processTickInt++);
        for (GateLockOperateRecord record : data) {
            DeviceKey deviceKey = getKeyByKeyId(record.getKeyId());
            if (deviceKey == null) return;
            Record record1 = new Record();
            record1.setCreateTime(record.getOperateTime());
            record1.setUserId(BaseApplication.getUser().getId());
//            record1.setUserName(deviceKey.getName());
            String description = String.format(getApplication().getString(R.string.lock_device_key_record_description), deviceKey.getName(),
                    getApplication().getResources().getStringArray(R.array.deviceTypeName)[deviceKey.getKeyType()]);
            record1.setDescription(description);
            record1.setLockId(Integer.parseInt(mLockKey.getId()));
            record1.setKeyId(0 - mLockKey.getKeyId());
            record1.setType(mLockKey.getType() + 2);//设备钥匙的类型从2开始
            mRecordList.add(record1);
        }
        if (mRecordIndex < 6) {//加载25条
            toGetLockRecord();
        } else {
            endReadRecord();
        }


    }

    private void endReadRecord() {
        processTick.postValue(100);//加载结束
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_success));
        //TODO 发送到后台
    }

    private int mRecordIndex = 1;

    private void toGetLockRecord() {
        mBleOperateManager.readOpenRecord(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), mRecordIndex);
    }

    private DeviceKey getKeyByKeyId(int id) {
        for (DeviceKey key : mAllDeviceKey) {
            if (key.getKeyID() == id) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void onReadRecordFailed(int errorcode, String errorMsg) {
        UTLog.i("i:" + errorcode + " msg:" + errorMsg);
        if (errorcode == -4) {
            endReadRecord();
        } else {
            processTick.postValue(-1);
            showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
        }

        mBleOperateManager.disconnect(mLockKey.getMac());
    }
}
