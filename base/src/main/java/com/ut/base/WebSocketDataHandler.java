package com.ut.base;

import android.annotation.SuppressLint;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.example.operation.WebSocketHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.Key;
import com.ut.database.entity.LockKey;

import java.util.List;

import io.reactivex.schedulers.Schedulers;

@SuppressLint("CheckResult")
public class WebSocketDataHandler implements WebSocketHelper.WebSocketDataListener {

    public static final int CODE_SEND_KEY_RECEIVER = 1010; //发送钥匙的接收端
    public static final int CODE_SEND_KEY_SENDER = 1015; //发送钥匙的发送端
    public static final int CODE_DELETE_KEY = 1030;
    public static final int CODE_FREEZE_KEY = 1050;      //冻结钥匙
    public static final int CODE_UNFREEZE_KEY = 1060;    //解冻钥匙
    public static final int CODE_AUTH_KEY = 1070;        //授权钥匙
    public static final int CODE_CANCEL_AUTH_KEY = 1080; //取消授权钥匙
    public static final int CODE_UPDATE_LOCK_NAME = 1100; //修改锁名称
    public static final int CODE_TRANSFORM_ADMIN = 1200;  //转移管理员

    public static final int CODE_UPDATE_LOCK = 10010;    //刷新锁列表
    public static final int CODE_UPDATE_KEY = 10020;     //刷新钥匙列表
    public static final int CODE_UPDATE_LOCK_KEY = 10030; //刷新锁和钥匙

    public static final int CODE_UPDATE_APPLY_MESSAGE = 20010; //刷新申请消息

    @Override
    public void onReceive(String data) {
        UTLog.d("websocket data:" + data);

        if ("ok".equals(data.toLowerCase())) {
            return;
        }

        TypeToken<Result<JsonElement>> typeToken = new TypeToken<Result<JsonElement>>() {
        };

        Gson gson = new Gson();
        Result<JsonElement> result = gson.fromJson(data, typeToken.getType());

        int keyId = -1;
        switch (result.code) {
            case CODE_SEND_KEY_RECEIVER:
                TypeToken<List<LockKey>> typeToken_ = new TypeToken<List<LockKey>>() {
                };
                List<LockKey> lockKeys_ = gson.fromJson(result.data, typeToken_.getType());
                LockKeyDaoImpl.get().insertAll(lockKeys_);
                break;

            case CODE_SEND_KEY_SENDER:
                Key key = gson.fromJson(result.data, Key.class);
                CloudLockDatabaseHolder.get().getKeyDao().insertKeys(key);
                break;

            case CODE_DELETE_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().deleteByKeyId(keyId);
                CloudLockDatabaseHolder.get().getKeyDao().deleteKeyByKeyId(keyId);
                break;

            case CODE_FREEZE_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyStatus(keyId, EnumCollection.KeyStatus.HAS_FREEZE.ordinal());
                Key k = CloudLockDatabaseHolder.get().getKeyDao().findKeyByKeyId(keyId);
                if (k != null) {
                    k.setStatus(EnumCollection.KeyStatus.HAS_FREEZE.ordinal());
                    CloudLockDatabaseHolder.get().getKeyDao().insertKeys(k);
                }
                break;

            case CODE_UNFREEZE_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyStatus(keyId, EnumCollection.KeyStatus.NORMAL.ordinal());
                k = CloudLockDatabaseHolder.get().getKeyDao().findKeyByKeyId(keyId);
                k.setStatus(EnumCollection.KeyStatus.NORMAL.ordinal());
                CloudLockDatabaseHolder.get().getKeyDao().insertKeys(k);
                break;

            case CODE_AUTH_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyAuth(keyId, EnumCollection.UserType.AUTH.ordinal());
                k = CloudLockDatabaseHolder.get().getKeyDao().findKeyByKeyId(keyId);
                k.setUserType(EnumCollection.UserType.AUTH.ordinal());
                k.setStatus(EnumCollection.KeyStatus.NORMAL.ordinal());
                CloudLockDatabaseHolder.get().getKeyDao().insertKeys(k);
                break;

            case CODE_CANCEL_AUTH_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyAuth(keyId, EnumCollection.UserType.NORMAL.ordinal());
                k = CloudLockDatabaseHolder.get().getKeyDao().findKeyByKeyId(keyId);
                k.setUserType(EnumCollection.UserType.NORMAL.ordinal());
                k.setStatus(EnumCollection.KeyStatus.NORMAL.ordinal());
                CloudLockDatabaseHolder.get().getKeyDao().insertKeys(k);
                break;

            case CODE_UPDATE_LOCK_NAME:
                JsonObject jsonObject = result.data.getAsJsonObject();
                int lockId = jsonObject.get("lockId").getAsInt();
                String lockName = jsonObject.get("lockName").getAsString();
                LockKeyDaoImpl.get().updateLockName(lockId, lockName);
                break;

            case CODE_TRANSFORM_ADMIN:
                TypeToken<List<LockKey>> typeToken1 = new TypeToken<List<LockKey>>() {
                };
                List<LockKey> lockKeys = gson.fromJson(result.data, typeToken1.getType());
                LockKeyDaoImpl.get().insertAll(lockKeys);
                break;

            case CODE_UPDATE_LOCK:
                updateLock();
                updateMessage();
                break;

            case CODE_UPDATE_KEY:
                String mac = result.data.getAsString();
                updateLockAndKey(BaseApplication.getUser().getId(), mac);
                break;

            case CODE_UPDATE_LOCK_KEY:
                String mac1 = result.data.getAsString();
                updateLockAndKey(BaseApplication.getUser().getId(), mac1);
                break;

            case CODE_UPDATE_APPLY_MESSAGE:
                updateApplyMessage();
                break;

            default:
        }
    }

    private static void updateLock() {
        MyRetrofit.get().getCommonApiService().pageUserLock(1, -1)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException();
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(lockKeyResults -> {
                    LockKeyDaoImpl.get().deleteAll();
                    LockKeyDaoImpl.get().insertAll(lockKeyResults.data);
                }, throwable -> {

                });
    }

    private static void updateKey(long userId, String mac) {
        MyRetrofit.get().getCommonApiService().pageKeys(userId, mac, 1, -1)
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException();
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> {
                    CloudLockDatabaseHolder.get().getKeyDao().deleteAllByMac(mac);
                    CloudLockDatabaseHolder.get().getKeyDao().insertKeys(listResult.data);
                }, throwable -> {

                });
    }

    private static void updateMessage() {
        MyRetrofit.get().getCommonApiService().getMessage()
                .doOnNext(stringResult -> {
                    if (stringResult == null) {
                        throw new NullPointerException();
                    }

                    if (!stringResult.isSuccess()) {
                        throw new Exception(stringResult.msg);
                    }
                })
                .subscribe(listResult -> {
                    if (listResult.isSuccess()) {
                        CloudLockDatabaseHolder.get().getLockMessageDao().insert(listResult.data);
                    }
                }, throwable -> {

                });
    }

    private static void updateLockAndKey(long userId, String mac) {
        updateLock();
        updateKey(userId, mac);
        updateMessage();
    }

    private void updateApplyMessage() {
        MyRetrofit.get().getCommonApiService().getKeyApplyList()
                .subscribeOn(Schedulers.io())
                .subscribe(listResult -> {
                    if (listResult.isSuccess()) {
                        CloudLockDatabaseHolder.get().getApplyMessageDao().deleteAll();
                        CloudLockDatabaseHolder.get().getApplyMessageDao().insert(listResult.data);
                    }
                }, Throwable::printStackTrace);
    }
}
