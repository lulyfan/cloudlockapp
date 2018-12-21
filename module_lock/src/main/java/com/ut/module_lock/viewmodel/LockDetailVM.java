package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.ut.database.entity.LockKey;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.CallBack;
import com.ut.unilink.cloudLock.CloudLock;
import com.ut.unilink.cloudLock.ConnectListener;
import com.ut.unilink.cloudLock.ScanDevice;
import com.ut.unilink.cloudLock.ScanListener;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/12/20
 * desc   :
 * version: 1.0
 */
public class LockDetailVM extends AndroidViewModel {
    private MutableLiveData<Boolean> connectStatus = new MutableLiveData<>();
    private LockKey mLockKey = null;
    private volatile boolean isToConnect = false;

    public LockDetailVM(@NonNull Application application) {
        super(application);
        connectStatus.setValue(false);
    }

    public LiveData<Boolean> getConnectStatus() {
        return connectStatus;
    }

    public void setLockKey(LockKey lockKey) {
        this.mLockKey = lockKey;
    }

    public int toOpenLock() {
        return UnilinkManager.getInstance(getApplication()).scan(new ScanListener() {
            @Override
            public void onScan(ScanDevice scanDevice, List<ScanDevice> list) {
                if (!isToConnect && scanDevice.getAddress().equals(mLockKey.getMac())) {
                    toConnect(scanDevice);
                    //TODO 去停止搜索
                }
            }

            @Override
            public void onFinish() {
                //TODO 提示搜索不到
            }
        }, 20);
    }

    public void toConnect(ScanDevice scanDevice) {
        UnilinkManager.getInstance(getApplication()).connect(scanDevice, new ConnectListener() {
            @Override
            public void onConnect() {
                toActOpenLock();
                connectStatus.postValue(true);
            }

            @Override
            public void onDisconnect(int i, String s) {
                connectStatus.postValue(false);
                if (i != -100) {
                    //TODO 提示连接失败
                }
            }
        });
    }

    private void toActOpenLock() {
        CloudLock cloudLock = new CloudLock(mLockKey.getMac());
        cloudLock.setOpenLockPassword(mLockKey.getBlueKey());
        cloudLock.setEncryptType(mLockKey.getEncryptType());
        cloudLock.setEntryptKey(mLockKey.getEncryptKey());
        UnilinkManager.getInstance(getApplication()).openLock(cloudLock, new CallBack() {
            @Override
            public void onSuccess(CloudLock cloudLock) {
                //TODO 提示开锁成功，并发送记录到后台
            }

            @Override
            public void onFailed(int i, String s) {
                //TODO 提示开锁失败
            }
        });
    }
}
