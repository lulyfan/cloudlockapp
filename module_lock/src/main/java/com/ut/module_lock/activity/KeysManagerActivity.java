package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.adapter.ListAdapter;
import com.ut.base.common.CommonPopupWindow;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.BR;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityKeysManagerBinding;
import com.ut.module_lock.entity.Key;
import com.ut.module_lock.viewmodel.KeyManagerVM;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.KEY_MANAGER)
public class KeysManagerActivity extends BaseActivity {

    private KeyManagerVM kmVM = null;
    private ActivityKeysManagerBinding mBinding = null;
    private ListAdapter<Key> mAdapter = null;
    private List<Key> keyList = new ArrayList<>();
    private String mMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_manager);
        LockKey lockKey = getIntent().getParcelableExtra(Constance.LOCK_KEY);
        mMac = lockKey.getMac();
        //ToDo
        mMac = "33-33-22-A1-B0-34";
        initTitle();
        init();
    }

    private void initTitle() {
        initDarkToolbar();
        initMore(this::popupMoreWindow);
        setTitle(R.string.func_manage_key);
    }

    private void init() {
        mAdapter = new ListAdapter<Key>(this, R.layout.item_keys_manager, keyList, BR.keyItem);
        mBinding.list.setAdapter(mAdapter);
        kmVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        kmVM.setMac(mMac);
        kmVM.getKeys().observe(this, (keyItems) -> {
            if (keyItems == null || keyItems.isEmpty()) return;
            if (mBinding.refreshLayout.isLoading()) {
                mAdapter.loadDate(keyItems);
                mBinding.refreshLayout.postDelayed(() -> mBinding.refreshLayout.setLoading(false), 200L);
            } else {
                mAdapter.updateDate(keyItems);
            }
        });
        mBinding.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mBinding.refreshLayout.setListViewFooter(mBinding.refreshLayout.createLoadingFooter());
        mBinding.refreshLayout.setOnRefreshListener(() -> {
            mBinding.refreshLayout.setRefreshing(true);
            kmVM.updateKeyItems();
            mBinding.refreshLayout.postDelayed(() -> {
                mBinding.refreshLayout.setRefreshing(false);
            }, 2000L);
        });
        mBinding.refreshLayout.setOnLoadListener(() -> {
            if (!keyList.isEmpty() && keyList.size() - 1 >= kmVM.getDefaultPageSize()) {
                loadData();
            } else {
                mBinding.refreshLayout.setLoading(false);
            }
        });
        mBinding.list.setOnItemClickListener((parent, view, position, id) -> {
            Key keyItem = keyList.get(position);
            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_INFO).withSerializable(Constance.KEY_INFO, keyItem).navigation();
        });

        updateData();
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

    private void updateData() {
        kmVM.updateKeyItems();
    }

    private void loadData() {
        kmVM.loadKeyItems();
    }

    private void sendKey() {

    }

    private void clearKey() {

    }

}
