package com.ut.database.daoImpl;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.ut.database.dao.SearchRecordDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.database.entity.SearchRecord;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * author : zhouyubin
 * time   : 2018/12/24
 * desc   :
 * version: 1.0
 */
public class SearchRecordDaoImpl {

    private SearchRecordDao mSearchRecordDao;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private SearchRecordDaoImpl() {
        mSearchRecordDao = CloudLockDatabaseHolder.get().getSearchRecordDao();
    }

    public static SearchRecordDaoImpl get() {
        return Holder.searchRecordDao;
    }

    private static class Holder {
        protected static SearchRecordDaoImpl searchRecordDao = new SearchRecordDaoImpl();
    }

    public LiveData<List<SearchRecord>> getAll() {
        return mSearchRecordDao.getAll();
    }

    public void insert(final SearchRecord searchRecord) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                mSearchRecordDao.insert(searchRecord);
            }
        });
    }

}
