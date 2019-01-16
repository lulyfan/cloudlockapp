package com.ut.unilink.cloudLock;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ut.unilink.util.Log;
import com.zhichu.nativeplugin.ble.Ble;
import com.zhichu.nativeplugin.ble.BleDevice;
import com.zhichu.nativeplugin.ble.IBleNotifyDataCallback;
import com.zhichu.nativeplugin.ble.IConnectCallback;
import com.zhichu.nativeplugin.ble.INotifyCallback;
import com.zhichu.nativeplugin.ble.IWriteCallback;
import com.zhichu.nativeplugin.ble.scan.CloudLockFilter;
import com.zhichu.nativeplugin.ble.scan.GateLockFilter;
import com.zhichu.nativeplugin.ble.scan.IScanCallback;
import com.zhichu.nativeplugin.ble.scan.ScanCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class UTBleLink extends BaseBleLink {

    public static final int CODE_DISCONNECT = -100;
    private static final String UUID_SERVICE = "55540001-5554-0000-0055-4E4954454348";
    private static final String UUID_NOTIFY_CHARACTERISTIC = "55540002-5554-0000-0055-4E4954454348";
    private static final String UUID_WRITE_CHARACTERISTIC = "55540003-5554-0000-0055-4E4954454348";

    private Handler handler;
    private Executor sendExecutor = Executors.newSingleThreadExecutor();
    private LinkedBlockingQueue<SendTask> sendQueue = new LinkedBlockingQueue();
    private boolean isSending;
    private Object sendLock = new Object();

    public UTBleLink(Context context) {
        handler = new Handler(Looper.getMainLooper());
        Ble.get().init(context);

        sendExecutor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        SendTask sendTask = sendQueue.take();
                        write(sendTask.deviceUUID, sendTask.data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 连接指定云锁设备
     * @param address 云锁设备MAC地址，可以从搜索获取的云锁设备{@link ScanDevice#getAddress()}得到
     * @param connectListener 连接结果监听器
     */
    @Override
    public void connect(String address, final ConnectListener connectListener) {
        Ble.get().connect(address, new IConnectCallback() {
            @Override
            public void onConnectSuccess(BleDevice bleDevice) {

                final String deviceUUID = bleDevice.getDeviceUUID();

                if (mConnectionManager != null) {
                    mConnectionManager.onConnect(deviceUUID);
                }

                Ble.get().addBleNotifyDataCallback(deviceUUID, new IBleNotifyDataCallback() {
                    @Override
                    public void onNotify(BleDevice bleDevice, byte[] data, UUID serviceUUID, UUID characteristicUUID) {

//                        Log.i("notify data:" + Log.toUnsignedHex(data));
                        if (mConnectionManager != null) {
                            mConnectionManager.onReceive(bleDevice.getDeviceUUID(), data);
                        }
                    }
                });

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Ble.get().registerNotify(deviceUUID, UUID_SERVICE, UUID_NOTIFY_CHARACTERISTIC,
                                new INotifyCallback() {
                                    @Override
                                    public void onNotify(UUID serviceUUID, UUID characteristicUUID) {
                                        Log.i("registerNotify success");
                                        if (connectListener != null) {
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    connectListener.onConnect();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(BleDevice bleDevice, int code, String message) {
                                        Log.i("registerNotify failed ------ code:" + code + " msg:" + message);
                                    }
                                });
                    }
                }, 0);
            }

            @Override
            public void onDisconnect(BleDevice bleDevice, boolean isActive) {
                handleDisconnect(bleDevice, CODE_DISCONNECT, "", connectListener);
            }

            @Override
            public void onConnect(BleDevice bleDevice) {
            }

            @Override
            public void onFailure(BleDevice bleDevice, int code, String message) {

                handleDisconnect(bleDevice, code, message, connectListener);
            }
        });
    }

    private void handleDisconnect(BleDevice bleDevice, final int code, final String message, final ConnectListener connectListener) {

        Log.i("disconnect mac:" + bleDevice.getDeviceUUID() + " code:" + code + " msg:" + message);
        String deviceUUID = bleDevice.getDeviceUUID();
        close(deviceUUID);

        if (mConnectionManager != null) {
            mConnectionManager.onDisConnect(deviceUUID, code);
        }

        if (connectListener != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    connectListener.onDisconnect(code, message);
                }
            });
        }
    }

    @Override
    public void close(String deviceUUID) {

        Ble.get().disconnect(deviceUUID);
    }

    @Override
    public void send(String deviceUUID, byte[] data) {

        if (data == null || data.length > 20) {
            return;
        }

        SendTask sendTask = new SendTask(deviceUUID, data);
        try {
            sendQueue.put(sendTask);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void write(String deviceUUID, byte[] data) throws InterruptedException {

        synchronized (sendLock) {
            while (isSending) {
                sendLock.wait();
            }

            isSending = true;
            Log.i("start write:" + Log.toUnsignedHex(data));
            Ble.get().write(deviceUUID, UUID_SERVICE, UUID_WRITE_CHARACTERISTIC, data, new IWriteCallback() {
                @Override
                public void onWrite(UUID serviceUUID, UUID characteristicUUID) {
                    Log.i("ble write success");
                    handleSendEnd();

//                    handler.postDelayed(() -> {}, 20);
                }

                @Override
                public void onFailure(BleDevice bleDevice, int code, String message) {
                    Log.i("ble write failed");
                    handleSendEnd();
                }
            });
        }
    }

    private void handleSendEnd() {
        synchronized (sendLock) {
            isSending = false;
            sendLock.notify();
        }
    }

    private void unregisterNotify(String deviceUUID) {
        Ble.get().unregisterNotify(deviceUUID, UUID_SERVICE, UUID_NOTIFY_CHARACTERISTIC, new INotifyCallback() {
            @Override
            public void onNotify(UUID serviceUUID, UUID characteristicUUID) {
                Log.i("unregisterNotify success");
            }

            @Override
            public void onFailure(BleDevice bleDevice, int code, String message) {
                Log.e("unregisterNotify failed ------ code:" + code + "msg:" + message);
            }
        });
    }

    public IConnectionManager getConnectionManager() {
        return mConnectionManager;
    }

    public boolean isConnect(ScanDevice device) {
        return Ble.get().isConnected(device.getAddress());
    }

    class SendTask {
        String deviceUUID;
        byte[] data;

        public SendTask(String deviceUUID, byte[] data) {
            this.deviceUUID = deviceUUID;
            this.data = data;
        }
    }
}
