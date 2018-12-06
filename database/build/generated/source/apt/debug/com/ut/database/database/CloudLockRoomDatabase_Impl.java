package com.ut.database.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;

@SuppressWarnings("unchecked")
public class CloudLockRoomDatabase_Impl extends CloudLockRoomDatabase {
  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `lock_key` (`id` TEXT NOT NULL, `name` TEXT, `status` INTEGER NOT NULL, `lockType` INTEGER NOT NULL, `keyType` INTEGER NOT NULL, `userType` INTEGER NOT NULL, `electricity` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"92b7b20442fd8bd2b57bf1dbeb7b876c\")");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `lock_key`");
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsLockKey = new HashMap<String, TableInfo.Column>(7);
        _columnsLockKey.put("id", new TableInfo.Column("id", "TEXT", true, 1));
        _columnsLockKey.put("name", new TableInfo.Column("name", "TEXT", false, 0));
        _columnsLockKey.put("status", new TableInfo.Column("status", "INTEGER", true, 0));
        _columnsLockKey.put("lockType", new TableInfo.Column("lockType", "INTEGER", true, 0));
        _columnsLockKey.put("keyType", new TableInfo.Column("keyType", "INTEGER", true, 0));
        _columnsLockKey.put("userType", new TableInfo.Column("userType", "INTEGER", true, 0));
        _columnsLockKey.put("electricity", new TableInfo.Column("electricity", "INTEGER", true, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLockKey = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLockKey = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLockKey = new TableInfo("lock_key", _columnsLockKey, _foreignKeysLockKey, _indicesLockKey);
        final TableInfo _existingLockKey = TableInfo.read(_db, "lock_key");
        if (! _infoLockKey.equals(_existingLockKey)) {
          throw new IllegalStateException("Migration didn't properly handle lock_key(com.ut.database.entity.LockKey).\n"
                  + " Expected:\n" + _infoLockKey + "\n"
                  + " Found:\n" + _existingLockKey);
        }
      }
    }, "92b7b20442fd8bd2b57bf1dbeb7b876c", "53de157de5b43fa40336be21d6a58cb4");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "lock_key");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `lock_key`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }
}
