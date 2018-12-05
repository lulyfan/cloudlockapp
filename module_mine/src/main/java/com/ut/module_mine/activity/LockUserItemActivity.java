package com.ut.module_mine.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ut.base.BaseActivity;
import com.ut.module_mine.BR;
import com.ut.module_mine.R;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.databinding.ActivityLockUserItemBinding;
import com.ut.module_mine.databinding.MineItemLockListBinding;
import com.ut.module_mine.util.BottomLineItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class LockUserItemActivity extends BaseActivity {
    public static final String EXTRA_USER_NAME = "userName";
    private ActivityLockUserItemBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive(R.color.appBarColor, true);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_user_item);
        initUI();
    }

    private void initUI() {
        setActionBar();
        setLockList();
    }

    private void setActionBar() {
        setSupportActionBar(binding.toolbar8);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left_black);
        actionBar.setTitle(null);

        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        binding.title.setText(userName);
    }

    private void setLockList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.lockList.setLayoutManager(layoutManager);
        binding.lockList.addItemDecoration(
                new BottomLineItemDecoration(this, true, BottomLineItemDecoration.MATCH_ITEM));

        DataBindingAdapter<Lock, MineItemLockListBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.mine_item_lock_list, BR.userLock);
        adapter.setItemHeightByPercent(0.076);

        List<Lock> list = new ArrayList<>();
        list.add(new Lock("优特智能锁", "永久"));
        list.add(new Lock("优特智能锁", "限时"));
        list.add(new Lock("优特智能锁", "限次"));

        adapter.setData(list);
        binding.lockList.setAdapter(adapter);
    }


    public static class Lock {
        public String lockName;
        public String validTime;

        public Lock(String lockName, String validTime) {
            this.lockName = lockName;
            this.validTime = validTime;
        }
    }
}
