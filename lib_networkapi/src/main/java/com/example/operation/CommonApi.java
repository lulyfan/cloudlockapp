package com.example.operation;

import com.example.api.CommonApiService;
import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.google.gson.JsonElement;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.NearScanLock;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ZYB on 2017-03-10.
 */

public class CommonApi {

    //绑定锁
    public static Observable<Result<Void>> bindLock(String mac, String lockName, String adminPwd,
                                                    String blueKey, String encryptType, String encryptKey) {
        Observable<Result<Void>> resultObservable = getCommonApiService().bindLock(mac, lockName, adminPwd, blueKey, encryptType, encryptKey);
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
    public static Observable<Result<JsonElement>> addLog(long lockId, long keyId, int type, int electric) {
        Observable<Result<JsonElement>> resultObservable = getCommonApiService().addLog(lockId, keyId, type, electric);
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
     * 获取锁分组列表
     *
     * @return
     */
    public static Observable<Result<List<LockGroup>>> getGroup() {
        return getCommonApiService().getGroup();
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