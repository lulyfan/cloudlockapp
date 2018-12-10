package com.example.api;

import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.example.entity.data.AuthData;
import com.example.entity.data.AuthLog;
import com.example.entity.data.LockInfo;
import com.example.entity.data.LoginEntity;
import com.example.entity.data.OperateLog;
import com.example.entity.data.VersionInfo;
import com.google.gson.JsonElement;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by ZYB on 2017-03-10.
 */

public interface CommonApiService {

    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: ShareBike-App",
            "name:utShareBike"
    })

    @FormUrlEncoded
    @POST
    Call<Result<JsonElement>> doPost(@FieldMap Map<String, Object> map, @Url String url);

    @FormUrlEncoded
    @POST(ApiUrl.loginUrl)
    Call<Result<LoginEntity>> login(@Field("mobile") String mobile, @Field("password") String pwd);
}