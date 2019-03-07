package com.ut.module_lock.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.utils.NetWorkUtil;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.entity.OfflineRecord;
import com.ut.database.entity.Record;
import com.ut.module_lock.R;
import com.ut.module_lock.adapter.ORListAdapter;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityOperationRecordBinding;
import com.ut.module_lock.entity.OperationRecord;
import com.ut.module_lock.viewmodel.OperationVm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/3
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.OPERATION_RECORD)
public class OperationRecordAcitivity extends BaseActivity {

    private ActivityOperationRecordBinding mBinding = null;
    private OperationVm operationVm = null;
    private ORListAdapter listAdapter = null;
    private List<OperationRecord> oprs = new ArrayList<>();
    private long currentId = -1L;
    private String recordType = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_operation_record);
        operationVm = ViewModelProviders.of(this).get(OperationVm.class);
        initDarkToolbar();
        setTitle(R.string.lock_operation_record);
        handlerIntent();
        initView();
        handleData();

        mBinding.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mBinding.refreshLayout.setOnRefreshListener(() -> {
            operationVm.setCurrentPage(1);
            operationVm.loadRecord(recordType, currentId);
            mBinding.refreshLayout.postDelayed(() -> mBinding.refreshLayout.setRefreshing(false), 1200L);
        });

        mBinding.operationRecordList.setOnLoadMoreListener(() -> {
            operationVm.loadRecord(recordType, currentId);
            mBinding.operationRecordList.postDelayed(() -> {
                mBinding.operationRecordList.setLoadCompleted();
            }, 800L);
        });
    }

    private void handleData() {
        operationVm.getRecords(recordType, currentId).observe(this, records -> {
            if (records == null) return;
            oprs.clear();
            if(NetWorkUtil.isNetworkAvailable(this)) {
                updateList(records);
            } else {
                //离线时查找离线记录
                operationVm.getOfflineRecords(currentId, (data) -> {
                    records.addAll(getOfflineData(data));
                    updateList(records);
                });
            }
        });
        operationVm.loadRecord(recordType, currentId);
    }

    private void updateList(List<Record> records) {
        if (mBinding.operationRecordList.isLoading()) {
            List<OperationRecord> tmps = operationVm.handlerRecords(records);
            oprs.addAll(tmps);
        } else {
            operationVm.clear();
            List<OperationRecord> tmps = operationVm.handlerRecords(records);
            oprs.addAll(tmps);
        }
        mBinding.nodata.setVisibility(oprs.isEmpty() ? View.VISIBLE : View.GONE);
        listAdapter.notifyDataSetChanged();
    }

    private List<Record> getOfflineData(List<OfflineRecord> values) {
        List<Record> offlineRecords = new ArrayList<>();
        if (values == null) return offlineRecords;
        offlineRecords.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        for (OfflineRecord offlineRecord : values) {
            Record record = new Record();
            record.setLockId(offlineRecord.getLockId());
            record.setOpenLockType(offlineRecord.getOpenLockType());
            record.setTime(sdf.format(new Date(offlineRecord.getCreateTime())));
            record.setCreateTime(offlineRecord.getCreateTime());
            if (offlineRecord.getOpenLockType() == 0) {
                record.setDescription("蓝牙手动开锁");
            } else if (offlineRecord.getOpenLockType() == 1) {
                record.setDescription("蓝牙无感开锁");
            }
            record.setKeyId(offlineRecord.getKeyId());
            record.setKeyName(BaseApplication.getUser().name);
            offlineRecords.add(record);
        }
        return offlineRecords;
    }

    private void initView() {
        listAdapter = new ORListAdapter(this, oprs);
        mBinding.operationRecordList.setAdapter(listAdapter);
    }

    private void handlerIntent() {
        recordType = getIntent().getStringExtra(Constance.RECORD_TYPE);
        boolean isGateRecord = getIntent().getBooleanExtra(Constance.FIND_GATE_RECORD, false);
        operationVm.setGateRecord(isGateRecord);
        if (getIntent().hasExtra(Constance.KEY_ID)) {
            currentId = getIntent().getLongExtra(Constance.KEY_ID, currentId);
            if (getIntent().hasExtra(Constance.LOCK_ID)) {
                operationVm.setLockId(getIntent().getLongExtra(Constance.LOCK_ID, 0L));
            }
        } else if (getIntent().hasExtra(Constance.USER_ID)) {
            currentId = getIntent().getLongExtra(Constance.USER_ID, currentId);
        } else if (getIntent().hasExtra(Constance.LOCK_ID)) {
            currentId = getIntent().getLongExtra(Constance.LOCK_ID, currentId);
        }
    }
}
