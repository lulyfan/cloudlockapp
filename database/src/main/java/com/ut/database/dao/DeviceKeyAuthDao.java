package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/29
 * desc   :
 */
@Dao
public interface DeviceKeyAuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceKeyAuths(DeviceKeyAuth... keys);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceKeyAuths(List<DeviceKeyAuth> keys);

    @Query("SELECT * FROM device_key_auth ORDER BY AuthId ASC")
    LiveData<List<DeviceKeyAuth>> getAll();


    @Query("DELETE FROM device_key_auth")
    void deleteAll();
}
