package com.example.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by ZYB on 2017-03-13.
 */

public interface DownloadApi {
    @GET
    Call<ResponseBody> downloadApk(@Url String url);
}
