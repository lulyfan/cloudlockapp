package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonPopupWindow;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.base.dialog.DialogHelper;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityKeyInfoBinding;
import com.ut.database.entity.Key;
import com.ut.module_lock.viewmodel.KeyManagerVM;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.KEY_INFO)
public class KeyInfoActivity extends BaseActivity {

    private Key keyInfo;
    private ActivityKeyInfoBinding mBinding = null;
    private static final int REQUEST_EDIT_KEY = 1111;

    private KeyManagerVM keyManagerVM = null;
    private int managerUserType = -1;
    private boolean hasEdited = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_key_info);
        keyInfo = (Key) getIntent().getSerializableExtra(Constance.KEY_INFO);
        keyManagerVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        keyManagerVM.getFeedbackMessage().observe(this, message -> {
            CLToast.showAtBottom(KeyInfoActivity.this, message);
            finish();
        });

        mBinding.setKeyItem(keyInfo);
        keyManagerVM.setKey(keyInfo);
        keyManagerVM.setMac(keyInfo.getMac());
        keyManagerVM.getKeyById(keyInfo.getKeyId()).observe(this, key -> {
            if (key == null) {
                finish();
                return;
            }
            keyInfo = key;
            keyManagerVM.initKey(keyInfo);
            mBinding.setKeyItem(keyInfo);
        });

        managerUserType = getIntent().getIntExtra(Constance.USERTYPE, -1);
        initTitle();
        initListener();

    }

    private void initTitle() {
        initDarkToolbar();
        setTitle(R.string.lock_key_info);
        if (managerUserType < EnumCollection.UserType.NORMAL.ordinal() && managerUserType > 0) {
            if (keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal()) {
                return;
            }
            initMore(this::popupMoreWindow);
        }

    }

    private void initListener() {
        mBinding.keyNameSelection.setOnClickListener(v -> ARouter.getInstance()
                .build(RouterUtil.LockModulePath.EDIT_NAME)
                .withString(RouterUtil.LockModuleExtraKey.EDIT_NAME_TITLE, getString(R.string.key_name))
                .withInt(RouterUtil.LockModuleExtraKey.NAME_TYPE, RouterUtil.LockModuleConstParams.NAMETYPE_KEY)
                .withLong(RouterUtil.LockModuleExtraKey.KEY_ID, keyInfo.getKeyId())
                .withString(RouterUtil.LockModuleExtraKey.NAME, keyInfo.getKeyName())
                .navigation(this, REQUEST_EDIT_KEY));
        mBinding.keyTypeSelection.setOnClickListener(v -> {

            if (keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.FREEZING.ordinal()) {
                CLToast.showAtCenter(getBaseContext(), getString(R.string.lock_freezen_key_not_allow_to_fix));
                return;
            }

            String url;
            if (keyInfo.getRuleType() == EnumCollection.KeyRuleType.TIMELIMIT.ordinal()) {
                url = RouterUtil.LockModulePath.EDIT_LIMITED_TIME;
            } else {
                url = RouterUtil.LockModulePath.EDIT_LOOP_TIME;
            }
            ARouter.getInstance().build(url).withSerializable(Constance.KEY_INFO, keyInfo).navigation(this, REQUEST_EDIT_KEY);
        });
        mBinding.operationRecord.setOnClickListener(v -> {
                    ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD)
                            .withString(Constance.RECORD_TYPE, Constance.BY_KEY)
                            .withLong(Constance.KEY_ID, keyInfo.getKeyId())
                            .navigation();
                }
        );
        mBinding.btnDeleteKey.setOnClickListener(v -> deleteKey());
    }

    private void deleteKey() {
        DialogHelper.getInstance().newDialog().setMessage(getString(R.string.lock_delete_key_tips))
                .setPositiveButton(getString(R.string.lock_delete), ((dialog1, which) -> keyManagerVM.deleteKey(keyInfo.getKeyId())))
                .setNegativeButton(getString(R.string.lock_cancel), null)
                .show();
    }

    private void popupMoreWindow() {
        setWindowAlpha(0.5f);
        CommonPopupWindow popupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                TextView item1 = getView(R.id.item1);
                if (managerUserType == EnumCollection.UserType.ADMIN.ordinal()
                        && keyInfo.getUserType() != EnumCollection.UserType.ADMIN.ordinal()
                        && keyInfo.getRuleType() == EnumCollection.KeyRuleType.FOREVER.ordinal()
                        && (keyInfo.getStatus() != EnumCollection.KeyStatus.HAS_FREEZE.ordinal() && keyInfo.getStatus() != EnumCollection.KeyStatus.FREEZING.ordinal())
                ) {
                    item1.setText(keyInfo.getUserType() == EnumCollection.UserType.AUTH.ordinal() ? getString(R.string.lock_cancel_auth) : getString(R.string.lock_to_auth));
                    item1.setOnClickListener(v -> {
                        getPopupWindow().dismiss();
                        //ToDO
                        //授权 or not
                        if (keyInfo.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
                            DialogHelper.getInstance().setMessage(getString(R.string.lock_key_cancel_auth_title) + getString(R.string.lock_key_cancel_auth_tips))
                                    .setPositiveButton(getString(R.string.lock_btn_confirm), (dialog, which) -> keyManagerVM.cancelAuth(keyInfo.getKeyId()))
                                    .setNegativeButton(getString(R.string.lock_cancel), null)
                                    .show();

                        } else if (keyInfo.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
                            DialogHelper.getInstance().setMessage(getString(R.string.lock_key_auth_title) + getString(R.string.lock_key_auth_tips))
                                    .setPositiveButton(getString(R.string.lock_auth), (dialog, which) -> keyManagerVM.toAuth(keyInfo.getKeyId()))
                                    .setNegativeButton(getString(R.string.lock_cancel), null)
                                    .show();
                        }

                    });
                } else {
                    item1.setVisibility(View.GONE);
                }

                TextView item2 = getView(R.id.item2);
                if (keyInfo.getStatus() == EnumCollection.KeyStatus.DELETING.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_DELETE.ordinal()
                        || keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_INVALID.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_OVERDUE.ordinal()) {
                    item2.setVisibility(View.GONE);
                } else {
                    item2.setText(keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.FREEZING.ordinal()
                            ? getString(R.string.lock_unfrozen) : getString(R.string.lock_frozen));
                    item2.setOnClickListener(v -> {
                        getPopupWindow().dismiss();
                        if (keyInfo.isFrozened()) {
                            DialogHelper.getInstance().setMessage(getString(R.string.lock_unfrozen_tips))
                                    .setPositiveButton(getString(R.string.lock_unfrozen), (dialog, which) -> keyManagerVM.unFrozenKey(keyInfo.getKeyId()))
                                    .setNegativeButton(getString(R.string.lock_cancel), null)
                                    .show();

                        } else {
                            DialogHelper.getInstance().setMessage(getString(R.string.lock_frozen_tips))
                                    .setPositiveButton(getString(R.string.lock_frozen), (dialog, which) -> keyManagerVM.frozenKey(keyInfo.getKeyId()))
                                    .setNegativeButton(getString(R.string.lock_cancel), null)
                                    .show();
                        }

                    });
                }
                getView(R.id.close_window).setOnClickListener(v -> getPopupWindow().dismiss());
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(() -> {
                    setWindowAlpha(1f);
                    initDarkToolbar();
                });
            }
        };
        initLightToolbar();
        popupWindow.showAtLocationWithAnim(mBinding.getRoot(), Gravity.TOP, 0, 0, R.style.animTranslate);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_EDIT_KEY:
                    if (data == null) return;
                    if (data.hasExtra(Constance.KEY_INFO)) {
                        Key item = (Key) data.getSerializableExtra(Constance.KEY_INFO);
                        if (item != null) {
                            editKey(item);
                        }
                    } else if (data.hasExtra(Constance.EDIT_NAME)) {
                        String keyName = data.getStringExtra(Constance.EDIT_NAME);
                        if (!TextUtils.isEmpty(keyName)) {
                            editKeyName(keyInfo, keyName);
                        }
                    }
                    break;
            }
        }
    }

    private void editKeyName(Key k, String name) {
        keyManagerVM.editKeyName(k, name);
    }

    private void editKey(Key k) {
        keyManagerVM.editKey(k);
    }

}
