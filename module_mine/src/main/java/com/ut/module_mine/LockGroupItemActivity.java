package com.ut.module_mine;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.design.drawable.DrawableUtils;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.ut.base.BaseActivity;
import com.ut.module_mine.databinding.ActivityLockGroupBinding;
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
        actionBar.setTitle(null);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockList.setLayoutManager(layoutManager);
        binding.rvLockList.addItemDecoration(
                new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_PARENT));

        DataBindingAdapter<String, ItemLockBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_lock, BR.lockName);
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
