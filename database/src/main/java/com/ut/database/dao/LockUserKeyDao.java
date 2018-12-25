package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.LockUserKey;

import java.util.List;

@Dao
public interface LockUserKeyDao {
    @Query("select * from lockuserkey")
    LiveData<List<LockUserKey>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<LockUserKey> lockUserKeys);

    @Query("delete from lockuserkey where keyId = :keyId")
    void deleteById(long keyId);

    @Query("delete from lockuserkey where keyId >= 0")
    void deleteAll();
}
