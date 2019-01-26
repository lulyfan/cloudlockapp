package com.ut.module_mine.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.Utils.Util;
import com.ut.database.entity.LockGroup;
import com.ut.module_mine.BR;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockGroupBinding;
import com.ut.module_mine.databinding.ListLockGroupBinding;
import com.ut.module_mine.viewModel.LockGroupViewModel;

import java.util.ArrayList;
import java.util.List;

public class LockGroupActivity extends BaseActivity {

    private ActivityLockGroupBinding binding;
    private DataBindingAdapter<LockGroupData, ListLockGroupBinding> adapter;
    private LockGroupViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_group);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LockGroupViewModel.class);
        viewModel.mLockGroups.observe(this, lockGroups -> {
            List<LockGroupData> lockGroupDataList = new ArrayList<>();
            for (LockGroup lockGroup : lockGroups) {
                LockGroupData item = new LockGroupData(lockGroup.getId(), lockGroup.getName(), 0);
                lockGroupDataList.add(item);
            }
            adapter.setData(lockGroupDataList);
        });
        viewModel.addGroupSuccess.observe(this, aVoid -> viewModel.loadLockGroup(true));
        viewModel.tip.observe(this, s -> toastShort(s));
        viewModel.loadLockGroupState.observe(this, aBoolean -> binding.swipeLayout.setRefreshing(false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadLockGroup(false);
    }

    private void initUI() {
        initAdd(() -> addLockGroup());
        initLightToolbar();
        setTitle(getString(R.string.lockGroup));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockGroup.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.list_lock_group, BR.lockGroupData);
        binding.rvLockGroup.setAdapter(adapter);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockGroupActivity.this, LockGroupItemActivity.class);
            intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_NAME, selectedbinding.lockGroupName.getText());
            intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_ID, adapter.getItemData(position).groupId);
            startActivity(intent);
        });

        binding.swipeLayout.setOnRefreshListener(() -> viewModel.loadLockGroup(true));
        binding.swipeLayout.setColorSchemeResources(R.color.themeColor);
    }

    public static class LockGroupData {
        public long groupId;
        public String name;
        public int lockCount;

        public LockGroupData(long groupId, String name, int lockCount) {
            this.groupId = groupId;
            this.name = name;
            this.lockCount = lockCount;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.getItem(1);
        menuItem.setIcon(R.drawable.add_black);
        return super.onPrepareOptionsMenu(menu);
    }

    public void addLockGroup() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_addgroup, null);
        ImageView clear = view.findViewById(R.id.clear);
        EditText et_groupName = view.findViewById(R.id.et_groupName);
        et_groupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.INVISIBLE);
                }
            }
        });


        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.mine_bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {

                        String groupName = et_groupName.getText().toString();
                        if ("".equals(groupName.trim())) {
                            toastShort(getString(R.string.inputGroupName));
                            return;
                        }
                        viewModel.addLockGroup(groupName);
                        dialog1.dismiss();

                    } else if (i == R.id.clear){
                        et_groupName.setText("");
                    }
                })
                .create();

        dialog.show();
    }
}
