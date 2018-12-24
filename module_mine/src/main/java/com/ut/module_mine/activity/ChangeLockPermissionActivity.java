package com.ut.module_mine.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;

import com.ut.base.BaseActivity;
import com.ut.database.entity.Lock;
import com.ut.database.entity.LockKey;
import com.ut.module_mine.BR;
import com.ut.module_mine.GlobalData;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityChangeLockPermissionBinding;
import com.ut.module_mine.databinding.ItemChangeLockPermissionBinding;
import com.ut.module_mine.viewModel.ChangeLockPermissionViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChangeLockPermissionActivity extends BaseActivity {

    private ActivityChangeLockPermissionBinding mBinding;
    private ChangeLockPermissionViewModel viewModel;
    private DataBindingAdapter<Data, ItemChangeLockPermissionBinding> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_lock_permission);
        initUI();
        initViewMOdel();
    }

    private void initViewMOdel() {
        viewModel = ViewModelProviders.of(this).get(ChangeLockPermissionViewModel.class);
        viewModel.locks.observe(this, locks -> {
            List<Data> dataList = new ArrayList<>();
            for (LockKey lock : locks) {
                Data data = new Data(lock.getName(), lock.getMac(), false);
                dataList.add(data);
            }
            adapter.setData(dataList);
        });
    }

    private void initUI() {
        initCheckAll(() -> {
            List<Data> list = adapter.getData();
            for (Data data : list) {
                data.isChangePermission.set(true);
            }
            adapter.setData(list);
        });
        initLightToolbar();
        setTitle(getString(R.string.transformLock));

        setLockList();

        mBinding.nextStep.setOnClickListener(v -> {
            Intent intent = new Intent(ChangeLockPermissionActivity.this, ReceiverSettingActivity.class);
            startActivity(intent);

            //保存要转移的锁mac地址
            List<Data> dataList = adapter.getData();
            String changeLockMacs = "";
            for (Data data : dataList) {
                if (data.isChangePermission.get()) {
                    changeLockMacs += data.lockMac + ",";
                }
            }
            GlobalData.getInstance().changeLockMacs = changeLockMacs;
        });
    }

    private void setLockList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvLockList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_change_lock_permission, BR.changeLockPermissionData);
        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            boolean isChecked = selectedbinding.checkBox.isChecked();
            selectedbinding.checkBox.setChecked(!isChecked);
        });

        List<Data> list = new ArrayList<>();
        list.add(new Data("优特智能锁", "",false));
        list.add(new Data("优特智能锁", "",false));
        list.add(new Data("优特智能锁", "",false));

        adapter.setData(list);
        mBinding.rvLockList.setAdapter(adapter);
    }

    public class Data {
        public String lockName;
        public String lockMac;
        public ObservableField<Boolean> isChangePermission = new ObservableField<>();

        public Data(String lockName, String lockMac, boolean isChangePermission) {
            this.lockName = lockName;
            this.lockMac = lockMac;
            this.isChangePermission.set(isChangePermission);

            //用户选择要转移的锁后才可以进行下一步
            this.isChangePermission.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    List<Data> dataList = adapter.getData();
                    for (Data data : dataList) {
                        if (data.isChangePermission.get()) {
                            mBinding.nextStep.setEnabled(true);
                            return;
                        }
                    }
                    mBinding.nextStep.setEnabled(false);
                }
            });
        }
    }
}
