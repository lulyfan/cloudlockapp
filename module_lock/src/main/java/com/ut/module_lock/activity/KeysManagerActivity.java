package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonPopupWindow;
import com.ut.module_lock.BR;
import com.ut.module_lock.R;
import com.ut.module_lock.adapter.RecyclerListAdapter;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityKeysManagerBinding;
import com.ut.module_lock.entity.KeyItem;
import com.ut.module_lock.entity.LockKey;
import com.ut.module_lock.viewmodel.KeyManagerVM;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.KEY_MANAGER)
public class KeysManagerActivity extends BaseActivity {

    private KeyManagerVM kmVM = null;
    private ActivityKeysManagerBinding mBinding = null;
    private RecyclerListAdapter<KeyItem> mAdapter = null;
    private List<KeyItem> keyItemList = new ArrayList<>();
    private String mMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_manager);
        LockKey lockKey = getIntent().getParcelableExtra("lock_key");
        mMac = lockKey.getMac();
        initTitle();
        init();
    }

    private void initTitle() {
        initDarkToolbar();
        initMore(this::popupMoreWindow);
        setTitle(R.string.func_manage_key);
    }

    private void init() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.list.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerListAdapter<>(keyItemList, R.layout.item_keys_manager, BR.keyItem);
        mBinding.list.setAdapter(mAdapter);
        kmVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        kmVM.setMac(mMac);
        kmVM.getKeys().observe(this, (keyItems) -> {
            if (keyItems == null || keyItems.isEmpty()) return;
            mAdapter.addData(keyItems);
        });
        mAdapter.setOnItemListener((v, position) -> {
            KeyItem keyItem = keyItemList.get(position);
            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_INFO).withSerializable(Constance.KEY_INFO, keyItem).navigation();
        });
    }

    private void popupMoreWindow() {
        setWindowAlpha(0.5f);
        CommonPopupWindow popupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {
                getView(R.id.item1).setOnClickListener(v -> {
                    clearKey();
                    getPopupWindow().dismiss();
                });
                getView(R.id.item2).setOnClickListener(v -> {
                    sendKey();
                    getPopupWindow().dismiss();
                });
                getView(R.id.close_window).setOnClickListener(v -> getPopupWindow().dismiss());
                setLightStatusBar();
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(() -> {
                            setWindowAlpha(1f);
                            setDarkStatusBar();
                        }
                );
            }
        };
        popupWindow.showAtLocationWithAnim(mBinding.getRoot(), Gravity.TOP, 0, 0, R.style.animTranslate);
    }

    private void loadData() {
        kmVM.loadKeyItems();
    }

    private void sendKey() {

    }

    private void clearKey() {

    }

}
