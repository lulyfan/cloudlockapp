package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
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
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.Utils.Util;
import com.ut.database.entity.LockGroup;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.base.Utils.DialogUtil;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockGroupBinding;
import com.ut.module_mine.databinding.ItemLockGroupBinding;
import com.ut.module_mine.viewModel.LockGroupViewModel;

import java.util.ArrayList;
import java.util.List;

public class LockGroupActivity extends BaseActivity {

    private ActivityLockGroupBinding binding;
    private DataBindingAdapter<LockGroupData, ItemLockGroupBinding> adapter;
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
        viewModel.lockGroups.observe(this, new Observer<List<LockGroup>>() {
            @Override
            public void onChanged(@Nullable List<LockGroup> lockGroups) {
                List<LockGroupData> lockGroupDataList = new ArrayList<>();
                for (LockGroup lockGroup : lockGroups) {
                    LockGroupData item = new LockGroupData(lockGroup.getId(), lockGroup.getName(), 0);
                    lockGroupDataList.add(item);
                }
                adapter.setData(lockGroupDataList);
            }
        });
        viewModel.addGroupSuccess.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                viewModel.loadLockGroup();
            }
        });
        viewModel.tip.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                toastShort(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadLockGroup();
    }

    private void initUI() {
        initAdd(() -> addLockGroup());
        initLightToolbar();
        setTitle(getString(R.string.lockGroup));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockGroup.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_lock_group, BR.lockGroupItem);

        List<LockGroupData> list = new ArrayList<>();
        list.add(new LockGroupData(1,"全部分组", 8));
        list.add(new LockGroupData(2,"一楼办公区", 18));
        list.add(new LockGroupData(3,"二楼仓库", 20));
        list.add(new LockGroupData(4,"五楼会议室", 7));
        list.add(new LockGroupData(5,"7楼机房", 20));
        adapter.setData(list);
        binding.rvLockGroup.setAdapter(adapter);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockGroupActivity.this, LockGroupItemActivity.class);
            intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_NAME, selectedbinding.lockGroupName.getText());
            intent.putExtra(LockGroupItemActivity.EXTRA_LOCK_GROUP_ID, adapter.getItemData(position).groupId);
            startActivity(intent);
        });
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
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {
                        String groupName = et_groupName.getText().toString();
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
