package com.ut.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ut.database.entity.UUID;

/**
 * author : chenjiajun
 * time   : 2018/12/14
 * desc   :
 */
@Dao
public interface UUIDDao {
    @Query("SELECT * FROM uuid  ORDER BY id desc LIMIT 1")
    UUID findUUID();

    @Insert
    void insertUUID(UUID uuid);

    @Delete
    void deleteUUID(UUID uuid);
}
