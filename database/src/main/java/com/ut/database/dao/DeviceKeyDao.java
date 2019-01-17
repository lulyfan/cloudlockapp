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

    @Query("select * from device_key where keyType like :type")
    LiveData<List<DeviceKey>> findDeviceKeysByType(int type);

    @Query("SELECT * FROM device_key ORDER BY keyID ASC")
    LiveData<List<DeviceKey>> getAll();


    @Query("DELETE FROM device_key")
    void deleteAll();

    @Delete
    void delete(DeviceKey... deviceKeys);
}
