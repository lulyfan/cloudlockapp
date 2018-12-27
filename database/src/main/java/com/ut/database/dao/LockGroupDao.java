package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.LockGroup;

import java.util.List;

@Dao
public interface LockGroupDao {
    @Query("select * from lock_group ORDER By name ASC")
    LiveData<List<LockGroup>> getAll();

    @Query("select * from lock_group where id = :groupId limit 1")
    LockGroup getById(long groupId);


    @Delete
    void delete(LockGroup lockGroup);

    @Query("delete from lock_group where id >= 0")
    void deleteAll();

    @Update
    void updata(LockGroup lockGroup);

    @Insert
    void insert(LockGroup lockGroup);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(LockGroup... lockGroups);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<LockGroup> lockGroups);

    @Query("UPDATE lock_group SET name = :groupName WHERE id = :groupId")
    void updateGroupName(long groupId, String groupName);

    @Query("DELETE FROM lock_group WHERE id = :groupId")
    void deleteById(long groupId);

}
