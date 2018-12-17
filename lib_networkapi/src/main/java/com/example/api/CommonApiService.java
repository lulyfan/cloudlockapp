package com.example.api;

import com.example.entity.base.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ut.database.entity.User;


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
    Observable<Result<Void>> applyKey(@Field("userId") long userId, @Field("mac") String mac, @Field("reason") String reason);

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
    @POST(ApiUrl.unfrozenKey)
    Observable<Result<Void>> unForzenKey(@Field("keyId")long id);

    @FormUrlEncoded
    @POST(ApiUrl.frozenKey)
    Observable<Result<Void>> frozenKey(@Field("keyId") long keyId);

    @FormUrlEncoded
    @POST(ApiUrl.deleteKey)
    Observable<Result<Void>> deleteKey(@Field("keyId") long keyId);

    @FormUrlEncoded
    @POST(ApiUrl.queryLogsByKey)
    Observable<JsonObject> queryLogsByKey(@Field("keyId") long keyId, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

    @GET(ApiUrl.getUserInfo)
    Call<Result<User>> getUserInfo();
}