package com.ut.module_msg.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.ut.module_msg.dao.MessageNotificationDao;
import com.ut.module_msg.model.MessageNotification;

@Database(entities = { MessageNotification.class }, version = 1,exportSchema = false)
public abstract class MessageBase extends RoomDatabase {

    private static final String DB_NAME = "MessageBase.db";
    private static volatile MessageBase instance;

   public static synchronized MessageBase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static MessageBase create(final Context context) {
        return Room.databaseBuilder(
            context,
            MessageBase.class,
            DB_NAME).build();
    }

    public abstract MessageNotificationDao getMessageNotificationDao();
}