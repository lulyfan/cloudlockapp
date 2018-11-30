package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.BR;
import com.ut.module_lock.R;
import com.ut.module_lock.adapter.RecyclerListAdapter;
import com.ut.module_lock.databinding.ActivityKeysManagerBinding;
import com.ut.module_lock.entity.KeyItem;
import com.ut.module_lock.viewmodel.KeyManagerVM;

import java.util.ArrayList;
import java.util.List;

@Route(path = RouterUtil.LockModulePath.KEY_MANAGER)
public class KeysManagerActivity extends AppCompatActivity {

    private KeyManagerVM kmVM = null;
    private ActivityKeysManagerBinding mBinding = null;
    private RecyclerListAdapter<KeyItem> mAdapter = null;
    private List<KeyItem> keyItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_keys_manager);
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
            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_INFO).withSerializable("keyInfo",keyItem).navigation();
        });

        loadData();
    }

    private void loadData() {
        List<KeyItem> items = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            KeyItem item = new KeyItem();
            item.setCaption("caption " + i);
            item.setDesc("Dexxxxxxxxx ");
            item.setType((i + 1) % 4);
            item.setSender("大波阿哥");
            item.setSender("2018/09/08 10:55");
            item.setAcceptTime("2018/09/08 11:34");
            item.setState("待接受");
            item.setStartTime("2018/09/09 11:12");
            item.setEndTime("2018/11/11 12:00");
            items.add(item);
        }

        mBinding.list.postDelayed(() -> kmVM.getKeys().postValue(items), 1500L);
    }

}
