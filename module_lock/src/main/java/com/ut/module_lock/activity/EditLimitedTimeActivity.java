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
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLimitedTimeBinding;
import com.ut.database.entity.Key;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
@Route(path = RouterUtil.LockModulePath.EDIT_LIMITED_TIME)
public class EditLimitedTimeActivity extends BaseActivity {

    private ActivityEditLimitedTimeBinding mBinding = null;
    private Key keyInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_limited_time);
        initDarkToolbar();
        setTitle(R.string.lock_limited_time_key);
        keyInfo = (Key) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(keyInfo);
        mBinding.chooseStartTime.setOnClickListener(v -> dateChoose(v, getString(R.string.valid_time)));
        mBinding.chooseEndTime.setOnClickListener(v -> dateChoose(v, getString(R.string.invalid_time)));
        mBinding.btnSave.setOnClickListener(v -> save());
    }

    private void dateChoose(View v, String title) {
        DialogUtil.chooseDateTime(v.getContext(), title, (year, month, day, hour, minute) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);

            if (getString(R.string.valid_time).equals(title)) {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                keyInfo.setStartTime(sdf.format(new Date(calendar.getTimeInMillis())));
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 59);
                keyInfo.setEndTime(sdf.format(new Date(calendar.getTimeInMillis())));
            }
            mBinding.setKeyItem(keyInfo);
        });
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, keyInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}
