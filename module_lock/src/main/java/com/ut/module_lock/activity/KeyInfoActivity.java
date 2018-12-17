package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonPopupWindow;
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityKeyInfoBinding;
import com.ut.module_lock.entity.KeyItem;
import com.ut.module_lock.viewmodel.KeyManagerVM;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.KEY_INFO)
public class KeyInfoActivity extends BaseActivity {

    private KeyItem keyInfo;
    private ActivityKeyInfoBinding mBinding = null;
    private static final int REQUEST_EDIT_KEY = 1111;

    private KeyManagerVM keyManagerVM = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_key_info);
        keyInfo = (KeyItem) getIntent().getSerializableExtra(Constance.KEY_INFO);
        keyManagerVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        keyManagerVM.getFeedbackMessage().observe(this, message -> CLToast.showAtBottom(KeyInfoActivity.this, message));
        mBinding.setKeyItem(keyInfo);
        initTitle();
        initListener();

    }

    private void initTitle() {
        initDarkToolbar();
        setTitle(R.string.lock_key_info);
        initMore(this::popupMoreWindow);

    }

    private void initListener() {

        mBinding.keyNameSelection.setOnClickListener(v -> ARouter.getInstance()
                .build(RouterUtil.LockModulePath.EDIT_KEY_NAME)
                .withSerializable(Constance.KEY_INFO, keyInfo)
                .navigation(this, REQUEST_EDIT_KEY));
        mBinding.keyTypeSelection.setOnClickListener(v -> {
            String url;
            if (keyInfo.getRuleType() == 2) {
                url = RouterUtil.LockModulePath.EDIT_LIMITED_TIME;
            } else {
                url = RouterUtil.LockModulePath.EDIT_LOOP_TIME;
            }
            ARouter.getInstance().build(url).withSerializable(Constance.KEY_INFO, keyInfo).navigation(this, REQUEST_EDIT_KEY);
        });
        mBinding.operationRecord.setOnClickListener(v ->
                {
                    ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD).withString(Constance.RECORD_TYPE, Constance.BY_KEY).withLong(Constance.KEY_ID, keyInfo.getKeyId()).navigation();
                }
        );
        mBinding.btnDeleteKey.setOnClickListener(v -> deleteKey());
    }

    private void deleteKey() {
        keyManagerVM.deleteKey(keyInfo.getKeyId());
    }

    private void popupMoreWindow() {
        setWindowAlpha(0.5f);
        CommonPopupWindow popupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                TextView item1 = getView(R.id.item1);
                item1.setText("授权/取消授权");
                item1.setOnClickListener(v -> {
                    //ToDO
                    getPopupWindow().dismiss();
                });
                TextView item2 = getView(R.id.item2);
                item2.setText("冻结/解除冻结");
                item2.setOnClickListener(v -> {
                    //ToDO
                    getPopupWindow().dismiss();

                    if (keyInfo.isForzened()) {
                        keyManagerVM.unFrozenKey(keyInfo.getKeyId());
                    } else {
                        keyManagerVM.frozenKey(keyInfo.getKeyId());
                    }

                });
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
                        KeyItem item = (KeyItem) data.getSerializableExtra(Constance.KEY_INFO);
                        if (item != null) {
                            keyInfo = item;
                        }
                    } else if (data.hasExtra(Constance.EDIT_KEY_NAME)) {
                        String keyName = data.getStringExtra(Constance.EDIT_KEY_NAME);
                        if (!TextUtils.isEmpty(keyName)) {
                            keyInfo.setKeyName(keyName);
                        }
                    }
                    mBinding.setKeyItem(keyInfo);
                    break;
            }
        }
    }
}
