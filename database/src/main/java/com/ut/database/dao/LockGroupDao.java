package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.LockGroup;
@Dao
public interface LockGroupDao {
    @Query("select * from lockgroup")
    LiveData<LockGroup> getAll();

    @Delete
    void delete(LockGroup lockGroup);

    @Update
    void updata(LockGroup lockGroup);

    @Insert
    void insert(LockGroup lockGroup);
}
