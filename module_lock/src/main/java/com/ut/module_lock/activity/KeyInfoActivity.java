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
//            hasEdited = true;
            finish();
        });

        mBinding.setKeyItem(keyInfo);
        keyManagerVM.setKey(keyInfo);
        keyManagerVM.getKeyById(keyInfo.getKeyId()).observe(this, key -> {
            keyInfo = key;
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
            initMore(this::popupMoreWindow);
        }

    }

    private void initListener() {
        mBinding.keyNameSelection.setOnClickListener(v -> ARouter.getInstance()
                .build(RouterUtil.LockModulePath.EDIT_NAME)
                .withString("edit_name_title", getString(R.string.key_name))
                .withLong("key_id", keyInfo.getKeyId())
                .withString("name", keyInfo.getKeyName())
                .navigation(this, REQUEST_EDIT_KEY));
        mBinding.keyTypeSelection.setOnClickListener(v -> {
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
        CustomerAlertDialog dialog = new CustomerAlertDialog(this, false);
        dialog.setMsg(getString(R.string.lock_delete_key_tips));
        dialog.setConfirmText(getString(R.string.lock_delete));
        dialog.setConfirmListener(v -> keyManagerVM.deleteKey(keyInfo.getKeyId()));
        dialog.setCancelText(getString(R.string.lock_cancel));
        dialog.setCancelLister(null);
        dialog.show();
    }

    private void popupMoreWindow() {
        setWindowAlpha(0.5f);
        CommonPopupWindow popupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                TextView item1 = getView(R.id.item1);
                if (managerUserType == EnumCollection.UserType.ADMIN.ordinal() && keyInfo.getUserType() != EnumCollection.UserType.ADMIN.ordinal()) {
                    item1.setText(keyInfo.getUserType() == EnumCollection.UserType.AUTH.ordinal() ? getString(R.string.lock_cancel_auth) : getString(R.string.lock_to_auth));
                    item1.setOnClickListener(v -> {
                        getPopupWindow().dismiss();
                        //ToDO
                        //授权 or not
                        if (keyInfo.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
                            new CustomerAlertDialog(KeyInfoActivity.this, false)
                                    .setMsg(getString(R.string.lock_key_cancel_auth_title) + getString(R.string.lock_key_cancel_auth_tips))
                                    .setCancelText(getString(R.string.lock_btn_confirm))
                                    .setConfirmListener(v1 -> keyManagerVM.cancelAuth(keyInfo.getKeyId()))
                                    .setCancelText(getString(R.string.lock_cancel))
                                    .show();

                        } else if (keyInfo.getUserType() == EnumCollection.UserType.NORMAL.ordinal()) {
                            new CustomerAlertDialog(KeyInfoActivity.this, false)
                                    .setMsg(getString(R.string.lock_key_auth_title) + getString(R.string.lock_key_auth_tips))
                                    .setConfirmText(getString(R.string.lock_auth))
                                    .setConfirmListener(v1 -> keyManagerVM.toAuth(keyInfo.getKeyId()))
                                    .setCancelText(getString(R.string.lock_cancel))
                                    .show();
                        }

                    });
                } else {
                    item1.setVisibility(View.GONE);
                }

                TextView item2 = getView(R.id.item2);
                if (keyInfo.getStatus() == EnumCollection.KeyStatus.DELETING.ordinal() || keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_DELETE.ordinal()) {
                    item2.setVisibility(View.GONE);
                } else {
                    item2.setText(keyInfo.getStatus() == EnumCollection.KeyStatus.HAS_FREEZE.ordinal() ? getString(R.string.lock_unfrozen) : getString(R.string.lock_frozen));
                    item2.setOnClickListener(v -> {
                        getPopupWindow().dismiss();
                        if (keyInfo.isFrozened()) {
                            new CustomerAlertDialog(KeyInfoActivity.this, false)
                                    .setMsg(getString(R.string.lock_unfrozen_tips))
                                    .setConfirmText(getString(R.string.lock_unfrozen))
                                    .setConfirmListener(v1 ->
                                            keyManagerVM.unFrozenKey(keyInfo.getKeyId()))
                                    .setCancelText(getString(R.string.lock_cancel))
                                    .setCancelLister(null)
                                    .show();

                        } else {
                            new CustomerAlertDialog(KeyInfoActivity.this, false)
                                    .setMsg(getString(R.string.lock_frozen_tips))
                                    .setConfirmText(getString(R.string.lock_frozen))
                                    .setConfirmListener(v1 ->
                                            keyManagerVM.frozenKey(keyInfo.getKeyId()))
                                    .setCancelText(getString(R.string.lock_cancel))
                                    .setCancelLister(null)
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
                            editkeyName(keyInfo, keyName);
                        }
                    }
                    break;
            }
        }
    }

    private void editkeyName(Key k, String name) {
        keyManagerVM.editKeyName(k, name);
    }

    private void editKey(Key k) {
        keyManagerVM.editKey(k);
    }


    @Override
    public void finish() {
        if (hasEdited) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
        }
        super.finish();
    }
}
