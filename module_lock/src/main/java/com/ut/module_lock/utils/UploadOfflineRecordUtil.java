package com.ut.module_lock.utils;

import com.ut.database.dao.OfflineRecordDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.OfflineRecord;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

public class UploadOfflineRecordUtil {

    private static Executor executor = Executors.newSingleThreadExecutor();

    public static void upload(OfflineRecord record) {

        executor.execute(() -> {
            OfflineRecordDao dao = CloudLockDatabaseHolder.get().getOfflineRecordDao();
            long id = dao.insert(record);

            Constraints myConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            Data data = new Data.Builder()
                    .putLong(UploadSingleOfflineRecordWorker.KEY_ID, id)
                    .build();

            OneTimeWorkRequest uploadWork =
                    new OneTimeWorkRequest.Builder(UploadSingleOfflineRecordWorker.class)
                            .setConstraints(myConstraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                            .setInputData(data)
                            .build();
            WorkManager.getInstance().enqueue(uploadWork);
        });

    }

    public static void uploadBatch(OfflineRecord record) {
        executor.execute(() -> {
            OfflineRecordDao dao = CloudLockDatabaseHolder.get().getOfflineRecordDao();
            dao.insert(record);

            Constraints myConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            OneTimeWorkRequest uploadWork =
                    new OneTimeWorkRequest.Builder(UploadBatchOfflineRecordWorker.class)
                            .setConstraints(myConstraints)
                            .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
                            .build();

            WorkManager workManager = WorkManager.getInstance();
            workManager.cancelAllWork();
            workManager.enqueue(uploadWork);
        });
    }
}
