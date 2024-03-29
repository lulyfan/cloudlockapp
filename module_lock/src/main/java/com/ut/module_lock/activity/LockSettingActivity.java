package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.AppManager;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.base.dialog.DialogHelper;
import com.ut.commoncomponent.CLToast;
import com.ut.database.daoImpl.LockGroupDaoImpl;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.AcitivityLockSettingBinding;
import com.ut.module_lock.viewmodel.LockSettingVM;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :锁设置界面
 */

@SuppressLint("CheckResult")
@Route(path = RouterUtil.LockModulePath.LOCK_SETTING)
public class LockSettingActivity extends BaseActivity {
    private AcitivityLockSettingBinding mBinding;
    private LockKey lockKey;

    private final int REQUEST_CODE_EDIT_NAME = 1122;
    private final int REQUEST_CODE_CHOOSE_GROUP = 1002;

    private LockSettingVM mLockSettingVM = null;

    private CustomerAlertDialog mPermissionDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.acitivity_lock_setting);
        lockKey = getIntent().getParcelableExtra(Constance.LOCK_KEY);
        setBindingLockKey();
        initTitle();
        initViewModel();
        mBinding.chooseGroup.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.CHOOSE_LOCK_GROUP).withParcelable("lock_key", lockKey).navigation(this, REQUEST_CODE_CHOOSE_GROUP));
        mBinding.btnDeleteKey.setOnClickListener(v -> deleteLock());
        mBinding.switchCanOpen.setOnClickListener(view -> {
            mLockSettingVM.changeCanOpen(mBinding.switchCanOpen.isChecked());
        });
        mBinding.layoutLockName.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_NAME)
                    .withString(RouterUtil.LockModuleExtraKey.EDIT_NAME_TITLE, getString(R.string.lock_name))
                    .withString(RouterUtil.LockModuleExtraKey.NAME, lockKey.getName())
                    .withInt(RouterUtil.LockModuleExtraKey.NAME_TYPE, RouterUtil.LockModuleConstParams.NAMETYPE_LOCK)
                    .withString(RouterUtil.LockModuleExtraKey.MAC, lockKey.getMac())
                    .navigation(this, REQUEST_CODE_EDIT_NAME);
        });

        if (lockKey.getType() != EnumCollection.LockType.SMARTLOCK.getType()) {
            mBinding.adjustTime.setVisibility(View.GONE);
        }
        mBinding.adjustTime.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.TIME_ADJUST)
                .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, lockKey)
                .navigation());
        if (lockKey.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
            mBinding.switchCanOpen.setVisibility(View.GONE);
        }

        mBinding.layoutFrame.setOnClickListener(v -> {
            if (lockKey.getRuleType() == EnumCollection.KeyRuleType.TIMELIMIT.ordinal()) {
                ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_LIMITED_TIME)
                        .withString("valid_time", lockKey.getStartTime())
                        .withString("invalid_time", lockKey.getEndTime()).navigation();
            } else if (lockKey.getRuleType() == EnumCollection.KeyRuleType.CYCLE.ordinal()) {
                ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_LOOP_TIME)
                        .withString("weeks", lockKey.getWeeks())
                        .withString("valid_time", lockKey.getStartTime())
                        .withString("invalid_time", lockKey.getEndTime())
                        .withString("start_time_range", lockKey.getStartTimeRange())
                        .withString("end_time_range", lockKey.getEndTimeRange()).navigation();
            }
        });
    }

    private void initViewModel() {
        mLockSettingVM = ViewModelProviders.of(this).get(LockSettingVM.class);
        mLockSettingVM.isDeleteSuccess().observe(this, isUnlockSuccess -> {
            if (isUnlockSuccess) {
                AppManager.getAppManager().finishActivity(LockDetailActivity.class);
                finish();
                lockKey = null;
                ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
            }
        });
        mLockSettingVM.getShowTip().observe(this, showTip -> {
            CLToast.showAtBottom(getBaseContext(), showTip);
        });
        mLockSettingVM.getDialogHandler().observe(this, tips -> {
            if (Constance.END_LOAD.equals(tips)) {
                endLoad();
                mBinding.btnDeleteKey.setEnabled(true);
            } else if (Constance.START_LOAD.equals(tips)) {
                startLoad();
                mBinding.btnDeleteKey.setEnabled(false);
            }
        });
        mLockSettingVM.setLockKey(lockKey);
        mLockSettingVM.loadLockKey(lockKey.getMac()).observe(this, lk -> {
            if (lk == null) return;
            lockKey = lk;
            setBindingLockKey();
            loadGroupName();
            mBinding.switchCanOpen.setEnabled(lockKey.isKeyValid());
        });
        mLockSettingVM.getSetCanOpenSwitchResult().observe(this, operateSuccess -> {
            if (!operateSuccess.result) {
                mBinding.switchCanOpen.setChecked(operateSuccess.oldVar);
            }
        });
    }

    private void initTitle() {
        initDarkToolbar();
        setTitle(R.string.lock_setting);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }

    private void loadGroupName() {
        Observable.just(lockKey.getGroupId())
                .subscribeOn(Schedulers.io())
                .map(id -> {
                    LockGroup lockGroup = LockGroupDaoImpl.get().getLockGroupById(id);
                    if (lockGroup == null) {
                        return getString(R.string.lock_all_groups);
                    } else {
                        return lockGroup.getName();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mBinding.tvLockGroup.setText(name));
    }

    private void deleteLock() {
        if (lockKey.getUserType() == EnumCollection.UserType.ADMIN.ordinal()) {
            DialogHelper.getInstance()
                    .setMessage(getString(R.string.lock_delete_lock_tips))
                    .setPositiveButton(getString(R.string.lock_btn_confirm), ((dialog, which) -> {
                        verifyAdmin();
                    }))
                    .setNegativeButton(getString(R.string.lock_cancel), null)
                    .show();
        } else if (lockKey.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
            View contentView = View.inflate(this, R.layout.layout_anthorize_delete_key, null);
            CheckBox checkBox = contentView.findViewById(R.id.check_box);
            DialogHelper.getInstance().setTitle(getString(R.string.lock_make_sure_delete_auth_key))
                    .setContentView(contentView)
                    .setPositiveButton(getString(R.string.lock_btn_confirm), ((dialog1, which) -> {
                        if (checkBox.isChecked()) {
                            mLockSettingVM.deleteLockKey(lockKey, 1);
                        } else {
                            mLockSettingVM.deleteLockKey(lockKey, 0);
                        }
                        SystemUtils.hideKeyboard(getBaseContext(), contentView);
                    }))
                    .setNegativeButton(getString(R.string.lock_cancel), null)
                    .show();
        } else if (lockKey.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
            DialogHelper.getInstance()
                    .setMessage(getString(R.string.lock_make_sure_delete_auth_key_2))
                    .setPositiveButton(getString(R.string.lock_btn_confirm), ((dialog1, which) -> {
                        mLockSettingVM.deleteLockKey(lockKey, 0);
                    }))
                    .setNegativeButton(getString(R.string.lock_cancel), null)
                    .show();
        }
    }

    private void verifyAdmin() {
        //todo
        View contentView = View.inflate(this, R.layout.dialog_edit, null);
        EditText edtPwd = contentView.findViewById(R.id.edt);
        edtPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edtPwd.setHint(getString(R.string.lock_verify_user_password_hint));
        DialogHelper.getInstance()
                .newDialog()
                .setContentView(contentView)
                .setTitle(getString(R.string.lock_safe_verify))
                .setPositiveButton(getString(R.string.lock_btn_confirm), (dialog, which) -> {
                    //verify
                    mLockSettingVM.verifyAdmin(edtPwd.getText().toString().trim());
                    SystemUtils.hideKeyboard(getBaseContext(), contentView.getRootView());
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.lock_cancel), null).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == REQUEST_CODE_CHOOSE_GROUP && data != null) {
                lockKey.setGroupId((int) data.getLongExtra("lock_group_id", lockKey.getGroupId()));
                Schedulers.io().scheduleDirect(() -> {
                    if (lockKey == null || TextUtils.isEmpty(lockKey.getMac())) return;
                    LockKeyDaoImpl.get().insert(lockKey);
                });
                setBindingLockKey();
            } else if (requestCode == REQUEST_CODE_EDIT_NAME && data != null) {
                String name = data.getStringExtra(Constance.EDIT_NAME);
                if (!TextUtils.isEmpty(name)) {
                    modifyLockName(name);
                }
            } else if (requestCode == LockSettingVM.BLEENABLECODE) {
                mLockSettingVM.toDeleteAdminKey();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LockSettingVM.BLEREAUESTCODE && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {//定位未允许的时候去
                    mPermissionDialog = new CustomerAlertDialog(this, false)
                            .setMsg(getString(R.string.lock_location_need_tips))
                            .setCancelLister(v -> {
                            })
                            .setConfirmListener(v -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            });
                    mPermissionDialog.show();
                    return;
                }
            }
            mLockSettingVM.getDialogHandler().postValue(Constance.START_LOAD);
            mLockSettingVM.toDeleteAdminKey();
        }
    }

    private void setBindingLockKey() {
        if (lockKey == null) return;
        lockKey.setStatusStr(this.getResources().getStringArray(R.array.key_status));
        lockKey.setLockTypeStr(this.getResources().getStringArray(R.array.lock_type));

        int ruleType = lockKey.getRuleType();
        settingRueTypeString(ruleType);
        lockKey.setElectricityStr();
        mBinding.setLockKey(lockKey);
    }

    private void settingRueTypeString(int ruleType) {
        if (ruleType == EnumCollection.KeyRuleType.FOREVER.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.permanent));
        } else if (ruleType == EnumCollection.KeyRuleType.ONCE.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.once_time));
        } else if (ruleType == EnumCollection.KeyRuleType.TIMELIMIT.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.limit_time));
        } else if (ruleType == EnumCollection.KeyRuleType.CYCLE.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.loop));
        }
    }

    private void modifyLockName(String name) {
        mLockSettingVM.modifyLockName(lockKey.getMac(), name);
    }

    @Override
    public void finish() {
        super.finish();

    }
}
