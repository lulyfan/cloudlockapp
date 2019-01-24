package com.ut.module_lock.viewmodel;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.example.operation.CommonApi;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.DeviceKeyDaoImpl;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.Lock;
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
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;


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

    public List<DeviceKey> mWebDeviceKeyList = null;

    public DeviceKeyVM(@NonNull Application application) {
        super(application);
        mBleOperateManager = new BleOperateManager(application);
        mBleOperateManager.setOperateCallback(this);
        mBleOperateManager.setOperateDeviceKeyCallback(this);
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
        initDataFromWeb();
    }

    public void initDataFromWeb() {
        mWebDeviceKeyList = null;
        Disposable disposable = CommonApi.getDeviceKeyListByType(0, mLockKey.getId())
                .observeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .subscribe(deviceKeyResults -> {
                    if (deviceKeyResults.isSuccess()) {
                        this.mWebDeviceKeyList = deviceKeyResults.getData();
                        if (mWebDeviceKeyList.size() > 0) {
                            DeviceKeyDaoImpl.get().deleteKeyByLockId(Integer.parseInt(mLockKey.getId()));
                            DeviceKeyDaoImpl.get().insertDeviceKeys(mWebDeviceKeyList);
                        }
                    }
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }

    public LiveData<List<DeviceKey>> getDeviceKeys(int deviceKeyType) {
        return DeviceKeyDaoImpl.get().findDeviceKeysByType(Integer.parseInt(mLockKey.getId()), deviceKeyType);
    }

    public void connectAndGetData(int type, Activity activity) {
        if (mBleOperateManager.isConnected(mLockKey.getMac())) {
            processTickInt = 0;
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
//        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    @Override
    public void onStartScan() {
        UTLog.i("onStartScan");
        processTickInt = 0;
        processTick.postValue(processTickInt++);
    }

    @Override
    public boolean checkScanResult(ScanDevice scanDevice) {
        if (scanDevice.getAddress().equalsIgnoreCase(mLockKey.getMac())) return true;
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
        UTLog.i("i:onConnectSuccess");
        processTick.postValue(processTickInt++);
        //todo 后台获取时间后进行对时
        mExecutorService.schedule(() -> {
            long time = System.currentTimeMillis();
            mBleOperateManager.updateTime(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), time);
        }, 100, TimeUnit.MILLISECONDS);

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
            if (key.getKeyType() == EnumCollection.DeviceKeyType.PASSWORD.ordinal()) {
                if (key.getInnerNum() == 0) {
                    deviceKey.setName(getApplication().getString(R.string.lock_device_key_admin_pwd));
                } else if (key.getInnerNum() == 1) {
                    deviceKey.setName(getApplication().getString(R.string.lock_device_key_user_pwd));
                } else if (key.getInnerNum() == 2) {
                    deviceKey.setName(getApplication().getString(R.string.lock_device_key_temp_pwd));
                }
            } else {
                deviceKey.initName(getApplication().getResources().getStringArray(R.array.deviceTypeName));
            }
            deviceKey.setIsAuthKey(key.isAuthKey());
            deviceKey.setLockID(Integer.valueOf(mLockKey.getId()));
            if (key.isFreeze()) {
                deviceKey.setKeyStatus(EnumCollection.DeviceKeyStatus.FROZEN.ordinal());
            }
            mAllDeviceKey.add(deviceKey);
        }
        //从蓝牙获取信息后存入数据库
//        mExecutorService.execute(() -> DeviceKeyDaoImpl.get().insertDeviceKeys(mAllDeviceKey));
        //再获取授权信息
        mBleOperateManager.queryAllAuth(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey());
    }

    @Override
    public void onReadKeyInfoFailed(int code, String msg) {
        UTLog.i("i:" + code + " msg:" + msg);
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
//        mBleOperateManager.disconnect(mLockKey.getMac());
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
//        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    @Override
    public void onReadAuthCountSuccess(List<AuthCountInfo> data) {
        processTick.postValue(processTickInt++);

        //再从蓝牙获取授权次数信息
        if (data != null && data.size() > 0) {
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
        }


        mExecutorService.execute(() -> {
            //从蓝牙获取信息后存入数据库
            initDeviceKeyNameAndSave(mWebDeviceKeyList);
            //将数据初始化到后台
            initDeviceKeyToWeb();
        });
        mRecordIndex = 1;
        mRecordList.clear();
        toGetLockRecord();
    }

    private void initDeviceKeyToWeb() {
        if (mWebDeviceKeyList != null) {
            //todo 提交数据到后台
            Disposable disposable = CommonApi.initLockKey(Integer.parseInt(mLockKey.getId()), mAllDeviceKey)
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribe(voidResults -> {
                        if (voidResults.isSuccess()) {
                        }
                    }, new ErrorHandler());
            mCompositeDisposable.add(disposable);
        } else {
            //todo 提交数据到后台
            Disposable disposable = CommonApi.getDeviceKeyListByType(0, mLockKey.getId())//先从后台拿名字,防止第一次拿不成功
                    .observeOn(io.reactivex.schedulers.Schedulers.io())
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .flatMap((Function<Results<DeviceKey>, Observable<Result<Void>>>) deviceKeyResults -> {
                        if (deviceKeyResults.isSuccess()) {
                            List<DeviceKey> list = deviceKeyResults.getData();
                            initDeviceKeyNameAndSave(list);
                            return CommonApi.initLockKey(Integer.parseInt(mLockKey.getId()), mAllDeviceKey);
                        }
                        return null;
                    })
                    .subscribe(deviceKeyResults -> {
                        if (deviceKeyResults.isSuccess()) {

                        }
                    }, new ErrorHandler());
            mCompositeDisposable.add(disposable);
        }
    }

    @Override
    public void onReadAuthCountFailed(int code, String msg) {
        processTick.postValue(-1);
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_failed));
//        mBleOperateManager.disconnect(mLockKey.getMac());
        UTLog.i("i:" + code + " msg:" + msg);
    }

    @Override
    public void onReadRecordSuccess(List<GateLockOperateRecord> data) {
        processTick.postValue(processTickInt++);
        if (data == null || data.size() <= 0) {//数据为空时说明读到末尾
            endReadRecord();
            return;
        }
        addOpenRecord(data);//加入缓存
        if (mRecordIndex < 4) {//加载15条数据
            toGetLockRecord();
        } else {
            endReadRecord();
        }


    }

    private void addOpenRecord(List<GateLockOperateRecord> data) {
        for (GateLockOperateRecord record : data) {
            DeviceKey deviceKey = getKeyByKeyId(record.getKeyId());
            if (deviceKey == null) continue;
            Record record1 = new Record();
            record1.setCreateTime(record.getOperateTime());
            record1.setUserId(BaseApplication.getUser().getId());
//            record1.setUserName(deviceKey.getName());
            String description = String.format(getApplication().getString(R.string.lock_device_key_record_description), deviceKey.getName(),
                    getApplication().getResources().getStringArray(R.array.deviceTypeName)[deviceKey.getKeyType()]);
            record1.setDescription(description);
            record1.setLockId(Integer.parseInt(mLockKey.getId()));
            record1.setKeyId(deviceKey.getRecordKeyId());
            record1.setType(deviceKey.getKeyType() + 2);//设备钥匙的类型从2开始
            mRecordList.add(record1);
        }
    }

    private void endReadRecord() {
        processTick.postValue(100);//加载结束
        showTip.postValue(getApplication().getString(R.string.lock_device_key_load_success));
        Disposable disposable = CommonApi.insertInnerLockLog(mRecordList)
                .subscribe(voidResult -> {
                    if (voidResult.isSuccess()) {
                    }
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
//        CloudLockDatabaseHolder.get().recordDao().insertRecords(mRecordList);
    }

    private int mRecordIndex = 1;

    private void toGetLockRecord() {
        mBleOperateManager.readOpenRecord(mLockKey.getMac(), mLockKey.getEncryptType(), mLockKey.getEncryptKey(), mRecordIndex++);
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

//        mBleOperateManager.disconnect(mLockKey.getMac());
    }

    //从后台拿名字
    private void initDeviceKeyNameAndSave(List<DeviceKey> list) {
        if (list != null && list.size() > 0) {
            for (DeviceKey deviceKey : mAllDeviceKey) {
                if (list.contains(deviceKey)) {
                    deviceKey.setName(list.get(list.indexOf(deviceKey)).getName());
                }
            }
        }
        DeviceKeyDaoImpl.get().deleteKeyByLockId(Integer.parseInt(mLockKey.getId()));
        DeviceKeyDaoImpl.get().insertDeviceKeys(mAllDeviceKey);//从后台拿名字后更新数据库并初始化后台数据
        UTLog.i("====mm:" + mAllDeviceKey.toString());
    }
}
