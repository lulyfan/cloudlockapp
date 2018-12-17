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
import com.google.gson.JsonElement;
import com.ut.base.BaseApplication;
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.entity.OperationRecord;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/14
 * desc   :
 */

@SuppressLint("CheckResult")
public class OperationVm extends AndroidViewModel {
    private int currentPage = 0;
    private static int DEFAULT_PAGE_SIZE = 10;
    private int currentId = -1;

    public OperationVm(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<List<OperationRecord>> operationRecords;

    public MutableLiveData<List<OperationRecord>> getOperationRecords() {
        if (operationRecords == null) {
            operationRecords = new MutableLiveData<>();
            List<OperationRecord> oprs = new ArrayList<>();
            OperationRecord op = new OperationRecord();
            op.setTime("2018/09/06");
            List<OperationRecord.Record> records = new ArrayList<>();
            OperationRecord.Record record = new OperationRecord.Record();
            record.setDesc("11:19:20 使用APP开锁");
            record.setOperator("曹哲君");
            OperationRecord.Record record1 = new OperationRecord.Record();
            record1.setDesc("11:19:20 使用APP开锁");
            record1.setOperator("曹哲君");
            OperationRecord.Record record2 = new OperationRecord.Record();
            record2.setDesc("11:19:20 使用APP开锁");
            record2.setOperator("曹哲君");
            records.add(record);
            records.add(record1);
            records.add(record2);
            op.setRecords(records);
            oprs.add(op);
            operationRecords.postValue(oprs);
        }
        return operationRecords;
    }


    public void loadRecord(String recordType, long id) {
        if (id != currentId) {
            currentPage = 0;
        }
        if (Constance.BY_KEY.equals(recordType)) {
            loadRecordByKey(id);
        } else if (Constance.BY_USER.equals(recordType)) {
            loadRecordByUser(id);
        } else if (Constance.BY_LOCK.equals(recordType)) {
            loadRecordByLock(id);
        }
    }

    public void loadRecordByLock(long lockId) {
    }

    public void loadRecordByUser(long userId) {
    }

    public void loadRecordByKey(long keyId) {
        MyRetrofit.get().getCommonApiService()
                .queryLogsByKey(keyId, currentPage, DEFAULT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(JsonElement::toString)
                .subscribe(json -> {
                    Result<List<OperationRecord>> result = JSON.parseObject(json, new TypeReference<Result<List<OperationRecord>>>() {
                    });
                    if (result.isSuccess()) {
                        getOperationRecords().postValue(result.data);
                        currentPage++;
                    } else {
                        CLToast.showAtCenter(BaseApplication.getAppContext(), String.valueOf(result.msg));
                    }
                }, Throwable::printStackTrace);
    }
}
