package com.ut.module_mine.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.Utils.Util;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.base.Utils.DialogUtil;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockGroupBinding;
import com.ut.module_mine.databinding.ItemLockGroupBinding;

import java.util.ArrayList;
import java.util.List;

public class LockGroupActivity extends BaseActivity {

    private ActivityLockGroupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.appBarColor, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_group);
        initUI();
    }

    private void initUI() {
        setSupportActionBar(binding.toolbar2);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_black);
        actionBar.setTitle(null);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockGroup.setLayoutManager(layoutManager);
        binding.rvLockGroup.addItemDecoration(new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_ITEM));

        DataBindingAdapter<LockGroupData, ItemLockGroupBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_lock_group, BR.lockGroupItem);
        adapter.setItemHeightByPercent(0.0708);

        List<LockGroupData> list = new ArrayList<>();
        list.add(new LockGroupData("全部分组", 8));
        list.add(new LockGroupData("一楼办公区", 18));
        list.add(new LockGroupData("二楼仓库", 20));
        list.add(new LockGroupData("五楼会议室", 7));
        list.add(new LockGroupData("7楼机房", 20));
        adapter.setData(list);
        binding.rvLockGroup.setAdapter(adapter);

        adapter.setOnClickItemListener(new DataBindingAdapter.OnClickItemListener<ItemLockGroupBinding>() {
            @Override
            public void onClick(ItemLockGroupBinding selectedbinding, int position, ItemLockGroupBinding lastSelectedBinding) {
                Intent intent = new Intent(LockGroupActivity.this, LockGroupItemActivity.class);
                intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_NAME, selectedbinding.lockGroupName.getText());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lockgroup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.addGroup) {
            addLockGroup();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }

    }

    public static class LockGroupData {
        public String name;
        public int lockCount;

        public LockGroupData(String name, int lockCount) {
            this.name = name;
            this.lockCount = lockCount;
        }
    }

    public void addLockGroup() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_addgroup, null);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        int i = view.getId();
                        if (i == R.id.cancel) {
                            dialog.dismiss();

                        } else if (i == R.id.confirm) {
                            dialog.dismiss();

                        } else {
                        }
                    }
                })
                .create();

        dialog.show();
    }
}
