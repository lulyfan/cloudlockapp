package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.AppManager;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.database.dao.ORecordDao;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.entity.OperationRecord;
import com.ut.database.entity.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/14
 * desc   :
 */

@SuppressLint("CheckResult")
public class OperationVm extends AndroidViewModel {
    private int currentPage = 1;
    private static int DEFAULT_PAGE_SIZE = 10;
    private long currentId = -1;
    private String currentType;

    private long lockId = 0;

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }

    public OperationVm(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Record>> getRecords(String recordType, long id) {
        if (id != currentId) {
            currentPage = 1;
            currentId = id;
        }
        if (Constance.BY_KEY.equals(recordType)) {
            currentType = Constance.BY_KEY;
            return CloudLockDatabaseHolder.get().recordDao().
                    getRecordsByKeyId(currentId);
        } else if (Constance.BY_LOCK.equals(recordType)) {
            currentType = Constance.BY_LOCK;
            return CloudLockDatabaseHolder.get().recordDao().
                    getRecordsByLockId(currentId);
        }
        return new MutableLiveData<>();
    }

    public void loadRecord(String recordType, long id) {
        if (id != currentId) {
            currentPage = 1;
            currentId = id;
        }

        if (Constance.BY_KEY.equals(recordType)) {
            currentType = Constance.BY_KEY;
            loadRecordByKey(id);
        } else if (Constance.BY_USER.equals(recordType)) {
            currentType = Constance.BY_USER;
            loadRecordByUser(id);
        } else if (Constance.BY_LOCK.equals(recordType)) {
            currentType = Constance.BY_LOCK;
            loadRecordByLock(id);
        }
    }

    private Consumer<Result<List<Record>>> subscriber = result -> {
        if (result.isSuccess()) {
            saveRecords(result.data);
            currentPage++;
            if (result.data.isEmpty()) {
                currentPage--;
            }
        }
    };

    private void loadRecordByLock(long lockId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByLock(lockId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber, new ErrorHandler());
    }

    private void loadRecordByUser(long userId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByUser(userId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber, new ErrorHandler());
    }

    private void loadRecordByKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByKey(keyId, currentPage, DEFAULT_PAGE_SIZE, lockId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber, new ErrorHandler() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (keyId >= 0)
                            super.accept(throwable);
                    }
                });
    }

    private void saveRecords(List<Record> records) {
        Schedulers.io().scheduleDirect(() -> {
            ORecordDao recordDao = CloudLockDatabaseHolder.get().recordDao();
            if (Constance.BY_KEY.equals(currentType)) {
                recordDao.deleteByKeyId(currentId);
            } else if (Constance.BY_LOCK.equals(currentType)) {
                recordDao.deleteByLockId(currentId);
            }
            recordDao.insertRecords(records);
        });
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    private LinkedHashMap<String, List<Record>> handlerMap = null;

    public List<OperationRecord> handlerRecords(List<Record> records) {
        if (handlerMap == null) {
            handlerMap = new LinkedHashMap<>();
        }
        Comparator<Record> comparator = (o1, o2) -> o1.getCreateTime() > o2.getCreateTime() ? -1 : 0;
        Collections.sort(records, comparator);
        for (Record li : records) {
            String date = SystemUtils.getTimeDate(li.getCreateTime());
            if (handlerMap.containsKey(date)) {
                ArrayList<Record> tmps = (ArrayList<Record>) handlerMap.get(date);
                tmps.add(li);
            } else {
                ArrayList<Record> newtmps = new ArrayList<Record>();
                newtmps.add(li);
                handlerMap.put(date, newtmps);
            }
        }
        List<OperationRecord> oprs = new ArrayList<>();
        Set<String> keySet = handlerMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<Record> rs = handlerMap.get(key);
            OperationRecord opr = new OperationRecord();
            opr.setTime(key);
            opr.setRecords(rs);
            oprs.add(opr);
        }
        return oprs;
    }

    public void clear() {
        if (handlerMap != null) {
            handlerMap.clear();
            handlerMap = null;
        }
    }
}
