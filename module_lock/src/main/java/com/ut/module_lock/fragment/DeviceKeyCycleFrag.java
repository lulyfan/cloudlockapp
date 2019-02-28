package com.ut.module_lock.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.IncludeDkPermissionCycleBinding;
import com.ut.module_lock.utils.StringUtils;
import com.ut.module_lock.viewmodel.DeviceKeyRuleVM;

/**
 * author : zhouyubin
 * time   : 2019/01/15
 * desc   :
 * version: 1.0
 */
public class DeviceKeyCycleFrag extends BaseFragment {
    private IncludeDkPermissionCycleBinding mBinding = null;
    private View rootView = null;
    private DeviceKeyRuleVM mDeviceKeyRuleVM = null;
    private DeviceKey mDeviceKey;
    private Boolean[] mWeekAuthData;

    public DeviceKeyCycleFrag() {
    }

    public static DeviceKeyCycleFrag newInstance() {
        DeviceKeyCycleFrag fragment = new DeviceKeyCycleFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.include_dk_permission_cycle, container, false);
            rootView = mBinding.getRoot();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initVM();
        initView();
        initListener();
    }

    private void initVM() {
        mDeviceKeyRuleVM = ViewModelProviders.of(getActivity()).get(DeviceKeyRuleVM.class);
        mDeviceKey = mDeviceKeyRuleVM.getDeviceKey();
        mDeviceKey.setOpenLockCnt(255);//循环时默认为无限制次数
    }

    private void initListener() {
        mBinding.validTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_cycle_validTime));
        });
        mBinding.invalidTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_key_invaild_time));
        });
        mBinding.startDate.setOnClickListener(v -> {
            chooseDate(v, getString(R.string.lock_cycle_startDate));
        });
        mBinding.endDate.setOnClickListener(v -> {
            chooseDate(v, getString(R.string.lock_cycle_endDate));
        });
    }

    private void initView() {
        initWeekView();
        if (mDeviceKey.getTimeStart() == 0L) mDeviceKey.setTimeStart(System.currentTimeMillis());
        if (mDeviceKey.getTimeEnd() == 0L)
            mDeviceKey.setTimeEnd(System.currentTimeMillis() + 3600000L);
        mBinding.validTime.setText(StringUtils.getTimeString(mDeviceKey.getTimeStart()));
        mBinding.invalidTime.setText(StringUtils.getTimeString(mDeviceKey.getTimeEnd()));
        mBinding.startDate.setText(StringUtils.getDateString(mDeviceKey.getTimeStart()));
        mBinding.endDate.setText(StringUtils.getDateString(mDeviceKey.getTimeEnd()));
    }

    private void initWeekView() {
        mWeekAuthData = mDeviceKey.getWeekAuthData();
        ConstraintLayout constraintLayout = (ConstraintLayout) mBinding.include5;
        int childCount = constraintLayout.getChildCount();
        if (childCount != mWeekAuthData.length) return;
        for (int i = 0; i < childCount; i++) {
            View v = constraintLayout.getChildAt(i);
            if (v instanceof CheckBox) {
                ((CheckBox) v).setChecked(mWeekAuthData[i]);
                v.setTag(i);
                ((CheckBox) v).setOnCheckedChangeListener(mOnCheckedChangeListener);
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int index = (int) buttonView.getTag();
            mWeekAuthData[index] = isChecked;
            mDeviceKey.setWeekAuthData(mWeekAuthData);
            mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
        }
    };

    private void chooseDate(View v, String title) {
        DialogUtil.chooseDate(getContext(), title, (year, month, day) -> {
            TextView textView = (TextView) v;
            String dateString = String.format("%02d/%02d/%02d", year, month, day);
            textView.setText(dateString);

            if (getString(R.string.lock_cycle_startDate).equals(title)) {
                initTime(true);
            } else if (getString(R.string.lock_cycle_endDate).equals(title)) {
                initTime(false);
            }
        }, false, 0, 0, 0);
    }

    private void chooseTime(View v, String title) {
        DialogUtil.chooseTime(getContext(), title, (hour, minute) -> {
            TextView textView = (TextView) v;
            String timeString = String.format("%02d:%02d", hour, minute);
            textView.setText(timeString);

            if (getString(R.string.lock_cycle_validTime).equals(title)) {
                initTime(true);
            } else if (getString(R.string.lock_key_invaild_time).equals(title)) {
                initTime(false);
            }
        }, getString(R.string.lock_key_invaild_time).equals(title));
    }

    private void initTime(boolean isStart) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isStart) {
            stringBuilder.append(mBinding.startDate.getText().toString().trim());
            stringBuilder.append(" ");
            stringBuilder.append(mBinding.validTime.getText().toString().trim());
        } else {
            stringBuilder.append(mBinding.endDate.getText().toString().trim());
            stringBuilder.append(" ");
            stringBuilder.append(mBinding.invalidTime.getText().toString().trim());
        }
        long timeStamp = StringUtils.getTimeStampFromString(stringBuilder.toString());
        if (isStart) {
            mDeviceKey.setTimeStart(timeStamp);
        } else {
            mDeviceKey.setTimeEnd(timeStamp);
        }
        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
    }
}
