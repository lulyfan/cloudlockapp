package com.example.operation;

import com.example.api.CommonApiService;
import com.example.api.DownloadApi;
import com.example.converter.CustomerGsonConverterFactory;
import com.example.download.HttpClientHelper;
import com.example.download.ProgressListener;
import com.example.i.INoLoginListener;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.UUID;
import com.utbike.testretrofit.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ZYB on 2017-03-10.
 */

public class MyRetrofit {
    public static String mBaseUrl = "http://192.168.104.51:8666";//云锁服务器url
    private CommonApiService mCommonApiService = null;

    private OkHttpClient mOkHttpClient = null;

    static String getBaseUrl() {
        String baseUrl = "http://192.168.104.51:8666/api/";//大众云锁服务器url
        return baseUrl;
    }

    private static String UUID = java.util.UUID.randomUUID().toString();

    public static String getUUID() {
        return UUID;
    }

    public static MyRetrofit get() {
        return Holder.instance;
    }

    private MyRetrofit() {
        initClient();
        initApiCommonServicet();
    }

    private INoLoginListener mNoLoginListener = null;

    public void setNoLoginListener(INoLoginListener noLoginListener) {
        this.mNoLoginListener = noLoginListener;
    }

    private void initClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        com.ut.database.entity.UUID uuid = CloudLockDatabaseHolder.get().getUUIDDao().findUUID();
                        if (uuid == null) {
                            uuid = new UUID();
                            uuid.setUuid(MyRetrofit.UUID);
                            CloudLockDatabaseHolder.get().getUUIDDao().insertUUID(uuid);
                        }
                        Request request = chain.request();
                        Request.Builder builder = request.newBuilder();
                        Request builder1 = builder.addHeader("mobile_session_flag", "true")
                                .addHeader("session_token", uuid.getUuid())
                                .build();
                        return chain.proceed(builder1);
                    }
                })
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(httpLoggingInterceptor);
        }
        mOkHttpClient = builder.build();
    }


    private void initApiCommonServicet() {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mCommonApiService = mRetrofit.create(CommonApiService.class);
    }

    public CommonApiService getCommonApiService() {
        return mCommonApiService;
    }


    public DownloadApi getDownloadPublicService(ProgressListener progressListener) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .client(HttpClientHelper.getClient(progressListener))
                .build();
        return retrofit.create(DownloadApi.class);
    }

    private static class Holder {
        private static MyRetrofit instance = new MyRetrofit();
    }

    private INoLoginListener mNoLoginListener1 = new INoLoginListener() {
        @Override
        public void onNoLogin() {
            if (mNoLoginListener != null) {
                mNoLoginListener.onNoLogin();
            }
        }
    };
}
