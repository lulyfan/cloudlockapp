package com.ut.module_msg.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.ut.module_msg.model.OperationRecord;

import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/28
 * desc   :
 */
public class OperationRecordVm extends ViewModel {

    private MutableLiveData<List<OperationRecord>> operationRecords = null;

    private MutableLiveData<List<OperationRecord>> getOperationRecords(){
        if(operationRecords == null) {
            operationRecords = new MutableLiveData<>();
        }

        return operationRecords;
    }

}
