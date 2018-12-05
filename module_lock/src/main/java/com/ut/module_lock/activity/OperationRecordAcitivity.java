package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.adapter.ORListAdapter;
import com.ut.module_lock.databinding.ActivityOperationRecordBinding;
import com.ut.module_lock.entity.OperationRecord;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_operation_record);
        mBinding.getRoot().setFitsSystemWindows(true);
        mBinding.back.setOnClickListener(v -> finish());
        List<OperationRecord > oprs = new ArrayList<>();
        for (int i= 0; i<20;i++){
            OperationRecord op = new OperationRecord();
            op.setTime("2018/09/06");
            List<OperationRecord.Record> records = new ArrayList<>();
            for (int j=0;j<3;j++) {
                OperationRecord.Record record = new OperationRecord.Record();
                record.setDesc("desc........");
                record.setOperator("operatorx");
                records.add(record);
            }
            op.setRecords(records);
            oprs.add(op);
        }
        mBinding.operationRecordList.setAdapter(new ORListAdapter(this, oprs));
    }
}
