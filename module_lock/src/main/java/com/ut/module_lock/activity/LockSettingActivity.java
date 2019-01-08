package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.dialog.CustomerAlertDialog;
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
import com.ut.unilink.UnilinkManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
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
        mBinding.switchCanOpen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isResetChecked) {
                isResetChecked = false;
                return;
            }
            mLockSettingVM.changeCanOpen(isChecked);
        });
        mBinding.layoutLockName.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_NAME)
                    .withString("edit_name_title", getString(R.string.lock_name))
                    .withString("name", lockKey.getName())
                    .withBoolean("is_lock", true)
                    .withString("mac", lockKey.getMac())
                    .navigation(this, REQUEST_CODE_EDIT_NAME);
        });
    }

    private volatile boolean isResetChecked = false;


    private void initViewModel() {
        mLockSettingVM = ViewModelProviders.of(this).get(LockSettingVM.class);
        mLockSettingVM.isDeleteSuccess().observe(this, isUnlockSuccess -> {
            if (isUnlockSuccess) {
                //TODO 退到首页
                finish();
                lockKey = null;
                ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
            }
        });
        mLockSettingVM.getShowTip().observe(this, showTip -> {
            CLToast.showAtBottom(getBaseContext(), showTip);
        });
        mLockSettingVM.setLockKey(lockKey);
        mLockSettingVM.loadLockKey(lockKey.getMac()).observe(this, lk -> {
            if (lk == null) return;
            lockKey = lk;
            setBindingLockKey();
            loadGroupName();
        });
        mLockSettingVM.getSetCanOpenSwitchResult().observe(this, operateSuccess -> {
            if (!operateSuccess.result) {
                isResetChecked = true;
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

    private void loadGroupName() {
        Observable.just(lockKey.getGroupId())
                .subscribeOn(Schedulers.io())
                .map(id -> {
                    LockGroup lockGroup = LockGroupDaoImpl.get().getLockGroupById(id);
                    if (lockGroup == null) {
                        return "";
                    } else {
                        return lockGroup.getName();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(name -> mBinding.tvLockGroup.setText(name));
    }

    private void deleteLock() {
        if (lockKey.getUserType() == EnumCollection.UserType.ADMIN.ordinal()) {
//            CustomerAlertDialog dialog = new CustomerAlertDialog(LockSettingActivity.this, false);
//            dialog.setMsg(getString(R.string.lock_delete_lock_tips));
//            dialog.setConfirmText(getString(R.string.lock_btn_confirm));
//            dialog.setConfirmListener(v1 -> verifyAdmin());
//            dialog.setCancelText(getString(R.string.lock_cancel));
//            dialog.setCancelLister(null);
//            dialog.show();
            //todo
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.lock_delete_lock_tips))
                    .setPositiveButton(getString(R.string.lock_btn_confirm), ((dialog, which) -> {
                        verifyAdmin();
                    }))
                    .setNegativeButton(getString(R.string.lock_cancel), null)
                    .show();
        } else if (lockKey.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
            View contentView = View.inflate(this, R.layout.layout_anthorize_delete_key, null);
            CheckBox checkBox = contentView.findViewById(R.id.check_box);
            //TODO
            new AlertDialog.Builder(this).setTitle(getString(R.string.lock_make_sure_delete_auth_key))
                    .setView(contentView)
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
            //TODO
            new AlertDialog.Builder(this)
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
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(contentView)
                .setTitle(R.string.lock_safe_verify)
                .setPositiveButton(getText(R.string.lock_btn_confirm), (dialog, which) -> {
                    //todo
                    //verify
                    mLockSettingVM.verifyAdmin(edtPwd.getText().toString().trim());
                    SystemUtils.hideKeyboard(getBaseContext(), contentView.getRootView());
                    dialog.dismiss();
                })
                .setNegativeButton(getText(R.string.lock_cancel), null)
                .create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == REQUEST_CODE_CHOOSE_GROUP && data != null) {
                lockKey.setGroupId((int) data.getLongExtra("lock_group_id", lockKey.getGroupId()));
                setBindingLockKey();

            } else if (requestCode == REQUEST_CODE_EDIT_NAME && data != null) {
                String name = data.getStringExtra(Constance.EDIT_NAME);
                if (!TextUtils.isEmpty(name)) {
                    modifyLockName(name);
                }
            } else if (requestCode == LockSettingVM.BLEREAUESTCODE || requestCode == LockSettingVM.BLEENABLECODE) {
                mLockSettingVM.toDeleteAdminKey();
            }
        }
    }

    private void setBindingLockKey() {
        if (lockKey == null) return;
        lockKey.setStatusStr(this.getResources().getStringArray(R.array.key_status));
        lockKey.setLockTypeStr(this.getResources().getStringArray(R.array.lock_type));

        int ruleType = lockKey.getRuleType();
        if (ruleType == EnumCollection.KeyRuleType.FOREVER.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.permanent));
        } else if (ruleType == EnumCollection.KeyRuleType.ONCE.ordinal()) {
            lockKey.setKeyTypeStr(getString(R.string.once_time));
        } else if (ruleType == EnumCollection.KeyRuleType.TIMELIMIT.ordinal()) {
            lockKey.setKeyTypeStr(lockKey.getStartTimeRange() + " - " + lockKey.getEndTimeRange());
        } else if (ruleType == EnumCollection.KeyRuleType.CYCLE.ordinal()) {
            StringBuffer xingqi = new StringBuffer("星期");
            String weeks = lockKey.getWeeks();
            String[] split = weeks.split(",");

            for (String s : split) {
                switch (Integer.valueOf(s)) {
                    case 1:
                        xingqi.append("一,");
                        break;
                    case 2:
                        xingqi.append("二,");
                        break;
                    case 3:
                        xingqi.append("三,");
                        break;
                    case 4:
                        xingqi.append("四,");
                        break;
                    case 5:
                        xingqi.append("五,");
                        break;
                    case 6:
                        xingqi.append("六,");
                        break;
                    case 7:
                        xingqi.append("七,");
                        break;
                }
            }
            xingqi = xingqi.delete(xingqi.lastIndexOf(","), xingqi.length());
            lockKey.setKeyTypeStr(lockKey.getStartTime() + " - " + lockKey.getEndTime() + " " + xingqi + " " + lockKey.getStartTimeRange() + " - " + lockKey.getEndTimeRange());
        }

//        lockKey.setKeyTypeStr(this.getResources().getStringArray(R.array.key_type));
        lockKey.setElectricityStr();
        mBinding.setLockKey(lockKey);
    }

    private void modifyLockName(String name) {
        mLockSettingVM.modifyLockName(lockKey.getMac(), name);
    }

    @Override
    public void finish() {
        super.finish();
        if (lockKey == null || TextUtils.isEmpty(lockKey.getMac())) return;
        Observable.just(lockKey)
                .subscribeOn(Schedulers.io())
                .subscribe(lockKey1 -> {
                    LockKeyDaoImpl.get().insert(lockKey1);
                });
    }
}
