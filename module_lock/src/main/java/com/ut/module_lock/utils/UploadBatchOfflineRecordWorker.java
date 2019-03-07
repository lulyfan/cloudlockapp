package com.ut.module_lock.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.google.gson.Gson;
import com.ut.database.dao.OfflineRecordDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.OfflineRecord;

import java.io.IOException;
import java.util.List;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Response;

public class UploadBatchOfflineRecordWorker extends Worker {
    public UploadBatchOfflineRecordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        OfflineRecordDao dao = CloudLockDatabaseHolder.get().getOfflineRecordDao();
        List<OfflineRecord> records = dao.getAll();
        if (records == null || records.size() <= 0) {
            return Result.success();
        }

        Gson gson = new Gson();
        String str = gson.toJson(records);
        Call<com.example.entity.base.Result<Void>> call = MyRetrofit.get().getCommonApiService().addLocalLogsSync(str);
        try {
            Response<com.example.entity.base.Result<Void>> response = call.execute();
            com.example.entity.base.Result<Void> result = response.body();
            if (result == null || !result.isSuccess()) {
                return Result.failure();
            }

            dao.deleteList(records);
            return Result.success();

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
