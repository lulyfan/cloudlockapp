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

import java.util.ArrayList;
import java.util.Arrays;
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
        long startTime = mDeviceKey.getTimeStart() == 0L ? System.currentTimeMillis() : mDeviceKey.getTimeStart();
        long endTime = mDeviceKey.getTimeEnd() == 0L ? System.currentTimeMillis() + 3600000 : mDeviceKey.getTimeEnd();
        mBinding.tvLockKeyVaildTime.setText(StringUtils.getTimeByStamp(startTime));
        mBinding.tvLockKeyInvaildTime.setText(StringUtils.getTimeByStamp(endTime));
        mBinding.tvLockKeyTime.setText(mDeviceKeyRuleVM.openCntTimeStringByDeviceKey(mDeviceKey.getOpenLockCnt()));
    }

    private void initListener() {
        mBinding.tvLockKeyVaildTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_key_vaild_time));
        });
        mBinding.tvLockKeyInvaildTime.setOnClickListener(v -> {
            chooseTime(v, getString(R.string.lock_key_invaild_time));
        });
        mBinding.tvLockKeyTime.setOnClickListener(v -> {
            chooseCount();
        });
    }

    private void initVM() {
        mDeviceKeyRuleVM = ViewModelProviders.of(getActivity()).get(DeviceKeyRuleVM.class);
        mDeviceKey = mDeviceKeyRuleVM.getDeviceKey();
    }

    private void chooseTime(View v, String title) {
        DialogUtil.chooseDateTime(getContext(), title, (year, month, day, hour, minute) -> {
            TextView textView = (TextView) v;
            textView.setText(getString(R.string.dateTime_format, year, month, day, hour, minute));

            if (getString(R.string.lock_key_vaild_time).equals(title)) {
                String startTime = textView.getText().toString();
                mDeviceKey.setTimeStart(StringUtils.getTimeStampFromString(startTime));
                mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);

            } else if (getString(R.string.lock_key_invaild_time).equals(title)) {
                String endTime = textView.getText().toString();
                mDeviceKey.setTimeEnd(StringUtils.getTimeStampFromString(endTime));
                mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
            }
        });

    }

    private void chooseCount() {
        int defaultTime = mDeviceKey.getOpenLockCnt() == 255 ? 0 : mDeviceKey.getOpenLockCnt();
        if (defaultTime < 0 || defaultTime > 99) {
            defaultTime = 0;
        }
        PickerDialogUtils.chooseSingle(getContext(), getString(R.string.lock_key_open_times), mTimes,
                defaultTime, new PickerDialogUtils.SinglePickListener() {
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
