package com.ut.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.ut.database.entity.User;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/12/10
 * desc   :
 */
@Dao
public interface UserDao {

    @Query("SELECT * FROM USER ORDER BY id ASC")
    LiveData<List<User>> findAllUsers();

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    LiveData<User> findLastOne();

    @Query("SELECT * FROM USER WHERE id = :id")
    User findUserById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("DELETE FROM USER WHERE id = :id")
    void deleteUser(long id);

    @Delete
    void deleteUsers(User... users);

    @Update
    void updateUser(User user);

    @Query("DELETE FROM user")
    void deleteAllUsers();
}
