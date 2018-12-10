package com.example.operation;

import com.example.download.ProgressListener;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by ZYB on 2017-03-13.
 */

public class Download {
    public static Call<ResponseBody> downloadApk(ProgressListener progressListener, String url) {
        return MyRetrofit.get().getDownloadPublicService(progressListener).downloadApk(url);
    }
}
