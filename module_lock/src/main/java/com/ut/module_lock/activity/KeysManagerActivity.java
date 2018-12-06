package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

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
import com.ut.module_lock.viewmodel.KeyManagerVM;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.KEY_MANAGER)
public class KeysManagerActivity extends BaseActivity {

    private KeyManagerVM kmVM = null;
    private ActivityKeysManagerBinding mBinding = null;
    private RecyclerListAdapter<KeyItem> mAdapter = null;
    private List<KeyItem> keyItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_manager);
        enableImmersive(R.color.title_bar_bg, false);
        init();
        mBinding.more.setOnClickListener(v -> popupMoreWindow());
        loadData();
    }

    private void init() {
        mBinding.back.setOnClickListener(v -> finish());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.list.setLayoutManager(linearLayoutManager);
        mAdapter = new RecyclerListAdapter<>(keyItemList, R.layout.item_keys_manager, BR.keyItem);
        mBinding.list.setAdapter(mAdapter);
        kmVM = ViewModelProviders.of(this).get(KeyManagerVM.class);
        kmVM.getKeys().observe(this, (keyItems) -> {
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
            }

            @Override
            protected void initWindow() {
                super.initWindow();
                getPopupWindow().setOnDismissListener(() -> {
                            setWindowAlpha(1f);
                            enableImmersive(R.color.title_bar_bg, false);
                        }
                );
            }
        };
        enableImmersive(R.color.white, true);
        popupWindow.showAtLocationWithAnim(mBinding.getRoot(), Gravity.TOP, 0, 0, R.style.animTranslate);
    }

    private void sendKey() {

    }

    private void clearKey() {

    }

    private void loadData() {
        List<KeyItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            KeyItem item = new KeyItem();
            item.setCaption("caption " + i);
            item.setDesc("desc......... ");
            item.setType((i + 1) % 4);
            item.setAuthorized((i + 2) % 2 == 0);
            item.setSender("大波阿哥");
            item.setSendTime("2018/09/08 10:55");
            item.setAcceptTime("2018/09/08 11:34");
            item.setStartTime("2018/09/09 11:12");
            item.setEndTime("2018/11/11 12:00");
            if (i < 5) {
                item.setState(2);
            } else {
                item.setState(0);
            }
            item.setAuthorizedType("普通用户");
            items.add(item);
        }
        kmVM.getKeys().postValue(items);
    }


    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
    }

}
