package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.NotificationMessage;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/19
 * desc   :
 */

@Dao
public interface NotifyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNotiMessage(NotificationMessage... message);

    @Update
    void updateNotiMessage(NotificationMessage message);

    @Query("SELECT * FROM NOTIFICATIONMESSAGE")
    LiveData<List<NotificationMessage>> getNotificationMessages();

    @Query("DELETE FROM NOTIFICATIONMESSAGE WHERE userId = :userId")
    void deleteNotiMessage(long userId);

    @Query("DELETE FROM NOTIFICATIONMESSAGE")
    void deleteAllNotiMessages();

    @Delete
    void delete(NotificationMessage... messages);
}
