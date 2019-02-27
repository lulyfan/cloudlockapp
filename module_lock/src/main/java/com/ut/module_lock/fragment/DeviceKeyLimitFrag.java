package com.ut.module_lock.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.DialogUtil;
import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.IncludeDkPermissionLimitBinding;
import com.ut.module_lock.utils.PickerDialogUtils;
import com.ut.module_lock.utils.StringUtils;
import com.ut.module_lock.viewmodel.DeviceKeyRuleVM;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * author : zhouyubin
 * time   : 2019/01/14
 * desc   :
 * version: 1.0
 */
public class DeviceKeyLimitFrag extends BaseFragment {

    private IncludeDkPermissionLimitBinding mBinding = null;
    private View rootView;
    private DeviceKeyRuleVM mDeviceKeyRuleVM;
    private DeviceKey mDeviceKey;
    private List<String> mTimes = new ArrayList<>();

    public DeviceKeyLimitFrag() {
    }

    public static DeviceKeyLimitFrag newInstance() {
        DeviceKeyLimitFrag fragment = new DeviceKeyLimitFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.include_dk_permission_limit, container, false);
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
        for (int i = 0; i < 100; i++) {
            if (i == 0) {
                mTimes.add(getString(R.string.device_key_time_unlimit));
            } else {
                mTimes.add(String.valueOf(i));
            }
        }
    }

    private void initView() {
        if (mDeviceKey.getTimeStart() == 0L) mDeviceKey.setTimeStart(System.currentTimeMillis());
        if (mDeviceKey.getTimeEnd() == 0L)
            mDeviceKey.setTimeEnd(System.currentTimeMillis() + 3600000L);
        long startTime = mDeviceKey.getTimeStart();
        long endTime = mDeviceKey.getTimeEnd();
        mBinding.tvLockKeyVaildTime.setText(StringUtils.getTimeByStamp(startTime));
        mBinding.tvLockKeyInvaildTime.setText(StringUtils.getTimeByStamp(endTime));
        if (mDeviceKey.getOpenLockCnt() <= 0) mDeviceKey.setOpenLockCnt(1);
        mBinding.tvLockKeyTime.setText(mDeviceKeyRuleVM.openCntTimeStringByDeviceKey(mDeviceKey.getOpenLockCnt()));
    }

    private void initListener() {
        mBinding.rlyValidTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_key_vaild_time));
        });
        mBinding.rlyInvalidTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_key_invaild_time));
        });
        mBinding.rlyTime.setOnClickListener(v -> {
            chooseCount();
        });
    }

    private void initVM() {
        mDeviceKeyRuleVM = ViewModelProviders.of(getActivity()).get(DeviceKeyRuleVM.class);
        mDeviceKey = mDeviceKeyRuleVM.getDeviceKey();
    }

    private void chooseTime(View v, String title) {
        String timeStr = mBinding.tvLockKeyVaildTime.getText().toString();
        long timeStamp = StringUtils.getTimeStampFromString(timeStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(timeStamp));
        DialogUtil.chooseDateTimeInSendLimitKey(getContext(), title, (year, month, day, hour, minute) -> {
                    String timeString = getString(R.string.dateTime_format, year, month, day, hour, minute);
                    if (getString(R.string.lock_key_vaild_time).equals(title)) {
                        mBinding.tvLockKeyVaildTime.setText(timeString);
                        mDeviceKey.setTimeStart(StringUtils.getTimeStampFromString(timeString));
                        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);

                    } else if (getString(R.string.lock_key_invaild_time).equals(title)) {
                        mBinding.tvLockKeyInvaildTime.setText(timeString);
                        mDeviceKey.setTimeEnd(StringUtils.getTimeStampFromString(timeString));
                        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));

    }

    private void chooseCount() {
        int defaultIndex = mDeviceKey.getOpenLockCnt() == 255 ? 0 : mDeviceKey.getOpenLockCnt();
        if (defaultIndex < 0 || defaultIndex > 99) {
            defaultIndex = 0;
        }
        PickerDialogUtils.chooseSingle(getContext(), getString(R.string.lock_key_open_times), mTimes,
                defaultIndex, new PickerDialogUtils.SinglePickListener() {
                    @Override
                    public void onSelected(int selectIndex, String selectData) {
                        mBinding.tvLockKeyTime.setText(selectData);
                        mDeviceKey.setOpenLockCnt(selectIndex == 0 ? 255 : selectIndex);
                        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
                    }
                }
        );
    }


}
