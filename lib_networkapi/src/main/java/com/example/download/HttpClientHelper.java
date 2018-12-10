package com.example.download;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by ZYB on 2017-03-13.
 */

public class HttpClientHelper {
    //用于download，提供下载进度
    public static OkHttpClient getClient(final ProgressListener progressListener) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().body(
                                new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return okHttpClient;
    }
}
