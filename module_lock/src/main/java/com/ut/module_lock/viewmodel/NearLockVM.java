package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.NearScanLock;
import com.example.operation.CommonApi;
import com.ut.base.Utils.UTLog;
import com.ut.module_lock.R;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * author : zhouyubin
 * time   : 2018/12/18
 * desc   :
 * version: 1.0
 */
public class NearLockVM extends AndroidViewModel {
    private MutableLiveData<List<NearScanLock>> nearScanLocks = new MutableLiveData<>();//显示搜索到的数据
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();//出错提示
    private MutableLiveData<Boolean> scanning = new MutableLiveData<>();//提示是否正在搜索
    private MutableLiveData<CloudLock> mBindLock = new MutableLiveData<>();//绑定成功的锁，用于修改名称
    private Map<String, ScanDevice> mStringScanDeviceMap = new HashMap<>();//缓存原始搜索数据
    private List<NearScanLock> nearLockList = new ArrayList<>();//存放后台放回的对象
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    public NearLockVM(@NonNull Application application) {
        super(application);
        scanning.setValue(false);
    }

    public MutableLiveData<List<NearScanLock>> getNearScanLocks() {
        return nearScanLocks;
    }

    public MutableLiveData<Boolean> isScanning() {
        return scanning;
    }

    public MutableLiveData<String> getErrorCode() {
        return errorMsg;
    }

    public MutableLiveData<CloudLock> getBindLock() {
        return mBindLock;
    }

    //开始搜索
    public int startScan() {
        scanning.setValue(true);
        nearLockList.clear();
        mStringScanDeviceMap.clear();
        nearScanLocks.setValue(nearLockList);
        return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {

            @Override
            public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                checkLock(scanDevice);
            }

            @Override
            public void onFinish() {
                scanning.postValue(false);
                UTLog.i("scan finish");
            }
        }, 10);
    }

    //获取锁信息
    public void checkLock(ScanDevice scanDevice) {
        mStringScanDeviceMap.put(scanDevice.getAddress(), scanDevice);
        Observable<Result<NearScanLock>> nearScanLock = CommonApi.getLockInfo(scanDevice.getAddress());
        Disposable disposable = nearScanLock.subscribe(nearScanLockResult -> {
            if (nearScanLockResult.code == 200) {
                NearScanLock nearScanLock1 = nearScanLockResult.data;
                ScanDevice scanDevice1 = mStringScanDeviceMap.get(nearScanLock1.getMac());
                if (scanDevice1 != null && !scanDevice1.isActive()) {
                    nearScanLock1.setBindStatus(EnumCollection.BindStatus.UNBIND.ordinal());
                }
                if (nearLockList.contains(nearScanLock1)) {
                    nearLockList.remove(nearScanLock1);
                }
                nearLockList.add(nearScanLock1);
                nearScanLocks.postValue(nearLockList);
            }
        }, throwable -> {//TODO 处理异常
            UTLog.i("get lock info failed");
        });
        mCompositeDisposable.add(disposable);
    }

    //绑定锁
    public void toBindLock(NearScanLock lock) {
        UnilinkManager.getInstance(getApplication()).connect(lock.getMac(), new ConnectListener() {
            @Override
            public void onConnect() {
                actBind(lock);
            }

            @Override
            public void onDisconnect(int i, String s) {
                errorMsg.postValue(getApplication().getString(R.string.lock_tip_bind_failed));
                UTLog.i("ble has been disconnected");
            }
        });

    }

    //执行绑定锁
    private void actBind(NearScanLock lock) {
        ScanDevice scanDevice = mStringScanDeviceMap.get(lock.getMac());
        if (scanDevice == null) {
            errorMsg.postValue(getApplication().getString(R.string.lock_tip_bind_failed));
            return;
        }
        UnilinkManager.getInstance(getApplication()).initLock(scanDevice, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
                Disposable disposable = CommonApi.bindLock(cloudLock.getAddress(), lock.getName(), cloudLock.getAdminPasswordString(),
                        cloudLock.getOpenLockPasswordString(), cloudLock.getEncryptType() + "", cloudLock.getEntryptKeyString())
                        .subscribe(voidResult -> {
                            if (voidResult.code == 200) {
                                confirmInitLock(cloudLock);
                            } else {
                                errorMsg.postValue(voidResult.msg);
                            }
                        }, throwable -> {
                            errorMsg.postValue(getApplication().getString(R.string.lock_tip_bind_failed));
                        });
                mCompositeDisposable.add(disposable);
            }

            @Override
            public void onFailed(int i, String s) {
                errorMsg.postValue(getApplication().getString(R.string.lock_tip_bind_failed));
                UTLog.i("ble init failed");
            }
        });
    }

    //确定绑定
    private void confirmInitLock(CloudLock cloudLock) {
        UnilinkManager.getInstance(getApplication()).confirmInit(cloudLock, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
                mBindLock.postValue(cloudLock);
            }

            @Override
            public void onFailed(int i, String s) {
                UTLog.i("ble confirm init failed");
                errorMsg.postValue(getApplication().getString(R.string.lock_tip_bind_failed));
                Disposable disposable = CommonApi.delAdminKey(cloudLock.getAddress())
                        .subscribe(voidResult -> {
                        });
                mCompositeDisposable.add(disposable);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.dispose();
    }
}