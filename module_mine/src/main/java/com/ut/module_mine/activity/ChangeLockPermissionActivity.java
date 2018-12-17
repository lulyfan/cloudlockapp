package com.ut.module_mine.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityChangeLockPermissionBinding;
import com.ut.module_mine.databinding.ItemChangeLockPermissionBinding;

import java.util.ArrayList;
import java.util.List;

public class ChangeLockPermissionActivity extends BaseActivity {

    private ActivityChangeLockPermissionBinding binding;
    private DataBindingAdapter<Data, ItemChangeLockPermissionBinding> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_lock_permission);
        initUI();
    }

    private void initUI() {
        initCheckAll(() -> {
            List<Data> list = adapter.getData();
            for (Data data : list) {
                data.isChangePermission = true;
            }
            adapter.setData(list);
        });
        initLightToolbar();
        setTitle(getString(R.string.transformLock));

        setLockList();

        binding.nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangeLockPermissionActivity.this, ReceiverSettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setLockList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvLockList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_change_lock_permission, BR.changeLockPermissionData);
        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            boolean isChecked = selectedbinding.checkBox.isChecked();
            selectedbinding.checkBox.setChecked(!isChecked);
        });

        List<Data> list = new ArrayList<>();
        list.add(new Data("优特智能锁", true));
        list.add(new Data("优特智能锁", false));
        list.add(new Data("优特智能锁", true));

        adapter.setData(list);
        binding.rvLockList.setAdapter(adapter);
    }

    public static class Data {
        public String lockName;
        public boolean isChangePermission;

        public Data(String lockName, boolean isChangePermission) {
            this.lockName = lockName;
            this.isChangePermission = isChangePermission;
        }
    }
}
