package com.example.api;

import com.example.entity.base.Result;
import com.example.entity.data.LoginEntity;
import com.google.gson.JsonElement;
import com.ut.database.entity.User;


import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
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
    Observable<String> getKeyApplyList(@Field("userId") long userId);

    @FormUrlEncoded
    @POST(ApiUrl.pageKey)
    Observable<String> pageKeys(@Field("userId") long userId, @Field("mac") String mac, @Field("currentPage") int currentPage, @Field("pageSize") int pageSize);

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
}