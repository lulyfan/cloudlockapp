package com.ut.module_lock.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.entity.base.Result;
import com.example.operation.MyRetrofit;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.entity.OperationRecord;
import com.ut.module_lock.entity.Record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
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

    public OperationVm(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<OperationRecord>> operationRecords;

    public MutableLiveData<List<OperationRecord>> getOperationRecords() {
        if (operationRecords == null) {
            operationRecords = new MutableLiveData<>();
        }
        return operationRecords;
    }


    public void loadRecord(String recordType, long id) {
        if (id != currentId) {
            currentPage = 1;
            currentId = id;
        }
        if (Constance.BY_KEY.equals(recordType)) {
            loadRecordByKey(id);
        } else if (Constance.BY_USER.equals(recordType)) {
            loadRecordByUser(id);
        } else if (Constance.BY_LOCK.equals(recordType)) {
            loadRecordByLock(id);
        }
    }

    private void loadRecordByLock(long lockId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByLock(lockId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(jsonObject -> {
                    Result<List<Record>> result = JSON.parseObject(jsonObject.toString(), new TypeReference<Result<List<Record>>>() {
                    });
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        List<OperationRecord> operationRecords = handlerRecords(result.data);
                        getOperationRecords().postValue(operationRecords);
                    }
                }, new ErrorHandler());
    }

    private void loadRecordByUser(long userId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByUser(userId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(jsonObject -> {
                    Result<List<Record>> result = JSON.parseObject(jsonObject.toString(), new TypeReference<Result<List<Record>>>() {
                    });
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        List<OperationRecord> operationRecords = handlerRecords(result.data);
                        getOperationRecords().postValue(operationRecords);
                    }
                }, new ErrorHandler());
    }

    private void loadRecordByKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByKey(keyId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map(jsonObject -> {
                    Result<List<Record>> result = JSON.parseObject(jsonObject.toString(), new TypeReference<Result<List<Record>>>() {
                    });
                    return result;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result.isSuccess()) {
                        List<OperationRecord> operationRecords = handlerRecords(result.data);
                        getOperationRecords().postValue(operationRecords);
                    }
                }, new ErrorHandler());
    }

    private List<OperationRecord> handlerRecords(List<Record> records) {
        LinkedHashMap<String, List<Record>> map = new LinkedHashMap<>();
        Comparator<Record> comparator = (o1, o2) -> o1.getCreateTime() > o2.getCreateTime() ? 0 : 1;
        for (Record li : records) {
            String date = SystemUtils.getTimeDate(li.getCreateTime());
            if (map.containsKey(date)) {
                ArrayList<Record> tmps = (ArrayList<Record>) map.get(date);
                tmps.add(li);
                Collections.sort(tmps, comparator);
            } else {
                ArrayList<Record> newtmps = new ArrayList<Record>();
                newtmps.add(li);
                map.put(date, newtmps);
            }
        }
        List<OperationRecord> oprs = new ArrayList<>();
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            List<Record> rs = map.get(key);
            OperationRecord opr = new OperationRecord();
            opr.setTime(key);
            opr.setRecords(rs);
            oprs.add(opr);
        }
        return oprs;
    }
}
