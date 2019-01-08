package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.Record;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/29
 * desc   :
 */

@Dao
public interface ORecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecords(Record... records);

    @Query("select * from record where lockId = :lockId")
    LiveData<List<Record>> getRecordsByLockId(long lockId);

    @Query("select * from record where keyId = :keyId")
    LiveData<List<Record>> getRecordsByKeyId(long keyId);

    @Query("delete from record")
    void deleteAll();
}
