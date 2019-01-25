package com.ut.module_lock.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.ut.database.dao.SearchRecordDao;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.daoImpl.SearchRecordDaoImpl;
import com.ut.database.entity.LockKey;
import com.ut.database.entity.SearchRecord;

import java.util.List;

/**
 * author : zhouyubin
 * time   : 2018/12/24
 * desc   :
 * version: 1.0
 */
public class SearchLockVM extends AndroidViewModel {

    public SearchLockVM(@NonNull Application application) {
        super(application);
    }

    /**
     * 获取搜索的锁列表
     *
     * @param keyWord 搜索的字段
     * @return
     */
    public LiveData<List<LockKey>> getLockKeys(String keyWord, long currentGroupId) {
        return LockKeyDaoImpl.get().getLockByName(keyWord, currentGroupId);
    }

    /**
     * 获取搜索记录
     *
     * @return
     */
    public LiveData<List<SearchRecord>> getSearchRecords() {
        return SearchRecordDaoImpl.get().getAll();
    }

    /**
     * 搜索搜索记录
     *
     * @param searchWord
     */
    public void insertSearchRecord(String searchWord) {
        SearchRecordDaoImpl.get().insert(new SearchRecord(searchWord));
    }
}
