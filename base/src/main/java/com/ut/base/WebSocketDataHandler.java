package com.ut.base;

import com.example.entity.base.Result;
import com.example.operation.WebSocketHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.ut.base.Utils.UTLog;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;

import java.lang.reflect.Type;

public class WebSocketDataHandler implements WebSocketHelper.WebSocketDataListener {

    public static final int CODE_SEND_KEY = 1010;
    public static final int CODE_DELETE_KEY = 1030;
    public static final int CODE_FREEZE_KEY = 1050;      //冻结钥匙
    public static final int CODE_UNFREEZE_KEY = 1060;    //解冻钥匙
    public static final int CODE_AUTH_KEY = 1070;        //授权钥匙
    public static final int CODE_CANCEL_AUTH_KEY = 1080; //取消授权钥匙

    @Override
    public void onReceive(String data) {
        UTLog.d("websocket data:" + data);

        TypeToken<Result<JsonElement>> typeToken = new TypeToken<Result<JsonElement>>() {};

        Gson gson = new Gson();
        Result<JsonElement> result = gson.fromJson(data, typeToken.getType());

        int keyId = -1;
        switch (result.code) {
            case CODE_SEND_KEY:
                LockKey lockKey = gson.fromJson(result.data, LockKey.class);
                LockKeyDaoImpl.get().insert(lockKey);
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

                default:
        }
    }
}
