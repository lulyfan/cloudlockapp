package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.example.operation.CommonApi;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.RomUtils;
import com.ut.base.Utils.UTLog;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.commoncomponent.CLToast;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.entity.SwitchResult;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/25
 * desc   :
 * version: 1.0
 */

@SuppressLint("CheckResult")
public class LockSettingVM extends BaseViewModel {
    private MutableLiveData<String> dialogHandler = new MutableLiveData<>();
    private boolean isConnected = false;
    private MutableLiveData<Boolean> isDeleteSuccess = new MutableLiveData<>();
    private MutableLiveData<Long> selectedGroupId = new MutableLiveData<>();
    private MutableLiveData<SwitchResult> setCanOpenSwitchResult = new MutableLiveData<>();
    private LockKey lockKey;

    private boolean hasFindedDevice = false;

    public static final int BLEREAUESTCODE = 101;

    public static final int BLEENABLECODE = 102;

    public void setLockKey(LockKey lockKey) {
        this.lockKey = lockKey;
    }

    public LockSettingVM(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> isDeleteSuccess() {
        return isDeleteSuccess;
    }

    public MutableLiveData<String> getDialogHandler() {
        return dialogHandler;
    }

    public MutableLiveData<Long> getSelectedGroupId() {
        if (selectedGroupId == null) {
            selectedGroupId = new MutableLiveData<>();
        }
        return selectedGroupId;
    }

    public LiveData<SwitchResult> getSetCanOpenSwitchResult() {
        return setCanOpenSwitchResult;
    }

    public void verifyAdmin(String pwd) {
        dialogHandler.postValue(Constance.START_LOAD);
        Disposable subscribe = MyRetrofit.get().getCommonApiService().verifyUserPwd(pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        toDeleteAdminKey();
                    } else {
                        if (TextUtils.isEmpty(result.msg)) {
                            showTip.postValue(getApplication().getString(R.string.lock_unbind_fail_tips));
                        } else {
                            showTip.postValue(result.msg);
                        }
                        endLoad();
                    }
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        endLoad();
                    }
                });
        mCompositeDisposable.add(subscribe);
    }

    public void changeCanOpen(boolean canOpen) {
        Disposable disposable = CommonApi.setCanOpen(lockKey.getKeyId(), canOpen ? 1 : 0)
                .subscribe(voidResult -> {
                    if (voidResult.isSuccess()) {
                        setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, true));
                        lockKey.setCanOpen(canOpen ? 1 : 0);
                        saveLockKey(lockKey);
                    } else {
                        setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, false));
                    }
                    showTip.postValue(voidResult.msg);
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, false));
                    }
                });
        mCompositeDisposable.add(disposable);
    }


    public void toDeleteAdminKey() {
        if (RomUtils.isFlymeRom()//当为魅族手机时需要提醒用户打开定位服务
                && !checkGpsAndOpenLock()) {
            endLoad();
            return;
        }
        int result = unbindLock(lockKey);
        switch (result) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                CLToast.showAtBottom(getApplication(), getApplication().getString(R.string.lock_tip_ble_not_support));
                endLoad();
                break;
            case UnilinkManager.NO_LOCATION_PERMISSION:
                UnilinkManager.getInstance(getApplication()).requestPermission(AppManager.getAppManager().currentActivity(), BLEREAUESTCODE);
                endLoad();
                break;
            case UnilinkManager.BLE_NOT_OPEN:
                UnilinkManager.getInstance(getApplication()).enableBluetooth(AppManager.getAppManager().currentActivity(), BLEENABLECODE);
                endLoad();
                break;
        }
    }

    private boolean checkGpsAndOpenLock() {
        if (!SystemUtils.isGPSOpen(AppManager.getAppManager().currentActivity())) {
            new CustomerAlertDialog(AppManager.getAppManager().currentActivity(), false)
                    .setMsg(AppManager.getAppManager().currentActivity().getString(R.string.lock_gps_open_tips))
                    .setCancelLister(v -> {
                    })
                    .setConfirmListener(v -> {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        AppManager.getAppManager().currentActivity().startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    })
                    .show();
            return false;
        }
        return true;
    }

    public int unbindLock(LockKey lockKey) {
        if (UnilinkManager.getInstance(getApplication()).isConnect(lockKey.getMac())) {
            toUnbind(lockKey);
        } else {
            hasFindedDevice = false;
            return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice) {
                    UTLog.i("scanDevice：" + scanDevice.getAddress());
                    if (!hasFindedDevice && lockKey.getMac().equalsIgnoreCase(scanDevice.getAddress())) {
                        hasFindedDevice = true;
                        if (!scanDevice.isActive()) {//当锁是未激活状态时直接删除后台数据
                            deleteAdminLock(lockKey);
                            endLoad();
                        } else {
                            toConnect(lockKey, scanDevice);
                        }
                    }
                }

                @Override
                public void onFinish(List<ScanDevice> scanDevices) {
                    if (!hasFindedDevice) {
                        onScanTimeout();
                    }
                }

                @Override
                public void onScanTimeout() {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                    endLoad();
                }
            }, 10);
        }
        return 0;
    }

    private void toConnect(LockKey lockKey, ScanDevice scanDevice) {
        UnilinkManager.getInstance(getApplication()).stopScan();
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                Schedulers.io().scheduleDirect(() -> {
                    toUnbind(lockKey);
                }, 100, TimeUnit.MILLISECONDS);
                isConnected = true;
            }

            @Override
            public void onDisconnect(int i, String s) {
                if (!isConnected) {
                    showTip.postValue(getApplication().getString(R.string.lock_bt_disconnect_tips));
                }
                endLoad();
            }
        });
    }

    private void toUnbind(LockKey lockKey) {
        CloudLock cloudLock = new CloudLock(lockKey.getMac());
        cloudLock.setAdminPassword(lockKey.getAdminPwd());
        cloudLock.setEncryptType(lockKey.getEncryptType());
        cloudLock.setEntryptKey(lockKey.getEncryptKey());
        UnilinkManager.getInstance(getApplication())
                .resetLock(cloudLock, new CallBack() {
                    @Override
                    public void onSuccess(CloudLock cloudLock) {
                        deleteAdminLock(lockKey);
                        endLoad();
                    }

                    @Override
                    public void onFailed(int i, String s) {
                        showTip.postValue(getApplication().getString(R.string.lock_unbind_fail_tips));
                        UnilinkManager.getInstance(getApplication()).disconnect(cloudLock.getAddress());
                        endLoad();

                    }
                });
    }

    private void deleteAdminLock(LockKey lockKey) {
        Disposable disposable = MyRetrofit.get().getCommonApiService().deleteAdminLock(lockKey.getMac())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        //删除数据库相应的锁数据
                        deleteLockKey(lockKey);
                        isDeleteSuccess.postValue(true);
                    }
                    showTip.postValue(result.msg);
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }

    private void deleteLockKey(LockKey lockKey) {
        Schedulers.newThread().scheduleDirect(() -> {
            LockKeyDaoImpl.get().deleteByMac(lockKey.getMac());
        }, 500, TimeUnit.MILLISECONDS);
    }

    public void deleteLockKey(LockKey lockKey, int ifAllKey) {
        Disposable disposable = MyRetrofit.get().getCommonApiService().deleteKey(lockKey.getKeyId(), ifAllKey)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        deleteLockKey(lockKey);
                        isDeleteSuccess.postValue(true);
                        //删除数据库相应的锁数据
                        Schedulers.io().scheduleDirect(() -> CloudLockDatabaseHolder.get().recordDao().deleteAll());
                    }
                    showTip.postValue(result.msg);
                }, new ErrorHandler());
        mCompositeDisposable.add(disposable);
    }

    public LiveData<LockKey> loadLockKey(String mac) {
        return LockKeyDaoImpl.get().getLockByMac(mac);
    }

    private void loadLockGroups() {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().getGroup()
                .subscribeOn(Schedulers.io())
                .map(listResult -> {
                    LockGroup[] lockGroups = new LockGroup[listResult.data.size()];
                    LockGroupDaoImpl.get().insertAll(listResult.data.toArray(lockGroups));
                    return listResult;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                    }
                }, new ErrorHandler());
        mCompositeDisposable.add(subscribe);
    }

    public LiveData<List<LockGroup>> getLockGroups() {
        return LockGroupDaoImpl.get().getAllLockGroup();
    }

    public void createGroup(String newGroupName) {
        Disposable subscribe = MyRetrofit.get().getCommonApiService().addGroup(newGroupName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtCenter(getApplication(), result.msg);
                    if (result.isSuccess()) {
                        loadLockGroups();
                        changeLockGroup(lockKey.getMac(), result.data);
                        selectedGroupId.postValue(Long.valueOf(result.data));
                        lockKey.setGroupId(result.data);
                        saveLockKey(lockKey);
                    }
                }, new ErrorHandler());
        mCompositeDisposable.add(subscribe);
    }

    public void modifyLockName(String mac, String newName) {
        MyRetrofit.get().getCommonApiService().editLockName(mac, newName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        lockKey.setName(newName);
                        saveLockKey(lockKey);
                    }
                    CLToast.showAtBottom(getApplication(), result.msg);
                }, new ErrorHandler());
    }

    public void addLockIntoGroup(String mac, long groupId) {
        final long lastId = lockKey.getGroupId();
        Disposable subscribe = MyRetrofit.get().getCommonApiService()
                .addLockIntoGroup(mac, groupId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtCenter(getApplication(), result.msg);
                    if (result.isSuccess()) {
                        lockKey.setGroupId((int) groupId);
                        saveLockKey(lockKey);
                    }
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        lockKey.setGroupId((int) lastId);
                        saveLockKey(lockKey);
                        selectedGroupId.postValue(lastId);
                        //todo 中文
                        CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                    }
                });
        mCompositeDisposable.add(subscribe);
    }


    public void delLockFromGroup(String mac, long groupId) {
        final long lastId = lockKey.getGroupId();
        Disposable subscribe = CommonApi
                .delLockFromGroup(mac, groupId)
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        lockKey.setGroupId(-1);
                        saveLockKey(lockKey);
                    }
                    CLToast.showAtCenter(getApplication(), result.msg);
                }, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        lockKey.setGroupId((int) lastId);
                        saveLockKey(lockKey);
                        selectedGroupId.postValue(lastId);
                        //todo 中文
                        CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                    }
                });
        mCompositeDisposable.add(subscribe);
    }

    public void changeLockGroup(String mac, long groupId) {
        if (lockKey.getGroupId() < 1) {
            addLockIntoGroup(mac, groupId);
        } else {
            final long lastId = lockKey.getGroupId();
            Disposable subscribe = MyRetrofit.get().getCommonApiService()
                    .changeLockGroup(mac, groupId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                            selectedGroupId.postValue(groupId);
                            lockKey.setGroupId((int) groupId);
                            saveLockKey(lockKey);
                        }
                        CLToast.showAtCenter(getApplication(), result.msg);
                    }, new ErrorHandler() {
                        @Override
                        public void accept(Throwable throwable) {
                            super.accept(throwable);
                            lockKey.setGroupId((int) lastId);
                            saveLockKey(lockKey);
                            selectedGroupId.postValue(lastId);
                            //todo 中文
                            CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                        }
                    });
            mCompositeDisposable.add(subscribe);
        }
    }

    public void saveLockKey(LockKey lockKey) {
        Schedulers.io().scheduleDirect(() -> {
            LockKeyDaoImpl.get().insert(lockKey);
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (lockKey != null) {
            UnilinkManager.getInstance(getApplication()).disconnect(lockKey.getMac());
        }
    }

    private void endLoad() {
        dialogHandler.postValue(Constance.END_LOAD);
    }
}
