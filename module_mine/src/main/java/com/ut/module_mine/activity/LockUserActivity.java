package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

public class LockUserActivity extends BaseActivity {

    private ActivityLockUserBinding binding;
    private LockUserViewModel viewModel;
    private DataBindingAdapter<User, ItemLockUserBinding> adapter;

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
            List<User> userList = new ArrayList<>();
            for (LockUser lockUser : lockUsers) {
                User user = new User(lockUser.getUserId(), lockUser.getHeadPic(), lockUser.getName(), lockUser.getTelNo());
                userList.add(user);
            }
            adapter.setData(userList);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.loadLockUser();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.lockUserManager));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.userList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.item_lock_user, BR.user);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockUserActivity.this, LockUserItemActivity.class);
            intent.putExtra(LockUserItemActivity.EXTRA_USER_NAME, selectedbinding.userName.getText());
            intent.putExtra(LockUserItemActivity.EXTRA_USER_ID, selectedbinding.getUser().userId);
            startActivity(intent);
        });

        binding.userList.setAdapter(adapter);
    }

    public static class User {
        public String headImgUrl;
        public String userName;
        public String phoneNum;
        public long userId;

        public User(long userId, String headImgUrl, String userName, String phoneNum) {
            this.headImgUrl = headImgUrl;
            this.userName = userName;
            this.phoneNum = phoneNum;
            this.userId = userId;
        }

    }
}
