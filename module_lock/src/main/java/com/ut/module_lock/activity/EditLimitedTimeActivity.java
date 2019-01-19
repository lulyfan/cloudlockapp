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
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLimitedTimeBinding;
import com.ut.database.entity.Key;

import java.text.ParseException;
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
    private int sYear, sMonth, sDay, sHour, sMinute;
    private int eYear, eMonth, eDay, eHour, eMinute;
    private int mYear, mMonth, mDay, mHour, mMinute;


    private long selectedStartTime, selectedEndTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_limited_time);
        initDarkToolbar();
        setTitle(R.string.lock_limited_time_key);
        keyInfo = (Key) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(keyInfo);
        initData();
    }

    private void initData() {
        mBinding.chooseStartTime.setOnClickListener(v -> {
            mYear = sYear;
            mMonth = sMonth;
            mDay = sDay;
            mHour = sHour;
            mMinute = sMinute;
            dateChoose(v, getString(R.string.valid_time));
        });
        mBinding.chooseEndTime.setOnClickListener(v -> {
            handleInvalidTime();
            dateChoose(v, getString(R.string.invalid_time));
        });
        mBinding.btnSave.setOnClickListener(v -> save());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            long startTime = sdf.parse(keyInfo.getStartTime()).getTime();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(startTime);
            sYear = calendar.get(Calendar.YEAR);
            sMonth = calendar.get(Calendar.MONTH);
            sDay = calendar.get(Calendar.DAY_OF_MONTH);
            sHour = calendar.get(Calendar.HOUR_OF_DAY);
            sMinute = calendar.get(Calendar.MINUTE);

            selectedStartTime = calendar.getTimeInMillis();

            long endTime = sdf.parse(keyInfo.getEndTime()).getTime();
            calendar.setTimeInMillis(endTime);
            eYear = calendar.get(Calendar.YEAR);
            eMonth = calendar.get(Calendar.MONTH);
            eDay = calendar.get(Calendar.DAY_OF_MONTH);
            eHour = calendar.get(Calendar.HOUR_OF_DAY);
            eMinute = calendar.get(Calendar.MINUTE);

            selectedEndTime = calendar.getTimeInMillis();


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void handleInvalidTime() {
        if (sYear > 0) {
            mYear = sYear;
            mMonth = sMonth;
            mDay = sDay;
            mHour = sHour;
            mMinute = sMinute;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mYear, mMonth, mDay, mHour, mMinute);
            calendar.add(Calendar.MINUTE, 15);
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mHour = calendar.get(Calendar.HOUR_OF_DAY);
            mMinute = calendar.get(Calendar.MINUTE);
        }
    }

    private void dateChoose(View v, String title) {
        DialogUtil.chooseDateTime(v.getContext(), title, (year, month, day, hour, minute) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day, hour, minute);

            if (getString(R.string.valid_time).equals(title)) {
                calendar.set(Calendar.SECOND, 0);
                selectedStartTime = calendar.getTimeInMillis();
                sYear = year;
                sMonth = month - 1;
                sDay = day;
                sHour = hour;
                sMinute = minute;
                keyInfo.setStartTime(sdf.format(new Date(calendar.getTimeInMillis())));

            } else {
                calendar.set(Calendar.SECOND, 59);
                selectedEndTime = calendar.getTimeInMillis();

                if(selectedStartTime > selectedEndTime) {
                    CLToast.showAtCenter(getBaseContext(), "生效时间不能晚于失效时间");
                    return;
                }

                eYear = year;
                eMonth = month - 1;
                eDay = day;
                eHour = hour;
                eMinute = minute;
                keyInfo.setEndTime(sdf.format(new Date(calendar.getTimeInMillis())));
            }
            mBinding.setKeyItem(keyInfo);
        }, mYear != mMonth, mYear, mMonth, mDay, mHour, mMinute);
    }

    private void save() {

        if(selectedStartTime > selectedEndTime) {
            CLToast.showAtCenter(getBaseContext(), "生效时间不能晚于失效时间");
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, keyInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}
