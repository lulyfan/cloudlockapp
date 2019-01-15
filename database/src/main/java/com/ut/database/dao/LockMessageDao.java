package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.LockMessage;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/25
 * desc   :
 */

@Dao
public interface LockMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<LockMessage> lockMessages);

    @Query("select * from LockMessage order by id desc")
    LiveData<List<LockMessage>> lockMessages();

    @Query("select * from LockMessage where id = :id ")
    LockMessage findLockMessageById(long id);

    @Delete
    void delete(LockMessage... lockMessages);

    @Query("Delete from LockMessage")
    void deleteAll();
}
