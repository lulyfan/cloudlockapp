package com.ut.database.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.ut.database.dao.ApplyMessageDao;
import com.ut.database.dao.DeviceKeyDao;
import com.ut.database.dao.KeyDao;
import com.ut.database.dao.LockMessageInfoDao;
import com.ut.database.dao.LockGroupDao;
import com.ut.database.dao.LockKeyDao;
import com.ut.database.dao.LockMessageDao;
import com.ut.database.dao.LockUserDao;
import com.ut.database.dao.LockUserKeyDao;
import com.ut.database.dao.NotifyDao;
import com.ut.database.dao.ORecordDao;
import com.ut.database.dao.SearchRecordDao;
import com.ut.database.dao.UUIDDao;
import com.ut.database.dao.UserDao;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.DeviceKeyAuth;
import com.ut.database.entity.ApplyMessage;
import com.ut.database.entity.Key;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.LockMessage;
import com.ut.database.entity.LockMessageInfo;
import com.ut.database.entity.LockUser;
import com.ut.database.entity.LockUserKey;
import com.ut.database.entity.NotificationMessage;
import com.ut.database.entity.Record;
import com.ut.database.entity.SearchRecord;
import com.ut.database.entity.UUID;
import com.ut.database.entity.User;

/**
 * author : zhouyubin
 * time   : 2018/12/04
 * desc   :
 * version: 1.0
 */
@Database(entities = {LockKey.class, User.class, UUID.class, NotificationMessage.class, LockGroup.class,
        LockUser.class, LockUserKey.class, SearchRecord.class, LockMessage.class, LockMessageInfo.class,
        Key.class, Record.class, DeviceKey.class, ApplyMessage.class}, version = 18)
public abstract class CloudLockRoomDatabase extends RoomDatabase {

    public abstract LockKeyDao lockKeyDao();

    public abstract UserDao userDao();

    public abstract UUIDDao uuidDao();

    public abstract NotifyDao notifyDao();

    public abstract LockGroupDao lockGroupDao();

    public abstract LockUserDao lockUserDao();

    public abstract LockUserKeyDao lockUserKeyDao();

    public abstract SearchRecordDao searchRecordDao();

    public abstract LockMessageDao lockMessageDao();

    public abstract LockMessageInfoDao lockMessageInfoDao();

    public abstract KeyDao keyDao();

    public abstract ORecordDao recordDao();

    public abstract DeviceKeyDao deviceDeyDao();


    public abstract ApplyMessageDao applyMessageDao();
}
