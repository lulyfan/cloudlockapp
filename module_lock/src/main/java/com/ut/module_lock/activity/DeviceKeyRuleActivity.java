package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.DeviceKey;
import com.ut.module_lock.R;
import com.ut.module_lock.fragment.DeviceKeyCycleFrag;
import com.ut.module_lock.fragment.DeviceKeyForeverFrag;
import com.ut.module_lock.fragment.DeviceKeyLimitFrag;
import com.ut.module_lock.utils.PickerDialogUtils;
import com.ut.module_lock.databinding.*;
import com.ut.module_lock.viewmodel.DeviceKeyRuleVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.LOCK_DEVICE_KEY_PERMISSION)
public class DeviceKeyRuleActivity extends BaseActivity {
    private DeviceKey mDeviceKey = null;
    private ActivityDeviceKeyRuleBinding mBinding = null;
    private String[] permissionArray = null;
    private DeviceKeyRuleVM mDeviceKeyRuleVM = null;
    private List<BaseFragment> mFragmentList = null;
    private BaseFragment mCurrentFragment = null;

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

    private void initVM() {
        mDeviceKeyRuleVM = ViewModelProviders.of(this).get(DeviceKeyRuleVM.class);
        mDeviceKeyRuleVM.setDeviceKey(mDeviceKey);
        mDeviceKeyRuleVM.getSaveResult().observe(this, saveResult -> {
            if (saveResult) {
                CLToast.showAtBottom(DeviceKeyRuleActivity.this, getString(R.string.operate_success));
            } else {
                CLToast.showAtBottom(DeviceKeyRuleActivity.this, getString(R.string.lock_device_key_operate_failed));
            }
        });
    }

    private void initData() {
        mDeviceKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY);
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
            mDeviceKeyRuleVM.saveDeviceKey();
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
        SystemUtils.hideKeyboard(this, v);
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
}