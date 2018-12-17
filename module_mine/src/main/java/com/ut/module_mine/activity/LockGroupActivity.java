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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_group);
        initUI();
    }

    private void initUI() {
        initAdd(() -> addLockGroup());
        initLightToolbar();
        setTitle(getString(R.string.lockGroup));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockGroup.setLayoutManager(layoutManager);
//        binding.rvLockGroup.addItemDecoration(new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_ITEM));

        DataBindingAdapter<LockGroupData, ItemLockGroupBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_lock_group, BR.lockGroupItem);
//        adapter.setItemHeightByPercent(0.0708);

        List<LockGroupData> list = new ArrayList<>();
        list.add(new LockGroupData("全部分组", 8));
        list.add(new LockGroupData("一楼办公区", 18));
        list.add(new LockGroupData("二楼仓库", 20));
        list.add(new LockGroupData("五楼会议室", 7));
        list.add(new LockGroupData("7楼机房", 20));
        adapter.setData(list);
        binding.rvLockGroup.setAdapter(adapter);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockGroupActivity.this, LockGroupItemActivity.class);
            intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_NAME, selectedbinding.lockGroupName.getText());
            startActivity(intent);
        });
    }

    public static class LockGroupData {
        public String name;
        public int lockCount;

        public LockGroupData(String name, int lockCount) {
            this.name = name;
            this.lockCount = lockCount;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.add);
        menuItem.setIcon(R.drawable.add_black);
        return super.onPrepareOptionsMenu(menu);
    }

    public void addLockGroup() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_addgroup, null);

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {
                        dialog1.dismiss();

                    } else {
                    }
                })
                .create();

        dialog.show();
    }
}
