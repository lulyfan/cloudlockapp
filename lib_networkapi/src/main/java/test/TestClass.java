package test;

import java.io.File;


import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;

/**
 * Created by ZYB on 2017-03-10.
 */

public class TestClass {
//    private OkHttpClient getCacheOkHttpClient(Context context){
//        final File baseDir = new File("E://");
//        final File cacheDir = new File(baseDir, "HttpResponseCache");
//        Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);   //缓存可用大小为10M
//
//        Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = chain -> {
//            Request request = chain.request();
//                request = request.newBuilder()
//                        .cacheControl(CacheControl.FORCE_CACHE)
//                        .build();
//
//            Response originalResponse = chain.proceed(request);
//            if (NetWorkUtils.isNetWorkAvailable()) {
//                int maxAge = 60;                  //在线缓存一分钟
//                return originalResponse.newBuilder()
//                        .removeHeader("Pragma")
//                        .removeHeader("Cache-Control")
//                        .header("Cache-Control", "public, max-age=" + maxAge)
//                        .build();
//
//            } else {
//                int maxStale = 60 * 60 * 24 * 4 * 7;     //离线缓存4周
//                return originalResponse.newBuilder()
//                        .removeHeader("Pragma")
//                        .removeHeader("Cache-Control")
//                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
//                        .build();
//            }
//        };
//
//        return new OkHttpClient.Builder()
//                .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//                .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//                .cache(cache)
//                .build();
//    }

    //    public static class NetWorkUtils{
//        public static boolean isNetWorkAvailable(){
//            return true;
//        }
//    }
    Observable observable = Observable.create(new Observable.OnSubscribe() {
        @Override
        public void call(Object o) {

        }
    });
}
