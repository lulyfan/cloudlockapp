package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.OfflineRecord;

import java.util.List;

@Dao
public interface OfflineRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(OfflineRecord offlineRecord);

    @Delete
    void delete(OfflineRecord offlineRecord);

    @Query("select * from offlinerecord where id = :id")
    OfflineRecord query(long id);

    @Query("select * from offlinerecord where keyId = :keyId ORDER BY createTime desc limit 10")
    LiveData<List<OfflineRecord>> getRecordsByKeyId(long keyId);
}
