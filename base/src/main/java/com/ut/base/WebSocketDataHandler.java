package com.ut.base;

import com.example.entity.base.Result;
import com.example.operation.WebSocketHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ut.base.Utils.UTLog;
import com.ut.database.dao.KeyDao_Impl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.Key;
import com.ut.database.entity.LockKey;

import java.util.List;

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

    @Override
    public void onReceive(String data) {
        UTLog.d("websocket data:" + data);

        TypeToken<Result<JsonElement>> typeToken = new TypeToken<Result<JsonElement>>() {};

        Gson gson = new Gson();
        Result<JsonElement> result = gson.fromJson(data, typeToken.getType());

        int keyId = -1;
        switch (result.code) {
            case CODE_SEND_KEY_RECEIVER:
                TypeToken<List<LockKey>> typeToken_ = new TypeToken<List<LockKey>>() {};
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
                break;

            case CODE_FREEZE_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyStatus(keyId, EnumCollection.KeyStatus.HAS_FREEZE.ordinal());
                break;

            case CODE_UNFREEZE_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyStatus(keyId, EnumCollection.KeyStatus.NORMAL.ordinal());
                break;

            case CODE_AUTH_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyAuth(keyId, EnumCollection.UserType.AUTH.ordinal());
                break;

            case CODE_CANCEL_AUTH_KEY:
                keyId = gson.fromJson(result.data, Integer.class);
                LockKeyDaoImpl.get().updateKeyAuth(keyId, EnumCollection.UserType.NORMAL.ordinal());
                break;

            case CODE_UPDATE_LOCK_NAME:
                JsonObject jsonObject = result.data.getAsJsonObject();
                int lockId = jsonObject.get("lockId").getAsInt();
                String lockName = jsonObject.get("lockName").getAsString();
                LockKeyDaoImpl.get().updateLockName(lockId, lockName);
                break;

            case CODE_TRANSFORM_ADMIN:
                TypeToken<List<LockKey>> typeToken1 = new TypeToken<List<LockKey>>() {};
                List<LockKey> lockKeys = gson.fromJson(result.data, typeToken1.getType());
                LockKeyDaoImpl.get().insertAll(lockKeys);
                break;

                default:
        }
    }
}
