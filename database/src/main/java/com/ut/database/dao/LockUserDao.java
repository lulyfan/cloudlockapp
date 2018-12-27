package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.LockUser;

import java.util.List;

@Dao
public interface LockUserDao {
    @Query("select * from lockuser")
    LiveData<List<LockUser>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<LockUser> lockUsers);

    @Query("delete from lockuser where userId >= 0")
    void deleteAll();
}
