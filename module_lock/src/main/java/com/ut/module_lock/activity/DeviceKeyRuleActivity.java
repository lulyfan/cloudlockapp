package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.fragment.DeviceKeyCycleFrag;
import com.ut.module_lock.fragment.DeviceKeyForeverFrag;
import com.ut.module_lock.fragment.DeviceKeyLimitFrag;
import com.ut.module_lock.utils.PickerDialogUtils;
import com.ut.module_lock.databinding.*;
import com.ut.module_lock.viewmodel.DeviceKeyRuleVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.LOCK_DEVICE_KEY_PERMISSION)
public class DeviceKeyRuleActivity extends BaseActivity {
    private DeviceKey mDeviceKey = null;
    private ActivityDeviceKeyRuleBinding mBinding = null;
    private String[] permissionArray = null;
    private DeviceKeyRuleVM mDeviceKeyRuleVM = null;
    private List<BaseFragment> mFragmentList = null;
    private BaseFragment mCurrentFragment = null;

    private LockKey mLockKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_key_rule);
        permissionArray = getResources().getStringArray(R.array.device_key_auth_type);
        initTitle();
        initData();
        initView();
        initListener();
        initVM();
        initFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceKeyRuleVM.mBleOperateManager.onActivityOnpause();
    }

    private void initVM() {
        mDeviceKeyRuleVM = ViewModelProviders.of(this).get(DeviceKeyRuleVM.class);
        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
        mDeviceKeyRuleVM.setFirstDeviceAuthType(mDeviceKey.getKeyAuthType());
        mDeviceKeyRuleVM.setLockKey(mLockKey);
        mDeviceKeyRuleVM.getSaveResult().observe(this, saveResult -> {
            if (saveResult) {
                DeviceKey deviceKey = mDeviceKeyRuleVM.getDeviceKey();
                Intent intent = new Intent();
                intent.putExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY, deviceKey);
                DeviceKeyRuleActivity.this.setResult(RESULT_OK, intent);
                finish();
            }
        });
        mDeviceKeyRuleVM.getDeviceKeyLiveData().observe(this, mDeviceKey -> {
            if (mDeviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.CYCLE.ordinal()) {
                if (TextUtils.isEmpty(mDeviceKey.getTimeICtl())) {
                    if (mBinding.btnSave.isEnabled())
                        mBinding.btnSave.setEnabled(false);
                    return;
                }
            }
            if (!mBinding.btnSave.isEnabled())
                mBinding.btnSave.setEnabled(true);
        });
        mDeviceKeyRuleVM.getShowTip().observe(this, msg -> {
            CLToast.showAtBottom(DeviceKeyRuleActivity.this, msg);
        });
        mDeviceKeyRuleVM.getShowDialog().observe(this, isShow -> {
            if (isShow) {
                startLoad();
            } else {
                endLoad();
            }
        });
        mDeviceKeyRuleVM.getShowLockResetDialog().observe(this, isShow -> {
            if (isShow)
                new CustomerAlertDialog(DeviceKeyRuleActivity.this, false)
                        .setMsg(getString(R.string.lock_detail_dialog_msg_reset))
                        .hideCancel()
                        .show();
        });
    }

    private void initData() {
        mDeviceKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY);
        mLockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
    }

    private void initTitle() {
        setTitle(R.string.lock_detail_edit_permission);
        initDarkToolbar();
    }

    private void initView() {
        mBinding.tvLockAuthorizeType.setText(permissionArray[mDeviceKey.getKeyAuthType()]);
    }

    private void initListener() {
        mBinding.card1.setOnClickListener(v -> {
            choosePermission(v);
        });
        mBinding.btnSave.setOnClickListener(v -> {
            DeviceKey deviceKey = mDeviceKeyRuleVM.getDeviceKey();
            if (deviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.TIMELIMIT.ordinal()) {
                if (deviceKey.getTimeEnd() <= deviceKey.getTimeStart()) {
                    CLToast.showAtCenter(DeviceKeyRuleActivity.this, getString(R.string.lock_device_key_tip_validtime));
                    return;
                }
            } else if (deviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.CYCLE.ordinal()) {
                Date dateStart = new Date(deviceKey.getTimeStart());
                Date dateEnd = new Date(deviceKey.getTimeEnd());
                long timeStart = dateStart.getHours() * 60 + dateStart.getMinutes();
                long timeEnd = dateEnd.getHours() * 60 + dateEnd.getMinutes();
                if (deviceKey.getTimeEnd() < deviceKey.getTimeStart()) {
                    CLToast.showAtCenter(DeviceKeyRuleActivity.this, getString(R.string.lock_device_key_tip_validdate));
                    return;
                } else if (timeEnd < timeStart) {
                    CLToast.showAtCenter(DeviceKeyRuleActivity.this, getString(R.string.lock_device_key_tip_validtime));
                    return;
                }
            }
            mDeviceKeyRuleVM.saveDeviceKey(DeviceKeyRuleActivity.this);
        });
    }

    private void initFragment() {
        mFragmentList = new ArrayList<>();
        mFragmentList.add(DeviceKeyForeverFrag.newInstance());
        mFragmentList.add(DeviceKeyLimitFrag.newInstance());
        mFragmentList.add(DeviceKeyCycleFrag.newInstance());
        switchFragemnt(mDeviceKey.getKeyAuthType());
    }

    private void switchFragemnt(int index) {
        if (index < 0 || index > mFragmentList.size()) return;
        BaseFragment baseFragment = mFragmentList.get(index);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (baseFragment != mCurrentFragment) {
            if (mCurrentFragment != null) {
                transaction.hide(mCurrentFragment);
            }
            if (!baseFragment.isAdded()) {
                transaction.add(R.id.container, baseFragment);
            } else {
                transaction.show(baseFragment);
            }
            transaction.commit();
        }
        mCurrentFragment = baseFragment;
    }


    private void choosePermission(View v) {
//        SystemUtils.hideKeyboard(this, v);
        PickerDialogUtils.chooseSingle(this, getString(R.string.lock_select_permission), Arrays.asList(permissionArray),
                mDeviceKey.getKeyAuthType(), new PickerDialogUtils.SinglePickListener() {
                    @Override
                    public void onSelected(int selectIndex, String selectData) {
                        mBinding.tvLockAuthorizeType.setText(selectData);
                        mDeviceKey.setKeyAuthType(selectIndex);
                        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
                        switchFragemnt(selectIndex);
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDeviceKeyRuleVM.mBleOperateManager.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDeviceKeyRuleVM.mBleOperateManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }
}
