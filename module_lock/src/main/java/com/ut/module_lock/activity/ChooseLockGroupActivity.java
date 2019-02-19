package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.adapter.ListAdapter;
import com.ut.base.dialog.DialogHelper;
import com.ut.database.entity.LockGroup;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.BR;
import com.ut.module_lock.R;
import com.ut.module_lock.viewmodel.LockSettingVM;

import java.util.ArrayList;
import java.util.List;


/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :选择钥匙分组界面
 */


@SuppressLint("CheckResult")
@Route(path = RouterUtil.LockModulePath.CHOOSE_LOCK_GROUP)
public class ChooseLockGroupActivity extends BaseActivity {
    private List<LockGroup> lockGroups = new ArrayList<>();
    private LockKey lockKey;
    private ListAdapter<LockGroup> adapter;
    private long currentGroupId = -1;
    private LockSettingVM lockSettingVM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_choose_group);
        lockKey = getIntent().getParcelableExtra("lock_key");
        currentGroupId = lockKey.getGroupId();
        lockSettingVM = ViewModelProviders.of(this).get(LockSettingVM.class);
        lockSettingVM.setLockKey(lockKey);
        initDarkToolbar();
        setTitle(getString(R.string.choose_group));
        initAdd(this::createGroup);
        initUI();
        lockSettingVM.getLockGroups().observe(this, lgs -> {
            adapter.updateDate(lgs);
        });

        lockSettingVM.getSelectedGroupId().observe(this, id -> {
            currentGroupId = id;
            adapter.notifyDataSetChanged();
        });
    }

    private void initUI() {
        ListView listView = findViewById(R.id.group_list);
        adapter = new ListAdapter<LockGroup>(this, R.layout.item_lock_group, lockGroups, BR.lockGroup) {
            @Override
            public void handleItem(ViewDataBinding binding, int position) {
                long groupId = lockGroups.get(position).getId();
                CheckBox checkBox = binding.getRoot().findViewById(R.id.check_box);
                checkBox.setChecked(currentGroupId == groupId);
                checkBox.setOnClickListener(v -> {
                    if (checkBox.isChecked()) {
                        currentGroupId = groupId;
                        lockSettingVM.changeLockGroup(lockKey.getMac(), groupId);
                    } else {
                        lockSettingVM.delLockFromGroup(lockKey.getMac(), currentGroupId);
                        currentGroupId = -1;
                    }
                    notifyDataSetChanged();
                });
            }
        };
        listView.setAdapter(adapter);
    }

    private void createGroup() {
        View contentView = View.inflate(this, R.layout.dialog_edit, null);
        EditText edt = contentView.findViewById(R.id.edt);
        edt.setHint(R.string.lock_create_group_hint);
        DialogHelper.getInstance().setContentView(contentView)
                .setTitle(getString(R.string.lock_create_group))
                .setPositiveButton(getString(R.string.lock_btn_confirm), (dialog, which) -> {
                    SystemUtils.hideKeyboard(getBaseContext(), edt);
                    lockSettingVM.createGroup(edt.getText().toString().trim());
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.lock_cancel), null)
                .show();
    }

//    private void loadGroup() {
//        lockSettingVM.loadLockGroups();
//    }
}
