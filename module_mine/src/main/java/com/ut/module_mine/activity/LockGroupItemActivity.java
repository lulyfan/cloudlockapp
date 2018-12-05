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
import com.ut.module_mine.databinding.ActivityLockGroupItemBinding;
import com.ut.module_mine.databinding.ItemLockBinding;

import java.util.ArrayList;
import java.util.List;

public class LockGroupItemActivity extends BaseActivity {

    private ActivityLockGroupItemBinding binding;
    public static final String EXTRA_LOCK_GROUP_NAME = "lockGroupName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.appBarColor, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_group_item);
        initUI();
    }

    private void initUI() {
        String lockGroupName = "";
        if (getIntent() != null) {
            lockGroupName = getIntent().getStringExtra(EXTRA_LOCK_GROUP_NAME);
            binding.title.setText(lockGroupName);
        }

        setSupportActionBar(binding.toolbar3);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_black);
        actionBar.setTitle(null);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockList.setLayoutManager(layoutManager);
        binding.rvLockList.addItemDecoration(
                new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_ITEM));

        DataBindingAdapter<String, ItemLockBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_lock, BR.lockName);
        adapter.setItemHeightByPercent(0.076);

        List<String> lockList = new ArrayList<>();
        lockList.add("物联锁");
        lockList.add("家居锁");
        lockList.add("物联锁");
        lockList.add("家居锁");
        adapter.setData(lockList);
        binding.rvLockList.setAdapter(adapter);

        adapter.setOnClickItemListener(new DataBindingAdapter.OnClickItemListener<ItemLockBinding>() {
            @Override
            public void onClick(ItemLockBinding selectedbinding, int position, ItemLockBinding lastSelectedBinding) {
                Intent intent = new Intent(LockGroupItemActivity.this, KeyManageActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lockgroup_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.editGroupName) {
            return true;
        } else if (i == R.id.batchAuth) {
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
