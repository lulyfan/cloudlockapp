package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.adapter.ORListAdapter;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityOperationRecordBinding;
import com.ut.module_lock.entity.OperationRecord;
import com.ut.module_lock.viewmodel.OperationVm;

import java.util.ArrayList;
import java.util.List;

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
        initDarkToolbar();
        setTitle(R.string.lock_operation_record);
        handlerIntent();
        initView();
        operationVm = ViewModelProviders.of(this).get(OperationVm.class);
        operationVm.getOperationRecords(recordType, currentId).observe(this, operationRecords -> {
            if (operationRecords == null || operationRecords.isEmpty()) return;
            oprs.clear();
            oprs.addAll(operationRecords);
            listAdapter.notifyDataSetChanged();
        });
        operationVm.loadRecord(recordType, currentId);

        mBinding.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mBinding.refreshLayout.setOnRefreshListener(() -> {
            operationVm.loadRecord(recordType, currentId);
            mBinding.refreshLayout.postDelayed(() -> mBinding.refreshLayout.setRefreshing(false), 1000L);
        });
    }

    private void initView() {
        listAdapter = new ORListAdapter(this, oprs);
        mBinding.operationRecordList.setAdapter(listAdapter);
    }

    private void handlerIntent() {
        recordType = getIntent().getStringExtra(Constance.RECORD_TYPE);
        if (getIntent().hasExtra(Constance.KEY_ID)) {
            currentId = getIntent().getLongExtra(Constance.KEY_ID, currentId);
        } else if (getIntent().hasExtra(Constance.USER_ID)) {
            currentId = getIntent().getLongExtra(Constance.USER_ID, currentId);
        } else if (getIntent().hasExtra(Constance.LOCK_ID)) {
            currentId = getIntent().getLongExtra(Constance.LOCK_ID, currentId);
        }
    }
}
