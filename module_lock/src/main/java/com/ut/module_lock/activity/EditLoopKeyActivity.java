package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.DialogUtil;
import com.ut.base.customView.DatePicker;
import com.ut.base.customView.DateTimePicker;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLoopBinding;
import com.ut.module_lock.entity.KeyItem;

import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/6
 * desc   :修改循环钥匙页面
 */

@Route(path = RouterUtil.LockModulePath.EDIT_LOOP_TIME)
public class EditLoopKeyActivity extends BaseActivity {

    private ActivityEditLoopBinding mBinding;
    private KeyItem mKeyItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_loop);
        initDarkToolbar();
        setTitle(R.string.lock_loop_key);
        mKeyItem = (KeyItem) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(mKeyItem);

        mBinding.validTime.setOnClickListener(v -> chooseDateTime(v, "生效时间"));
        mBinding.invalidTime.setOnClickListener(v -> chooseDateTime(v, "失效时间"));

        mBinding.startDate.setOnClickListener(v -> {
            chooseDate(v, "启用日期");
        });

        mBinding.endDate.setOnClickListener(v -> {
            chooseDate(v, "停止日期");
        });

        mBinding.btnSave.setOnClickListener(v -> save());
    }

    private void chooseDateTime(View v, String title) {
        DialogUtil.chooseDateTime(v.getContext(), title, (year, month, day, hour, minute) -> {
            String dateTime = String.valueOf(year + "/"
                    + String.format(Locale.getDefault(), "%02d", month) + "/"
                    + String.format(Locale.getDefault(), "%02d", day) + " "
                    + String.format(Locale.getDefault(), "%02d", hour) + ":"
                    + String.format(Locale.getDefault(), "%02d", minute));
            if ("生效时间".equals(title)) {
                mKeyItem.setStartTime(dateTime);
            } else {
                mKeyItem.setEndTime(dateTime);
            }
            mBinding.setKeyItem(mKeyItem);
        });
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, mKeyItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void chooseDate(View v, String title) {
        DialogUtil.chooseDate(v.getContext(), title, (year, month, day) -> {
            String date = year + "/" + String.format(Locale.getDefault(), "%02d", month) + "/" + String.format(Locale.getDefault(), "%02d", day);
            if ("启用时期".equals(title)) {
                mKeyItem.setStartDate(date);
            } else {
                mKeyItem.setEndDate(date);
            }
            mBinding.setKeyItem(mKeyItem);
        });
    }


}
