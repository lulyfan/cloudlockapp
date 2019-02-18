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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LockKey> lockKeys);

    @Query("SELECT * FROM lock_key WHERE groupId = :groupId ORDER BY userType ASC")
    LiveData<List<LockKey>> getLockByGroupId(long groupId);

    @Query("SELECT * FROM lock_key WHERE name LIKE :name ORDER BY ABS(groupId-:currentGroupId),userType ASC")
    LiveData<List<LockKey>> getLockByName(String name, long currentGroupId);

    @Query("DELETE FROM lock_key")
    void deleteAll();

    @Query("DELETE FROM lock_key WHERE mac LIKE :lockMac")
    void deleteByMac(String lockMac);

    @Query("DELETE FROM lock_key WHERE keyId = :keyId")
    void deleteByKeyId(int keyId);

    @Query("SELECT * FROM lock_key WHERE mac LIKE :mac limit 1")
    LiveData<LockKey> getByMac(String mac);

    @Query("SELECT * FROM lock_key WHERE mac LIKE :mac limit 1")
    LiveData<LockKey> getLockKeyByMac(String mac);

    @Query("SELECT * FROM lock_key WHERE userType = 1")
    LiveData<List<LockKey>> getAdminLock();

    @Query("UPDATE lock_key SET keyStatus = :keyStatus WHERE keyId = :keyId")
    void updateKeyStatus(int keyId, int keyStatus);

    @Query("UPDATE lock_key SET userType = :userType WHERE keyId = :keyId")
    void updateKeyAuth(int keyId, int userType);  //将钥匙状态改为授权钥匙

    @Query("UPDATE lock_key SET name = :lockName WHERE id = :lockId")
    void updateLockName(int lockId, String lockName);
}
