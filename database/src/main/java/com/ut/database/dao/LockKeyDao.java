package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.LockKey;

import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
@Dao
public interface LockKeyDao {
    @Query("SELECT * FROM lock_key ORDER BY userType ASC")
    LiveData<List<LockKey>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LockKey lockKey);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LockKey... lockKeys);

    @Query("SELECT * FROM lock_key WHERE groupId = :groupId ORDER BY userType ASC")
    LiveData<List<LockKey>> getLockByGroupId(int groupId);

    @Query("SELECT * FROM lock_key WHERE name LIKE :name ORDER BY userType ASC")
    LiveData<List<LockKey>> getLockByName(String name);

    @Query("DELETE FROM lock_key")
    void deleteAll();

    @Query("DELETE FROM lock_key WHERE mac LIKE :lockMac")
    void deleteByMac(String lockMac);

    @Query("SELECT * FROM lock_key WHERE mac LIKE :mac limit 1")
    LockKey getByMac(String mac);

    @Query("SELECT * FROM lock_key WHERE userType = 1")
    LiveData<List<LockKey>> getAdminLock();
}
