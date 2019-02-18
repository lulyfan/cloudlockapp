package com.example.operation;

import com.example.api.ApiUrl;
import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.example.entity.entity.Cloudlockenterpriseinfo;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.NearScanLock;
import com.ut.database.entity.Record;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.Field;

/**
 * Created by ZYB on 2017-03-10.
 */

public class CommonApi {
    private static Gson mGson = new Gson();

    //绑定锁
    public static Observable<Result<Void>> bindLock(String mac, String lockName, String adminPwd,
                                                    String blueKey, String encryptType, String encryptKey,
                                                    String lockVersion) {
        Observable<Result<Void>> resultObservable = getCommonApiService().bindLock(mac, lockName, adminPwd,
                blueKey, encryptType, encryptKey, lockVersion);
        return ObjectLoader.observe(resultObservable);
    }

    //获取锁信息
    public static Observable<Result<NearScanLock>> getLockInfo(String mac) {
        Observable<Result<NearScanLock>> resultObservable = getCommonApiService().getLockInfo(mac);
        return ObjectLoader.observe(resultObservable);
    }

    //删除管理员钥匙
    public static Observable<Result<Void>> delAdminKey(String mac) {
        Observable<Result<Void>> resultObservable = getCommonApiService().delAdminLock(mac);
        return ObjectLoader.observe(resultObservable);
    }

    //更新锁名称
    public static Observable<Result<Void>> updateLockName(String mac, String name) {
        Observable<Result<Void>> resultObservable = getCommonApiService().updateLockName(mac, name);
        return ObjectLoader.observe(resultObservable);
    }

    //判断是否允许开锁
    public static Observable<Result<JsonElement>> isAuth(String mac) {
        Observable<Result<JsonElement>> resultObservable = getCommonApiService().isAuth(mac);
        return ObjectLoader.observe(resultObservable);
    }

    //添加开锁记录
    public static Observable<Result<JsonElement>> addLog(long lockId, long keyId, int type, int openLockType, int electric) {
        Observable<Result<JsonElement>> resultObservable = getCommonApiService().addLog(lockId, keyId, type, openLockType, electric);
        return ObjectLoader.observe(resultObservable);
    }

    //删除锁分组
    public static Observable<Result<Void>> delLockFromGroup(String mac, long groupId) {
        Observable<Result<Void>> resultObservable = getCommonApiService().delLockFromGroup(mac, groupId);
        return ObjectLoader.observe(resultObservable);
    }

    /**
     * 获取锁列表
     *
     * @param index    起始页
     * @param pageSize 每页数量 ,-1为全部获取
     * @return
     */
    public static Observable<Results<LockKey>> pageUserLock(int index, int pageSize) {
        Observable<Results<LockKey>> resultObservable = getCommonApiService().pageUserLock(index, pageSize);
        return resultObservable;
    }

    /**
     * 设置锁是否可以触摸开锁
     *
     * @param keyId
     * @param canOpen
     * @return
     */
    public static Observable<Result<Void>> setCanOpen(long keyId, int canOpen) {
        Observable<Result<Void>> resultObservable = getCommonApiService().setCanOpen(keyId, canOpen);
        return ObjectLoader.observe(resultObservable);
    }

    public static Observable<Result<Void>> updateKeyInfo(DeviceKey deviceKey) {
        List<DeviceKey> list = new ArrayList<>();
        list.add(deviceKey);
        String volist = mGson.toJson(list);
        Observable<Result<Void>> resultObservable = getCommonApiService().updateKeyInfo(volist);
        return resultObservable;
    }

    /**
     * @param type   钥匙类型，为0时可以拿到所有的钥匙
     * @param lockId
     * @return
     */
    public static Observable<Results<DeviceKey>> getDeviceKeyListByType(int type, String lockId) {
        int lockIdInt = Integer.parseInt(lockId);
        Observable<Results<DeviceKey>> resultsObservable = getCommonApiService().getDeviceKeyListByType(type, lockIdInt);
        return resultsObservable;
    }

    public static Observable<Result<Void>> initLockKey(int lockId, List<DeviceKey> list) {
        String volist = mGson.toJson(list);
        Observable<Result<Void>> resultsObservable = getCommonApiService().initLockKey(lockId, volist);
        return resultsObservable;
    }

    public static Observable<Result<Void>> insertInnerLockLog(List<Record> list) {
        String volist = mGson.toJson(list);
        Observable<Result<Void>> resultsObservable = getCommonApiService().insertInnerLockLog(volist);
        return ObjectLoader.observe(resultsObservable);
    }

    public static Observable<Result<Void>> delDeviceKeyInfo(String lockId, int localKey) {
        int lockID = Integer.parseInt(lockId);
        Observable<Result<Void>> resultsObservable = getCommonApiService().delKeyInfo(lockID, localKey);
        return ObjectLoader.observe(resultsObservable);
    }

    /**
     * 获取锁分组列表
     *
     * @return
     */
    public static Observable<Result<List<LockGroup>>> getGroup() {
        return getCommonApiService().getGroup();
    }


    public static Observable<Cloudlockenterpriseinfo> getCloudlockenterpriseinfo() {
        return ObjectLoader.observe(getCommonApiService().
                getInfoFromUrl(ApiUrl.getCloudlockenterpriseinfo));
    }

    protected static CommonApiService getCommonApiService() {
        return MyRetrofit.get().getCommonApiService();
    }
}

class ObjectLoader {


    protected static <T> Observable<T> observe(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
//                .onErrorReturn(new Function<Throwable, T>() {
//                    @Override
//                    public T apply(Throwable throwable) throws Exception {
//                        throwable.printStackTrace();
//                        return null;
//                    }
//                })
                ;
    }
}