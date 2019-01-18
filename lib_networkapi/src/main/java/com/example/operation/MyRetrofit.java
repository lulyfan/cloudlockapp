package com.example.operation;

import android.text.TextUtils;
import android.util.Log;

import com.example.api.CommonApiService;
import com.example.api.DownloadApi;
import com.example.download.HttpClientHelper;
import com.example.download.ProgressListener;
import com.example.i.INoLoginListener;
import com.example.utils.NetWorkUtil;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.UUID;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ZYB on 2017-03-10.
 */

public class MyRetrofit {
    public static String mBaseUrl = "http://192.168.104.51:8666";//云锁服务器url
    //    public static String mBaseUrl = "http://39.108.50.181:8666";
    private CommonApiService mCommonApiService = null;

    private OkHttpClient mOkHttpClient = null;
    private WebSocketHelper mWebSocketHelper;

    static String getBaseUrl() {
        String baseUrl = "http://192.168.104.51:8666/api/";//大众云锁服务器url
        return baseUrl;
    }

    public static String getUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public static MyRetrofit get() {
        return Holder.instance;
    }

    private MyRetrofit() {
        initClient();
        initWebSocketClient();
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
                        Request request = chain.request();
                        Log.d("Request", "url " + request.url().url().toString());
                        try {
                            com.ut.database.entity.UUID uuid = CloudLockDatabaseHolder.get().getUUIDDao().findUUID();
                            if (uuid == null) {
                                uuid = new UUID();
                                uuid.setUuid(getUUID());
                                CloudLockDatabaseHolder.get().getUUIDDao().insertUUID(uuid);
                            }
                            Log.e("session_token", "----> " + uuid.getUuid());
                            Request builder = request.newBuilder()
                                    .addHeader("mobile_session_flag", "true")
                                    .addHeader("session_token", uuid.getUuid())
                                    .addHeader("appid", "2")
                                    .build();
                            Response response = chain.proceed(builder);
                            handlerResponse(response);
                            return response;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return chain.proceed(request.newBuilder().build());
                    }
                })
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);
        builder.addInterceptor(httpLoggingInterceptor);
        mOkHttpClient = builder.build();
    }

    private void handlerResponse(Response response) throws IOException {
        ResponseBody responseBody = response.body();
        String rBody = null;
        if (responseBody != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            Charset charset = Charset.forName("utf-8");
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(Charset.forName("utf-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            rBody = buffer.clone().readString(charset);
            String json = rBody;
            Log.i("response", json);
            if (TextUtils.isEmpty(json)) return;
            try {
                JSONObject jsonObject = new JSONObject(json);
                int code = jsonObject.getInt("code");
                String msg = jsonObject.optString("msg");
                if (code == 401 || "还未登录".equals(msg)) {
                    if (mNoLoginListener != null) {
                        mNoLoginListener.onNoLogin();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initWebSocketClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        com.ut.database.entity.UUID uuid1 = CloudLockDatabaseHolder.get().getUUIDDao().findUUID();
                        if (uuid1 == null) {
                            return chain.proceed(request).newBuilder().header("Cache-Control", "public,max-age=120").build();
                        }
                        Request builder = request.newBuilder()
                                .addHeader("mobile_session_flag", "true")
                                .addHeader("session_token", uuid1.getUuid())
                                .addHeader("appid", "2")
                                .build();
                        return chain.proceed(builder);
                    }
                })
                .connectTimeout(6, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);
        builder.addInterceptor(httpLoggingInterceptor);
        mWebSocketHelper = new WebSocketHelper(builder.build());
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

    public void setWebSocketListener(WebSocketHelper.WebSocketDataListener webSocketDataListener) {
        mWebSocketHelper.setWebSocketDataListener(webSocketDataListener);
    }

    public void sendUserId(int userId, String appId) {
        mWebSocketHelper.setUserId(userId, appId);
        mWebSocketHelper.initWebSocket(true);
    }

    public void closeWebSocket() {
        mWebSocketHelper.close();
    }

    private static class Holder {
        private static MyRetrofit instance = new MyRetrofit();
    }
}
