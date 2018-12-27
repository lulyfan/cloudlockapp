package com.example.api;

import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.LockMessage;
import com.ut.database.entity.LockMessageInfo;
import com.ut.database.entity.NearScanLock;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockUser;
import com.ut.database.entity.LockUserKey;
import com.ut.database.entity.User;


import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by ZYB on 2017-03-10.
 */

public interface CommonApiService {

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: ShareBike-App",
            "name:User-utShareBike"
    })

    @FormUrlEncoded
    @POST
    Call<Result<JsonElement>> doPost(@FieldMap Map<String, Object> map, @Url String url);

    @FormUrlEncoded
    @POST(ApiUrl.loginUrl)
    Observable<Result<User>> login(@Field("account") String mobile, @Field("password") String pwd);

    @FormUrlEncoded
    @POST(ApiUrl.registerUrl)
    Observable<Result<Void>> register(@Field("mobile") String mobile, @Field("password") String password, @Field("veriCode") String veriCode);

    @FormUrlEncoded
    @POST(ApiUrl.getRegisterVerifyCode)
    Observable<Result<Void>> getRegisterVerifyCode(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST(ApiUrl.getKeyApplyList)
    Observable<JsonObject> getKeyApplyList(@Field("userId") long userId);

    @FormUrlEncoded
    @POST(ApiUrl.pageKey)
    Observable<JsonObject> pageKeys(@Field("userId") long userId, @Field("mac") String mac, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiUrl.applyKey)
    Observable<Result<Void>> applyKey(@Field("userId") long userId, @Field("mac") String mac, @Field("reason") String reason, @Field("ruleType") int ruleType);

    @FormUrlEncoded
    @POST(ApiUrl.ignoreApply)
    Observable<Result<Void>> ignoreApply(@Field("userId") long userId, @Field("applyId") long applyId);

    @Multipart
    @PUT(ApiUrl.uploadImg)
    Observable<Result<String>> uploadImg(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST(ApiUrl.uploadHead)
    Observable<Result<String>> uploadHead(@Field("imgUrl") String imgUrl);

    @FormUrlEncoded
    @POST(ApiUrl.editUserNmae)
    Observable<Result<Void>> editUserName(@Field("name") String userName);

    @FormUrlEncoded
    @POST(ApiUrl.changeUserConfig)
    Observable<Result<Void>> changeUserConfig(@Field("configType") String configType, @Field("operVal") String operVal);

    @FormUrlEncoded
    @POST(ApiUrl.pageAdminLock)
    Observable<Result<List<Lock>>> pageAdminLock(@Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @GET(ApiUrl.getGroup)
    Observable<Result<List<LockGroup>>> getGroup();

    @FormUrlEncoded
    @POST(ApiUrl.addGroup)
    Observable<Result<Integer>> addGroup(@Field("name") String name);

    @FormUrlEncoded
    @POST(ApiUrl.getLockInfoFromGroup)
    Observable<Result<List<Lock>>> getLockInfoFromGroup(@Field("groupId") long groupId);

    @FormUrlEncoded
    @POST(ApiUrl.delGroup)
    Observable<Result<Void>> delGroup(@Field("groupId") long groupId);

    @FormUrlEncoded
    @POST(ApiUrl.updateGroupName)
    Observable<Result<Void>> updateGroupName(@Field("groupId") long groupId, @Field("groupName") String groupName);

    @FormUrlEncoded
    @POST(ApiUrl.unfrozenKey)
    Observable<Result<Void>> unFrozenKey(@Field("keyId") long id);

    @FormUrlEncoded
    @POST(ApiUrl.frozenKey)
    Observable<Result<Void>> frozenKey(@Field("keyId") long keyId);

    @FormUrlEncoded
    @POST(ApiUrl.deleteKey)
    Observable<Result<Void>> deleteKey(@Field("keyId") long keyId, @Field("isRelation") int isRelation);

    @FormUrlEncoded
    @POST(ApiUrl.queryLogsByKey)
    Observable<JsonObject> queryLogsByKey(@Field("keyId") long keyId, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @GET(ApiUrl.getUserInfo)
    Call<Result<User>> getUserInfo();

    @FormUrlEncoded
    @POST(ApiUrl.resetPassword)
    Observable<Result<Void>> resetPassword(@Field("mobile") String mobile, @Field("password") String password, @Field("veriCode") String verifyCode);

    @FormUrlEncoded
    @POST(ApiUrl.queryLogsByLock)
    Observable<JsonObject> queryLogsByLock(@Field("lockId") long lockId, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiUrl.queryLogsByUser)
    Observable<JsonObject> queryLogsByUser(@Field("userId") long userId, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiUrl.getForgetPwdVerifyCode)
    Observable<Result<Void>> getForgetPwdVerifyCode(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST(ApiUrl.editKey)
    Observable<Result<Void>> editKey(@Field("mac") String mac, @Field("keyId") long keyId, @Field("startTime") String startTime, @Field("endTime") String endTime, @Field("weeks") String weeks, @Field("startTimeRange") String startTimeRange, @Field("endTimeRange") String endTimeRange);

    @FormUrlEncoded
    @POST(ApiUrl.changeLockAdmin)
    Observable<Result<Void>> changeLockAdmin(@Field("macs") String macs, @Field("mobile") String mobile, @Field("veriCode") String veriCode);

    @FormUrlEncoded
    @POST(ApiUrl.sendMobileCode)
    Observable<Result<Void>> sendMobileCode(@Field("mobile") String mobile);

    @GET(ApiUrl.getChangeAdminCode)
    Observable<Result<Void>> getChangeAdminCode();

    @GET(ApiUrl.logout)
    Observable<Result<Void>> logout();

    @FormUrlEncoded
    @POST(ApiUrl.pageLockUser)
    Observable<Result<List<LockUser>>> pageLockUser(@Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiUrl.getUserInfoByMobile)
    Observable<Result<User>> getUserInfoByMobile(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST(ApiUrl.pageLockUserKey)
    Observable<Result<List<LockUserKey>>> pageLockUserKey(
            @Field("userId") long userId, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);


    @POST(ApiUrl.getMessage)
    Call<Result<List<LockMessage>>> getMessage();

    @FormUrlEncoded
    @POST(ApiUrl.checkKeyStatus)
    Observable<JsonObject> checkKeyStatus(@Field("applyId") int appId);

    @FormUrlEncoded
    @POST(ApiUrl.bindLock)
    Observable<Result<Void>> bindLock(@Field("mac") String mac, @Field("lockName") String lockName, @Field("adminPwd") String adminPwd,
                                      @Field("blueKey") String blueKey, @Field("encryptType") String encryptType, @Field("encryptKey") String encryptKey);

    @FormUrlEncoded
    @POST(ApiUrl.getLockInfo)
    Observable<Result<NearScanLock>> getLockInfo(@Field("mac") String mac);

    @FormUrlEncoded
    @POST(ApiUrl.delAdminLock)
    Observable<Result<Void>> delAdminLock(@Field("mac") String mac);

    @FormUrlEncoded
    @POST(ApiUrl.updateLockName)
    Observable<Result<Void>> updateLockName(@Field("mac") String mac, @Field("lockName") String lockName);

    @FormUrlEncoded
    @POST(ApiUrl.pageUserLock)
    Observable<Results<LockKey>> pageUserLock(@Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST(ApiUrl.sendKey)
    Observable<Result<Integer>> sendKey(@Field("mobile") String mobile, @Field("mac") String mac, @Field("ruleType") int ruleType,
                                        @Field("keyName") String keyName, @Field("isAdmin") String isAdmin,
                                        @Field("startTime") String startTime, @Field("endTime") String endTime,
                                        @Field("weeks") String weeks, @Field("startTimeRange") String startTimeRange,
                                        @Field("endTimeRange") String endTimeRange);

    @FormUrlEncoded
    @POST(ApiUrl.addLockIntoGroup)
    Observable<Result<Void>> addLockIntoGroup(@Field("macs") String mac, @Field("groupId") long groupId);

    @FormUrlEncoded
    @POST(ApiUrl.delLockFromGroup)
    Observable<Result<Void>> delLockFromGroup(@Field("macs") String mac, @Field("groupId") long groupId);

    @FormUrlEncoded
    @POST(ApiUrl.verifyUserPwd)
    Observable<Result<Void>> verifyUserPwd(@Field("password") String password);

    @FormUrlEncoded
    @POST(ApiUrl.deleteAdminLock)
    Observable<Result<Void>> deleteAdminLock(@Field("mac") String mac);

    @FormUrlEncoded
    @POST(ApiUrl.editLockName)
    Observable<Result<Void>> editLockName(@Field("mac") String mac, @Field("lockName") String lockName);

    @FormUrlEncoded
    @POST(ApiUrl.changeLockGroup)
    Observable<Result<Void>> changeLockGroup(@Field("mac") String mac, @Field("groupId") long groupId);

    @FormUrlEncoded
    @POST(ApiUrl.toAuth)
    Observable<Result<Void>> toAuth(@Field("keyId") long keyId);

    @FormUrlEncoded
    @POST(ApiUrl.cancelAuth)
    Observable<Result<Void>> cancelAuth(@Field("keyId") long keyId);

    @FormUrlEncoded
    @POST(ApiUrl.clearAllKeys)
    Observable<Result<Void>> clearAllKey(@Field("mac") String mac);

    @FormUrlEncoded
    @POST(ApiUrl.getLockMessageInfos)
    Observable<Result<List<LockMessageInfo>>> getLockMessageInfos(@Field("lockMac") String mac);

    @FormUrlEncoded
    @POST(ApiUrl.readMessages)
    Observable<Result<Void>> readMessages(@Field("lockMac") String mac);

    @POST(ApiUrl.isAuth)
    @FormUrlEncoded
    Observable<Result<JsonElement>> isAuth(@Field("mac") String mac);

    @POST(ApiUrl.addLog)
    @FormUrlEncoded
    Observable<Result<JsonElement>> addLog(@Field("lockId") long lockId, @Field("keyId") long keyId, @Field("type") int type);

    @GET(ApiUrl.updateAppVersion)
    Observable<Result<String>> updateVersion();
}