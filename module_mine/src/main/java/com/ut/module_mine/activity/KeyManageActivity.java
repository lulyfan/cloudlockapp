package com.ut.module_mine.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

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
        enableImmersive(R.color.appBarColor, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_key_manage);
        initUI();
    }

    private void initUI() {
        setSupportActionBar(binding.toolbar4);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_black);
        actionBar.setTitle(null);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.key_manage_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.clearKey) {
            return true;

        } else if (i == R.id.resetKey) {
            return true;

        } else if (i == R.id.sendKey) {
            Intent intent = new Intent(this, GrantPermissionActivity.class);
            startActivity(intent);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
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
