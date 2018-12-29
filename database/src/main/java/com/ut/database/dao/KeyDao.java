package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.Key;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/29
 * desc   :
 */
@Dao
public interface KeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertKeys(Key... keys);

    @Query("select * from ut_key where mac like :mac")
    LiveData<List<Key>> findKeysByMac(String mac);

    @Query("delete from ut_key where mac like :mac")
    void deleteAllByMac(String mac);

    @Query("select * from ut_key where keyId =:id")
    LiveData<Key> getKeyById(long id);

}
