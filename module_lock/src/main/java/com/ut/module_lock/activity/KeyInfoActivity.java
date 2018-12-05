package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonPopupWindow;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityKeyInfoBinding;
import com.ut.module_lock.entity.KeyItem;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.KEY_INFO)
public class KeyInfoActivity extends BaseActivity {

    private KeyItem keyInfo;
    private ActivityKeyInfoBinding mBinding = null;
    private static final int REQUEST_EDIT_KEY_NAME = 1111;
    private static final int REQUEST_EDIT_LIMITED_TIME = 1112;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_key_info);
        enableImmersive(R.color.title_bar_bg, false);
        keyInfo = (KeyItem) getIntent().getSerializableExtra("keyInfo");
        mBinding.setKeyItem(keyInfo);
        mBinding.back.setOnClickListener(v -> finish());
        mBinding.operationRecord.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD).navigation());
        mBinding.tvKeyName.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_KEY_NAME).navigation(this, REQUEST_EDIT_KEY_NAME));
        mBinding.tvKeyType.setOnClickListener(v -> ARouter.getInstance().build(RouterUtil.LockModulePath.EDIT_LIMITED_TIME).withSerializable("keyInfo", keyInfo).navigation(this, REQUEST_EDIT_LIMITED_TIME));
        mBinding.more.setOnClickListener(v -> popupMoreWindow());
        mBinding.btnDeleteKey.setOnClickListener(v -> deleteKey());
    }

    private void deleteKey() {

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
                });
                getView(R.id.close_window).setOnClickListener(v -> getPopupWindow().dismiss());
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(() -> {
                    setWindowAlpha(1f);
                    enableImmersive(R.color.title_bar_bg, false);
                });
            }
        };
        enableImmersive(R.color.white, true);
        popupWindow.showAtLocationWithAnim(mBinding.getRoot(), Gravity.TOP, 0, 0, R.style.animTranslate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_EDIT_KEY_NAME:
                    if (data == null) return;
                    String keyName = data.getStringExtra("edit_key_name");
                    keyInfo.setCaption(keyName);
                    mBinding.setKeyItem(keyInfo);
                    break;
            }
        }
    }

    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }
}
