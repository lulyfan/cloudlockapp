package com.ut.module_mine.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ut.base.BaseActivity;
import com.ut.database.entity.LockUser;
import com.ut.module_mine.BR;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockUserBinding;
import com.ut.module_mine.databinding.ItemLockUserBinding;
import com.ut.module_mine.viewModel.LockUserViewModel;


public class LockUserActivity extends BaseActivity {

    private ActivityLockUserBinding binding;
    private LockUserViewModel viewModel;
    private DataBindingAdapter<LockUser, ItemLockUserBinding> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_user);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LockUserViewModel.class);

        viewModel.mLockUsers.observe(this, lockUsers -> {
            for (LockUser lu : lockUsers) {
                String keyStatusStr = viewModel.getKeyStatusStr(lu.getKeyStatus());
                lu.setKeyStatusStr(keyStatusStr);
            }

            adapter.setData(lockUsers);
        });

        viewModel.loadLockUserState.observe(this, aBoolean -> binding.swipeLayout.setRefreshing(false));
        viewModel.tip.observe(this, s -> toastShort(s));
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.loadLockUser(false);
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.lockUserManager));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.userList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_lock_user, BR.lockUser);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockUserActivity.this, LockUserItemActivity.class);
            intent.putExtra(LockUserItemActivity.EXTRA_USER_NAME, selectedbinding.userName.getText());
            intent.putExtra(LockUserItemActivity.EXTRA_USER_ID, selectedbinding.getLockUser().getUserId());
            startActivity(intent);
        });

        binding.userList.setAdapter(adapter);

        binding.swipeLayout.setOnRefreshListener(() -> viewModel.loadLockUser(true));
        binding.swipeLayout.setColorSchemeResources(R.color.themeColor);
    }
}
