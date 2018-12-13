package com.ut.module_mine.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ut.base.BaseActivity;
import com.ut.module_mine.BR;
import com.ut.module_mine.util.BottomLineItemDecoration;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityLockUserBinding;
import com.ut.module_mine.databinding.ItemLockUserBinding;

import java.util.ArrayList;
import java.util.List;

public class LockUserActivity extends BaseActivity {

    private ActivityLockUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_user);
        initUI();
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.lockUserManager));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.userList.setLayoutManager(layoutManager);

        DataBindingAdapter<User, ItemLockUserBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.item_lock_user, BR.user);

        adapter.setOnClickItemListener((selectedbinding, position, lastSelectedBinding) -> {
            Intent intent = new Intent(LockUserActivity.this, LockUserItemActivity.class);
            intent.putExtra(LockUserItemActivity.EXTRA_USER_NAME, selectedbinding.userName.getText());
            startActivity(intent);
        });

        List<User> list = new ArrayList<>();
        list.add(new User("", "Sam", "18807644294"));
        list.add(new User("", "Sam", "18807644294"));
        list.add(new User("", "Sam", "18807644294"));

        adapter.setData(list);
        binding.userList.setAdapter(adapter);
    }

    public static class User {
        public String headImgUrl;
        public String userName;
        public String phoneNum;

        public User(String headImgUrl, String userName, String phoneNum) {
            this.headImgUrl = headImgUrl;
            this.userName = userName;
            this.phoneNum = phoneNum;
        }

    }
}
