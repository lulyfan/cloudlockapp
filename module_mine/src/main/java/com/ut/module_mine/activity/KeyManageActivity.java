package com.ut.module_mine.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ut.base.BaseActivity;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityKeyManageBinding;
import com.ut.module_mine.databinding.ItemKeyBinding;

import java.util.ArrayList;
import java.util.List;

public class KeyManageActivity extends BaseActivity {
    private ActivityKeyManageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_key_manage);
        initUI();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.keyManage));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvKeyList.setLayoutManager(layoutManager);
        binding.rvKeyList.addItemDecoration(
                new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_ITEM));

        DataBindingAdapter<KeyData, ItemKeyBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_key, BR.keyItem);
        adapter.setItemHeightByPercent(0.0708);

        List<KeyData> list = new ArrayList<>();
        list.add(new KeyData("Sam", "2018.11.15 10:00 - 2018.11.16 10:00"));
        list.add(new KeyData("Sam", "2018.11.15 10:00 - 2018.11.16 10:00"));
        list.add(new KeyData("Sam", "2018.11.15 10:00 - 2018.11.16 10:00"));
        list.add(new KeyData("Sam", "2018.11.15 10:00 - 2018.11.16 10:00"));
        list.add(new KeyData("Sam", "2018.11.15 10:00 - 2018.11.16 10:00"));
        adapter.setData(list);
        binding.rvKeyList.setAdapter(adapter);
    }

    public static class KeyData {
        public String keyName;
        public String validTime;

        public KeyData(String keyName, String validTime) {
            this.keyName = keyName;
            this.validTime = validTime;
        }
    }
}
