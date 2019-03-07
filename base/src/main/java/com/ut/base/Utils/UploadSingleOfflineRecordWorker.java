package com.ut.base.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.google.gson.JsonElement;
import com.ut.database.dao.OfflineRecordDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.OfflineRecord;
import com.ut.unilink.util.Log;

import java.io.IOException;
import java.util.Date;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Response;

public class UploadSingleOfflineRecordWorker extends Worker {

    static final String KEY_ID = "recordId";

    public UploadSingleOfflineRecordWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i("---------------------doWork---------------------");

        OfflineRecordDao dao = CloudLockDatabaseHolder.get().getOfflineRecordDao();
        long recordId = getInputData().getLong(KEY_ID, -1);
        OfflineRecord record = dao.query(recordId);
        if (record == null) {
            return Result.failure();
        }

        Call<com.example.entity.base.Result<Void>> call = MyRetrofit.get().getCommonApiService().addLocalLogSync(
                record.getLockId(), record.getKeyId(), record.getType(), record.getOpenLockType(), record.getElectric(),
                record.getCreateTime());

        try {
            Response<com.example.entity.base.Result<Void>> response = call.execute();
            com.example.entity.base.Result<Void> uploadResult = response.body();
            if (uploadResult == null || !uploadResult.isSuccess()) {
                return Result.retry();
            } else {
                dao.delete(record);
                return Result.success();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
