package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.DialogUtil;
import com.ut.commoncomponent.CLToast;
import com.ut.database.database.CloudLockDatabaseHolder;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLimitedTimeBinding;
import com.ut.database.entity.Key;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.schedulers.Schedulers;

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
        if (keyInfo == null) {
            keyInfo = new Key();
            keyInfo.setStartTime(getIntent().getStringExtra("valid_time"));
            keyInfo.setEndTime(getIntent().getStringExtra("invalid_time"));
        }
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
        } catch (Exception e) {
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
        int tv_year = 0;
        int tv_month = 0;
        int tv_day = 0;
        int tv_hour = 0;
        int tv_minute = 0;

        if (v != null) {
            String timeStr = ((TextView) v.findViewWithTag("time")).getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = null;
            try {
                date = dateFormat.parse(timeStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                tv_year = calendar.get(Calendar.YEAR);
                tv_month = calendar.get(Calendar.MONTH) + 1;
                tv_day = calendar.get(Calendar.DAY_OF_MONTH);
                tv_hour = calendar.get(Calendar.HOUR_OF_DAY);
                tv_minute = calendar.get(Calendar.MINUTE);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        DialogUtil.chooseDateTimeInSendLimitKey(v.getContext(), title, (year, month, day, hour, minute) -> {
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

                if (selectedStartTime > selectedEndTime) {
                    CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_void_time_edit_error));
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
        }, tv_year, tv_month, tv_day, tv_hour, tv_minute);
    }

    private void save() {

        if (selectedStartTime > selectedEndTime) {
            CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_void_time_edit_error));
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, keyInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}
