package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ut.base.BaseActivity;
import com.ut.database.entity.LockUserKey;
import com.ut.module_mine.BR;
import com.ut.module_mine.Constant;
import com.ut.module_mine.R;
import com.ut.module_mine.adapter.DataBindingAdapter;
import com.ut.module_mine.databinding.ActivityLockUserItemBinding;
import com.ut.module_mine.databinding.MineItemLockListBinding;
import com.ut.module_mine.viewModel.LockUserItemViewModel;

import java.util.ArrayList;
import java.util.List;

public class LockUserItemActivity extends BaseActivity {
    public static final String EXTRA_USER_NAME = "userName";
    private ActivityLockUserItemBinding binding;
    private LockUserItemViewModel viewModel;
    public static final String EXTRA_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_user_item);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LockUserItemViewModel.class);
        viewModel.tip.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                toastShort(s);
            }
        });
        viewModel.lockUserKeys.observe(this, new Observer<List<LockUserKey>>() {
            @Override
            public void onChanged(@Nullable List<LockUserKey> lockUserKeys) {
                List<Data> dataList = new ArrayList<>();
                for (LockUserKey key : lockUserKeys) {
                    String keyType = "";
                    switch (key.getRuleType()) {
                        case Constant.TYPE_KEY_FOREVER:
                            keyType = getString(R.string.mine_forever);
                            break;

                        case Constant.TYPE_KEY_LIMIT_TIME:
                            keyType = getString(R.string.mine_limitTime);
                            break;

                        case Constant.TYPE_KEY_ONCE:
                            keyType = getString(R.string.mine_once);
                            break;

                        case Constant.TYPE_KEY_LOOP:
                            keyType = getString(R.string.mine_loop);
                            break;

                            default:
                    }
                    Data data = new Data(key.getLockName(), keyType);
                    dataList.add(data);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.loadLockUserKey();
    }

    private void initUI() {
        setActionBar();
        setLockList();

        if (getIntent() != null) {
            viewModel.userId = getIntent().getLongExtra(EXTRA_USER_ID, -1);
        }
    }

    private void setActionBar() {
        initLightToolbar();

        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        setTitle(userName);
    }

    private void setLockList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.lockList.setLayoutManager(layoutManager);

        DataBindingAdapter<Data, MineItemLockListBinding> adapter =
                new DataBindingAdapter<>(this, R.layout.mine_item_lock_list, BR.userLock);

        List<Data> list = new ArrayList<>();
        list.add(new Data("优特智能锁", "永久"));
        list.add(new Data("优特智能锁", "限时"));
        list.add(new Data("优特智能锁", "限次"));

        adapter.setData(list);
        binding.lockList.setAdapter(adapter);
    }


    public static class Data {
        public String lockName;
        public String keyType;

        public Data(String lockName, String keyType) {
            this.lockName = lockName;
            this.keyType = keyType;
        }
    }
}
