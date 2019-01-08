package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.operation.CommonApi;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.commoncomponent.CLToast;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.entity.SwitchResult;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * author : zhouyubin
 * time   : 2018/12/25
 * desc   :
 * version: 1.0
 */

@SuppressLint("CheckResult")
public class LockSettingVM extends AndroidViewModel {
    private MutableLiveData<String> showTip = new MutableLiveData<>();
    private boolean isConnected = false;
    private MutableLiveData<Boolean> isDeleteSuccess = new MutableLiveData<>();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private MutableLiveData<Long> selectedGroupId = new MutableLiveData<>();
    private MutableLiveData<SwitchResult> setCanOpenSwitchResult = new MutableLiveData<>();
    private LockKey lockKey;

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

    public LiveData<String> getShowTip() {
        return showTip;
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
        MyRetrofit.get().getCommonApiService().verifyUserPwd(pwd)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        AppManager.getAppManager().currentActivity().startLoad();
                        toDeleteAdminKey();
                    }
                    showTip.postValue(result.msg);
                }, new ErrorHandler());
    }

    public void changeCanOpen(boolean canOpen) {
        Disposable disposable = CommonApi.setCanOpen(lockKey.getKeyId(), canOpen ? 1 : 0)
                .subscribe(voidResult -> {
                    if (voidResult.isSuccess()) {
                        setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, true));
                        lockKey.setCanOpen(canOpen ? 1 : 0);
                        updateLockKey();
                    } else {
                        setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, false));
                    }
                    showTip.postValue(voidResult.msg);
                }, throwable -> {
                    setCanOpenSwitchResult.postValue(new SwitchResult(!canOpen, false));
                    showTip.setValue(getApplication().getString(R.string.lock_tip_setting_failed));
                });
        mCompositeDisposable.add(disposable);
    }

    private void updateLockKey() {
        Schedulers.io().scheduleDirect(() -> {
            LockKeyDaoImpl.get().insert(lockKey);
        });
    }


    public void toDeleteAdminKey() {
        int result = unbindLock(lockKey);
        switch (result) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                CLToast.showAtBottom(getApplication(), getApplication().getString(R.string.lock_tip_ble_not_support));
                break;
            case UnilinkManager.NO_LOCATION_PERMISSION:
                UnilinkManager.getInstance(getApplication()).requestPermission(AppManager.getAppManager().currentActivity(), BLEREAUESTCODE);
                break;
            case UnilinkManager.BLE_NOT_OPEN:
                UnilinkManager.getInstance(getApplication()).enableBluetooth(AppManager.getAppManager().currentActivity(), BLEENABLECODE);
                break;
        }
    }


    public int unbindLock(LockKey lockKey) {
        if (UnilinkManager.getInstance(getApplication()).isConnect(lockKey.getMac())) {
            toUnbind(lockKey);
        } else {
            return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
                @Override
                public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                    UTLog.i("scanDevice：" + scanDevice.getAddress());
                    if (lockKey.getMac().equals(scanDevice.getAddress())) {
                        toConnect(lockKey, scanDevice);
                    }
                }

                @Override
                public void onFinish() {
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_not_finded));
                    AppManager.getAppManager().currentActivity().endLoad();
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
                    showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unbindlock_failed));
                    AppManager.getAppManager().currentActivity().endLoad();
                }
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
                        UnilinkManager.getInstance(getApplication()).disconnect(cloudLock.getAddress());
                        AppManager.getAppManager().currentActivity().endLoad();
                    }

                    @Override
                    public void onFailed(int i, String s) {
                        showTip.postValue(getApplication().getString(R.string.lock_tip_ble_unbindlock_failed));
                        UnilinkManager.getInstance(getApplication()).disconnect(cloudLock.getAddress());
                        AppManager.getAppManager().currentActivity().endLoad();

                    }
                });
    }

    private void deleteAdminLock(LockKey lockKey) {
        Disposable disposable = MyRetrofit.get().getCommonApiService().deleteAdminLock(lockKey.getMac())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        deleteLockKey(lockKey);
                        isDeleteSuccess.postValue(true);
                        //删除数据库相应的锁数据
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
    }

    public LiveData<List<LockGroup>> getLockGroups() {
        return LockGroupDaoImpl.get().getAllLockGroup();
    }

    public void createGroup(String newGroupName) {
        MyRetrofit.get().getCommonApiService().addGroup(newGroupName)
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
                        CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                    }
                });
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
                        CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                    }
                });
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
                            CLToast.showAtCenter(getApplication(), "网络错误, 移动失败");
                        }
                    });
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
        mCompositeDisposable.clear();
    }
}
