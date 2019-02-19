package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.Util;
import com.ut.base.common.CommonPopupWindow;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityDeviceKeyDetailBinding;
import com.ut.module_lock.viewmodel.DeviceKeyDetailVM;

@Route(path = RouterUtil.LockModulePath.LOCK_DEVICE_KEY_DETAIL)
public class DeviceKeyDetailActivity extends BaseActivity {
    private ActivityDeviceKeyDetailBinding mBinding;
    private DeviceKey mDeviceKey = null;
    private LockKey mLockKey = null;

    public static final int REQUEST_CODE_EDIT_NAME = 101;
    public static final int REQUEST_CODE_EDIT_PERMISSION = 102;

    private CommonPopupWindow mCommonPopupWindow = null;
    private DeviceKeyDetailVM mDeviceKeyDetailVM = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_key_detail);
        initTitle();
        initData();
        initView();
        mBinding.setPresent(new Present());
        initVM();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDeviceKeyDetailVM.mBleOperateManager.onActivityOnpause();
    }

    private void initVM() {
        mDeviceKeyDetailVM = ViewModelProviders.of(this).get(DeviceKeyDetailVM.class);
        mDeviceKeyDetailVM.setDeviceKey(mDeviceKey);
        mDeviceKeyDetailVM.setLockKey(mLockKey);
        mDeviceKeyDetailVM.getFreezeResult().observe(this, isSuccess -> {
            if (isSuccess) {
                initView();
            }
        });
        mDeviceKeyDetailVM.getShowDialog().observe(this, isShow -> {
            if (isShow) {
                startLoad();
            } else {
                endLoad();
            }
        });
        mDeviceKeyDetailVM.getShowTip().observe(this, msg -> {
            CLToast.showAtBottom(DeviceKeyDetailActivity.this, msg);
        });
        mDeviceKeyDetailVM.getDeleteResult().observe(this, isSuccess -> {
            if (isSuccess) {
                finish();
            }
        });
    }

    private void initTitle() {
        setTitle(R.string.lock_detail_key_detail);
        initDarkToolbar();
        initMore(() -> {
            setLightStatusBar();
            mCommonPopupWindow.showAtLocationWithAnim(mBinding.getRoot(), Gravity.TOP, 0, 0, R.style.animTranslate);
            SystemUtils.setWindowAlpha(this, 0.5f);
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mDeviceKey = bundle.getParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY);
            mLockKey = bundle.getParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
        }
    }

    private void initView() {
        mBinding.tvLockKeyName.setText(mDeviceKey.getName());
        mBinding.tvLockKeyType.setText(getResources().getStringArray(R.array.deviceTypeName)[mDeviceKey.getKeyType()]);
        mBinding.tvLockKeyPermission.setText(getResources().getStringArray(R.array.device_key_auth_type)[mDeviceKey.getKeyAuthType()]);
        if (mDeviceKey.getKeyAuthType() == EnumCollection.DeviceKeyAuthType.FOREVER.ordinal()) {
            mBinding.tvLockKeyTime.setText(R.string.device_key_time_unlimit);
        } else {
            if (mDeviceKey.getOpenLockCnt() == 255) {
                mBinding.tvLockKeyTime.setText(R.string.device_key_time_unlimit);
            } else {
                int remainTime = mDeviceKey.getOpenLockCnt() - mDeviceKey.getOpenLockCntUsed();
                mBinding.tvLockKeyTime.setText(String.valueOf(remainTime));
            }
        }
        mBinding.btnDelete.setOnClickListener(v -> {
            new CustomerAlertDialog(this, false)
                    .setMsg(getString(R.string.lock_deivce_key_tip_del))
                    .setConfirmText(getString(R.string.delete))
                    .setConfirmListener(v1 -> {
                        mDeviceKeyDetailVM.deleteKey(DeviceKeyDetailActivity.this);
                    }).show();

        });
        initPopupwindow();
    }

    private void initPopupwindow() {
        mCommonPopupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                getView(R.id.item2).setVisibility(View.GONE);
                getView(R.id.line1).setVisibility(View.GONE);
                TextView textView = getView(R.id.item1);
                boolean isFreezen = mDeviceKey.getKeyStatus() == EnumCollection.DeviceKeyStatus.FROZEN.ordinal();
                if (isFreezen) {
                    textView.setText(R.string.lock_unFreeze_key);
                } else {
                    textView.setText(R.string.lock_freeze_key);
                }
                textView.setOnClickListener(v -> {
                    mDeviceKeyDetailVM.freezeOrUnfreeze(!isFreezen, DeviceKeyDetailActivity.this);
                    getPopupWindow().dismiss();
                });
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        SystemUtils.setWindowAlpha(DeviceKeyDetailActivity.this, 1.0f);
                        DeviceKeyDetailActivity.this.setDarkStatusBar();
                    }
                });
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDeviceKeyDetailVM.mBleOperateManager.onActivityResult(this, requestCode, resultCode, data);
        if ((requestCode == REQUEST_CODE_EDIT_PERMISSION || requestCode == REQUEST_CODE_EDIT_NAME) && resultCode == RESULT_OK) {
            DeviceKey deviceKey = data.getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY);
            this.mDeviceKey = deviceKey;
            initView();
            mDeviceKeyDetailVM.setDeviceKey(mDeviceKey);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDeviceKeyDetailVM.mBleOperateManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public class Present {
        public void onNameClick(View view) {
            ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_NAME)
                    .withString(RouterUtil.LockModuleExtraKey.EDIT_NAME_TITLE, getString(R.string.key_name))
                    .withString(RouterUtil.LockModuleExtraKey.NAME, mDeviceKey.getName())
                    .withInt(RouterUtil.LockModuleExtraKey.NAME_TYPE, RouterUtil.LockModuleConstParams.NAMETYPE_DEVICE_KEY)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY, mDeviceKey)
                    .navigation(DeviceKeyDetailActivity.this, REQUEST_CODE_EDIT_NAME);
        }

        public void onPermissionClick(View view) {
            if (mDeviceKey.getKeyStatus() == EnumCollection.DeviceKeyStatus.FROZEN.ordinal()) {
                CLToast.showAtBottom(DeviceKeyDetailActivity.this, getString(R.string.lock_device_key_tip_auth_type));
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DEVICE_KEY_PERMISSION)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY, mDeviceKey)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, mLockKey)
                    .navigation(DeviceKeyDetailActivity.this, REQUEST_CODE_EDIT_PERMISSION);
        }

        public void onRecordClick(View view) {
            ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD)
                    .withBoolean(Constance.FIND_GATE_RECORD, true)
                    .withString(Constance.RECORD_TYPE, Constance.BY_KEY)
                    .withLong(Constance.KEY_ID, mDeviceKey.getRecordKeyId())
                    .withLong(Constance.LOCK_ID, mDeviceKey.getLockID())
                    .navigation();
        }


    }
}
