package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.AcitivityLockSettingBinding;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lockKey = getIntent().getParcelableExtra("lock_key");
        mBinding = DataBindingUtil.setContentView(this, R.layout.acitivity_lock_setting);
        mBinding.setLockKey(lockKey);
        initDarkToolbar();
        setTitle(R.string.lock_setting);
        mBinding.chooseGroup.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.CHOOSE_LOCK_GROUP).withParcelable("lock_key", lockKey).navigation(this, REQUEST_CODE_CHOOSE_GROUP));

        mBinding.btnDeleteKey.setOnClickListener(v -> deleteLock());

        mBinding.layoutLockName.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_NAME).navigation(this, REQUEST_CODE_EDIT_NAME);
        });
    }

    private void deleteLock() {
        CustomerAlertDialog dialog = new CustomerAlertDialog(LockSettingActivity.this, false);
        dialog.setMsg(getString(R.string.lock_delete_lock_tips));
        dialog.setConfirmText(getString(R.string.lock_btn_confirm));
        dialog.setConfirmListener(v1 -> verifyAdmin());
        dialog.setCancelText(getString(R.string.lock_cancel));
        dialog.setCancelLister(null);
        dialog.show();
    }

    private void verifyAdmin() {
        //todo
        View contentView = View.inflate(this, R.layout.dialog_edit, null);
        EditText edtPwd = contentView.findViewById(R.id.edt);
        edtPwd.setHint(getString(R.string.lock_verify_user_password_hint));
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setView(contentView)
                .setTitle(R.string.lock_safe_verify)
                .setPositiveButton(getText(R.string.lock_btn_confirm), (dialog, which) -> {
                    //todo
                    //verify
                    MyRetrofit.get().getCommonApiService().verifyUserPwd(edtPwd.getText().toString().trim())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                if (result.isSuccess()) {
                                    deleteAdminLock();
                                }
                            }, new ErrorHandler());
                    dialog.dismiss();
                })
                .setNegativeButton(getText(R.string.lock_cancel), null)
                .create();
        alertDialog.show();
    }

    private void deleteAdminLock() {
        MyRetrofit.get().getCommonApiService().deleteAdminLock(lockKey.getMac())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CLToast.showAtCenter(getBaseContext(), result.msg);
                    if (result.isSuccess()) {
                        finish();
                    }
                }, new ErrorHandler());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            if (requestCode == REQUEST_CODE_CHOOSE_GROUP && data != null) {
                lockKey.setGroupId((int) data.getLongExtra("lock_group_id", lockKey.getGroupId()));
                mBinding.setLockKey(lockKey);
            } else if (requestCode == REQUEST_CODE_EDIT_NAME && data != null) {
                String name = data.getStringExtra(Constance.EDIT_NAME);
                if (!TextUtils.isEmpty(name)) {
                    modifyLockName(name);
                }
            }
        }
    }

    private void modifyLockName(String name) {
        MyRetrofit.get().getCommonApiService().editLockName(lockKey.getMac(), name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result ->
                {
                    if (result.isSuccess()) {
                        lockKey.setName(name);
                        mBinding.setLockKey(lockKey);
                    }
                    CLToast.showAtCenter(getBaseContext(), result.msg);
                }, new ErrorHandler());
    }
}
