package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.Key;
import com.ut.database.entity.NotificationMessage;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/29
 * desc   :
 */
@Dao
public interface DeviceKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceKeys(DeviceKey... keys);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceKeys(List<DeviceKey> keys);

    @Query("select * from device_key where keyType = :type And lockId ==:lockId ORDER BY keyID ASC")
    LiveData<List<DeviceKey>> findDeviceKeysByType(int lockId, int type);

    @Query("SELECT * FROM device_key ORDER BY keyID ASC")
    LiveData<List<DeviceKey>> getAll();

    @Query("SELECT * FROM device_key where deviceId = :deviceId")
    LiveData<DeviceKey> getDeviceKeyByDeviceId(int deviceId);


    @Query("DELETE FROM device_key")
    void deleteAll();

    @Query("DELETE FROM device_key where lockId = :lockId")
    void deleteKeyByLockId(int lockId);

    @Delete
    void delete(DeviceKey... deviceKeys);
}
