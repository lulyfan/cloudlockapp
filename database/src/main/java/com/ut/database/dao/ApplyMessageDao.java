package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.entity.ApplyMessage;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2019/1/10
 * desc   :
 */
@Dao
public interface ApplyMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<ApplyMessage> applyMessages);

    @Query("select * from applymessage order by applyTime desc")
    LiveData<List<ApplyMessage>> loadApplyMessages();

    @Query("delete from applymessage")
    void deleteAll();
}
