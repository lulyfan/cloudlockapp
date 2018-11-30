package com.ut.module_msg.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.module_msg.model.MessageNotification;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/27
 * desc   :
 */

@Dao
public interface MessageNotificationDao {

    @Query("SELECT * FROM messagenotification")
    List<MessageNotification> getNotifications();

    @Insert
    void insert(MessageNotification...notifications);

    @Update
    void update(MessageNotification...notifications);

    @Delete
    void delete(MessageNotification...notifications);
}
