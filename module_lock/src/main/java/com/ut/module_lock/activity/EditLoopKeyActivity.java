package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.DialogUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLoopBinding;
import com.ut.database.entity.Key;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/12/6
 * desc   :修改循环钥匙页面
 */

@Route(path = RouterUtil.LockModulePath.EDIT_LOOP_TIME)
public class EditLoopKeyActivity extends BaseActivity {

    private ActivityEditLoopBinding mBinding;
    private Key mKey;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7;
    private int mY, mM, mD;
    private int sY, sM, sD, eY, eM, eD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_loop);
        mKey = (Key) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(mKey);
        initUI();

    }

    private void initUI() {
        initDarkToolbar();
        setTitle(R.string.lock_loop_key);
        checkBox1 = mBinding.include5.findViewById(R.id.monday);
        checkBox2 = mBinding.include5.findViewById(R.id.tuesday);
        checkBox3 = mBinding.include5.findViewById(R.id.wednesday);
        checkBox4 = mBinding.include5.findViewById(R.id.thursday);
        checkBox5 = mBinding.include5.findViewById(R.id.friday);
        checkBox6 = mBinding.include5.findViewById(R.id.saturday);
        checkBox7 = mBinding.include5.findViewById(R.id.sunday);
        mBinding.validTime.setOnClickListener(v -> chooseTime(v, getString(R.string.valid_time)));
        mBinding.invalidTime.setOnClickListener(v -> chooseTime(v, getString(R.string.invalid_time)));

        mBinding.startDate.setOnClickListener(v -> {
            mY = sY;
            mM = sM;
            mD = sD;
            chooseDate(v, getString(R.string.lock_start_date));
        });

        mBinding.endDate.setOnClickListener(v -> {
            handleEnDate();
            chooseDate(v, getString(R.string.lock_end_state));
        });

        mBinding.btnSave.setOnClickListener(v -> save());

        String weeks = mKey.getWeeks();

        if (weeks.contains("1")) {
            checkBox1.setChecked(true);
        }
        if (weeks.contains("2")) {
            checkBox2.setChecked(true);
        }
        if (weeks.contains("3")) {
            checkBox3.setChecked(true);
        }
        if (weeks.contains("4")) {
            checkBox4.setChecked(true);
        }
        if (weeks.contains("5")) {
            checkBox5.setChecked(true);
        }
        if (weeks.contains("6")) {
            checkBox6.setChecked(true);
        }
        if (weeks.contains("7")) {
            checkBox7.setChecked(true);
        }
    }

    private void handleEnDate() {
        if (sY > 0) {
            mY = sY;
            mM = sM;
            mD = sD;
            Calendar calendar = Calendar.getInstance();
            calendar.set(mY, mM, mD);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            mY = calendar.get(Calendar.YEAR);
            mM = calendar.get(Calendar.MONTH);
            mD = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }

    private void chooseTime(View v, String title) {
        DialogUtil.chooseTime(v.getContext(), title, (hour, minute) -> {
            String dateTime = String.format(Locale.getDefault(), "%02d", hour) + ":"
                    + String.format(Locale.getDefault(), "%02d", minute) + ":"
                    + String.format(Locale.getDefault(), "%02d", 0);
            if (getString(R.string.valid_time).equals(title)) {
                mKey.setStartTimeRange(dateTime);
            } else {
                mKey.setEndTimeRange(dateTime);
            }
            mBinding.setKeyItem(mKey);
        });
    }

    private void save() {
        StringBuilder weeks = new StringBuilder();
        if (checkBox1.isChecked()) {
            weeks.append(weeks.length() == 0 ? "1" : ",1");
        }
        if (checkBox2.isChecked()) {
            weeks.append(weeks.length() == 0 ? "2" : ",2");
        }
        if (checkBox3.isChecked()) {
            weeks.append(weeks.length() == 0 ? "3" : ",3");
        }
        if (checkBox4.isChecked()) {
            weeks.append(weeks.length() == 0 ? "4" : ",4");
        }
        if (checkBox5.isChecked()) {
            weeks.append(weeks.length() == 0 ? "5" : ",5");
        }
        if (checkBox6.isChecked()) {
            weeks.append(weeks.length() == 0 ? "6" : ",6");
        }
        if (checkBox7.isChecked()) {
            weeks.append(weeks.length() == 0 ? "7" : ",7");
        }

        mKey.setWeeks(weeks.toString());
        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, mKey);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void chooseDate(View v, String title) {
        DialogUtil.chooseDate(v.getContext(), title, (year, month, day) -> {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);
            if (getString(R.string.lock_start_date).equals(title)) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);

                sY = year;
                sM = month - 1;
                sD = day;
                mKey.setStartTime(sdf.format(new Date(calendar.getTimeInMillis())));
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                mKey.setEndTime(sdf.format(new Date(calendar.getTimeInMillis())));

                eY = year;
                eM = month - 1;
                eD = day;
            }
            mBinding.setKeyItem(mKey);
        }, mY != mM, mY, mM, mD);
    }


}
