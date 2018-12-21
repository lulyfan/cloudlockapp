package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ut.database.entity.LockUserKey;

import java.util.List;

@Dao
public interface LockUserKeyDao {
    @Query("select * from lockuserkey")
    LiveData<List<LockUserKey>> getAll();

    @Query("select * from lockuserkey where keyId = :keyId limit 1")
    LockUserKey getById(int keyId);

    @Insert
    void insert(List<LockUserKey> lockUserKeys);

    @Delete
    void delete(LockUserKey lockUserKey);
}
