package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.LockGroup;

import java.util.List;

@Dao
public interface LockGroupDao {
    @Query("select * from lockgroup")
    LiveData<List<LockGroup>> getAll();

    @Query("select * from lockgroup")
    LockGroup getById(long groupId);

    @Delete
    void delete(LockGroup lockGroup);

    @Update
    void update(LockGroup lockGroup);

    @Insert
    void insert(LockGroup lockGroup);
}
