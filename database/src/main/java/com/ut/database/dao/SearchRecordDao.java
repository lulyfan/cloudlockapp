package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.ut.database.entity.LockKey;
import com.ut.database.entity.SearchRecord;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/12/24
 * desc   :
 * version: 1.0
 */
@Dao
public interface SearchRecordDao {

    @Query("SELECT * FROM search_record ORDER BY id DESC")
    LiveData<List<SearchRecord>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchRecord searchRecord);
}
