package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.adapter.ListAdapter;
import com.ut.base.common.CommonPopupWindow;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.BR;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityKeysManagerBinding;
import com.ut.database.entity.Key;
import com.ut.module_lock.viewmodel.KeyManagerVM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.KEY_MANAGER)
public class KeysManagerActivity extends BaseActivity {

    private static final int REQUEST_CODE_KEY_INFO = 1121;
    private KeyManagerVM kmVM = null;
    private ActivityKeysManagerBinding mBinding = null;
    private ListAdapter<Key> mAdapter = null;
    private List<Key> keyList = new ArrayList<>();
    private String mMac = "";
    private LockKey lockKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_manager);
        lockKey = getIntent().getParcelableExtra(Constance.LOCK_KEY);
        mMac = lockKey.getMac();
        initTitle();
        init();
    }

    private void initTitle() {
        initDarkToolbar();
        if (lockKey.getUserType() == EnumCollection.UserType.ADMIN.ordinal() || lockKey.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
            initMore(this::popupMoreWindow);
        }
        setTitle(R.string.func_manage_key);
    }

    private void init() {
        mAdapter = new ListAdapter<Key>(this, R.layout.item_keys_manager, keyList, BR.keyItem) {
            @Override
            public void handleItem(ViewDataBinding binding, int position) {
                kmVM.initKey(keyList.get(position));
                ImageView ruleTypeIv = binding.getRoot().findViewById(R.id.tip);
                int rId = keyList.get(position).getRuleTypeDrawableId();
                ruleTypeIv.setBackgroundResource(rId);
            }
        };
        mBinding.list.setAdapter(mAdapter);
        kmVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        kmVM.setMac(mMac);
        kmVM.getKeys(mMac).observe(this, (keyItems) -> {
            if (keyItems == null) {
                keyItems = new ArrayList<>();
            }
            Collections.sort(keyItems, (o1, o2) -> o1.getStatus() < o2.getStatus() ? -1 : 0);
            if (mBinding.list.isLoading()) {
                mAdapter.loadDate(keyItems);
                mBinding.list.setLoadCompleted();
            } else {
                mBinding.refreshLayout.setRefreshing(false);
                mAdapter.updateDate(keyItems);
                mBinding.nodata.setVisibility(keyItems.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
        mBinding.refreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        mBinding.refreshLayout.setOnRefreshListener(this::updateData);

        mBinding.list.setOnLoadMoreListener(() -> {
            loadData();
            mBinding.list.postDelayed(() -> {
                mBinding.list.setLoadCompleted();
            }, 1200L);
        });

        mBinding.list.setOnItemClickListener((parent, view, position, id) -> {
            Key keyItem = keyList.get(position);
            if (keyItem.getStatus() != 4) {
                keyItem.setMac(mMac);

                ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_INFO)
                        .withInt(Constance.USERTYPE, lockKey.getUserType())
                        .withSerializable(Constance.KEY_INFO, keyItem)
                        .navigation(this, REQUEST_CODE_KEY_INFO);
            }
        });
    }

    private void popupMoreWindow() {
        setWindowAlpha(0.5f);
        CommonPopupWindow popupWindow = new CommonPopupWindow(this, R.layout.layout_popup_two_selections,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT) {
            @Override
            protected void initView() {

                if (lockKey.getUserType() == EnumCollection.UserType.ADMIN.ordinal() || lockKey.getUserType() == EnumCollection.UserType.AUTH.ordinal()) {
                    getView(R.id.item1).setOnClickListener(v -> {
                        //todo 弹窗
                        new AlertDialog.Builder(KeysManagerActivity.this)
                                .setMessage(R.string.lock_clear_all_keys_tips)
                                .setPositiveButton(R.string.lock_clear, ((dialog, which) -> {
                                    clearKey(lockKey.getMac());
                                    mBinding.refreshLayout.postDelayed(KeysManagerActivity.this::updateData, 500L);
                                }))
                                .setNegativeButton(R.string.lock_cancel, null)
                                .show();
                        getPopupWindow().dismiss();
                    });
                } else {
                    getView(R.id.item1).setVisibility(View.GONE);
                }

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
        mBinding.refreshLayout.setRefreshing(true);
        kmVM.updateKeyItems();
        mBinding.refreshLayout.postDelayed(() -> {
            mBinding.refreshLayout.setRefreshing(false);
        }, 1200L);
    }

    private void loadData() {
        kmVM.loadKeyItems();
    }

    private void sendKey() {
        ARouter.getInstance().build(RouterUtil.BaseModulePath.SEND_KEY)
                .withString(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MAC, mMac)
                .withInt(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY_USERTYPE, lockKey.getUserType())
                .navigation();
    }

    private void clearKey(String mac) {
        kmVM.clearKeys(mac);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }
}
