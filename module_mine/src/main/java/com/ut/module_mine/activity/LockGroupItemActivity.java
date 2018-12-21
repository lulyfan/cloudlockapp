package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.database.entity.Lock;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockGroupItemBinding;
import com.ut.module_mine.databinding.ItemLockBinding;
import com.ut.module_mine.viewModel.LockGroupItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class LockGroupItemActivity extends BaseActivity {

    private ActivityLockGroupItemBinding binding;
    private LockGroupItemViewModel viewModel;
    private DataBindingAdapter<Lock, ItemLockBinding> adapter;
    public static final String EXTRA_LOCK_GROUP_NAME = "lockGroupName";
    public static final String EXTRA_LOCK_GROUP_ID = "lockGroupId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_group_item);
        initViewModel();
        initUI();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LockGroupItemViewModel.class);
        viewModel.locks.observe(this, locks -> adapter.setData(locks));

        viewModel.updateGroupName.observe(this, groupName -> setTitle(groupName));

        viewModel.delGroupSuccess.observe(this, aVoid -> onBackPressed());
        viewModel.tip.observe(this, s -> toastShort(s));
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.loadLockByGroup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lockgroup_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editGroupName) {
            editGroupName();
        } else if (item.getItemId() == R.id.delGroup){
            deleteGroup();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void initUI() {
        initLightToolbar();
        Toolbar toolbar = getToolBar();
        toolbar.setOverflowIcon(ActivityCompat.getDrawable(this, R.drawable.overflow_black));

        String lockGroupName = "";
        if (getIntent() != null) {
            lockGroupName = getIntent().getStringExtra(EXTRA_LOCK_GROUP_NAME);
            setTitle(lockGroupName);
            viewModel.groupId = getIntent().getLongExtra(EXTRA_LOCK_GROUP_ID, -1);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_lock, BR.lock);
        binding.rvLockList.setAdapter(adapter);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module)
                    .withObject("lock", selectedbinding.getLock())
                    .navigation();
        });
    }

    public void editGroupName() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_addgroup, null);
        TextView title = view.findViewById(R.id.content);
        title.setText(getString(R.string.editGroupName));
        EditText et_groupName = view.findViewById(R.id.et_groupName);

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
                        String groupName = et_groupName.getText().toString();
                        viewModel.editGroupName(groupName);
                        dialog1.dismiss();

                    } else {
                    }
                })
                .create();

        dialog.show();
    }

    public void deleteGroup() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_base, null);
        TextView textView = view.findViewById(R.id.content);
        textView.setText(getString(R.string.confirmDelGroup));

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
                        viewModel.delGroup();
                    } else {
                    }
                })
                .create();

        dialog.show();
    }
}
