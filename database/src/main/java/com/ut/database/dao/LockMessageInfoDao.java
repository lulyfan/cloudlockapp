package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.LockMessageInfo;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/29
 * desc   :
 */
@Dao
public interface LockMessageInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LockMessageInfo... LockMessageInfos);

    @Query("delete from LockMessageInfo")
    void deleteAll();

    @Delete
    void delete(LockMessageInfo detail);

    @Query("select * from LockMessageInfo where lockMac like :mac order by createTime desc ")
    LiveData<List<LockMessageInfo>> findLMessageDetailsByMac(String mac);
}
