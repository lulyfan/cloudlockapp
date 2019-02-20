package com.ut.module_mine.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.Utils.Util;
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
    private DataBindingAdapter<Data, MineItemLockListBinding> adapter;
    public static final String EXTRA_USER_ID = "userId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_user_item);
        initViewModel();
        initUI();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(LockUserItemViewModel.class);
        viewModel.tip.observe(this, s -> toastShort(s));

        viewModel.mLockUserKeys.observe(this, lockUserKeys -> {
            binding.swipeLayout.setRefreshing(false);

            List<Data> dataList = new ArrayList<>();
            for (LockUserKey key : lockUserKeys) {
                String keyType = "";
                String keyStatus = getResources().getStringArray(R.array.key_status)[key.getKeyStatus()];
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
                Data data = new Data(key.getLockName(), key.getKeyId(), keyType, keyStatus);
                dataList.add(data);
            }
            adapter.setData(dataList);
        });

        viewModel.loadLockUserKeyState.observe(this, aBoolean -> binding.swipeLayout.setRefreshing(false));
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

        binding.swipeLayout.setOnRefreshListener(() -> viewModel.loadLockUserKey());
        binding.swipeLayout.setColorSchemeResources(R.color.themeColor);
    }

    private void setActionBar() {
        initLightToolbar();

        String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        setTitle(userName);
    }

    private void setLockList() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.lockList.setLayoutManager(layoutManager);

        adapter = new DataBindingAdapter<>(this, R.layout.mine_item_lock_list, BR.userLock);
        adapter.setOnLongClickItemListener((selectedbinding, position, lastSelectedBinding) ->
                deleteKey(selectedbinding.getUserLock().keyId));
        binding.lockList.setAdapter(adapter);
    }

    public void deleteKey(long keyId) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_base, null);
        TextView textView = view.findViewById(R.id.content);
        textView.setText(getString(R.string.confirmDeleteKey));

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
                        dialog1.dismiss();
                        viewModel.deleteKey(keyId);
                    } else {
                    }
                })
                .create();

        dialog.show();
    }


    public static class Data {
        public String lockName;
        public String keyType;
        public long keyId;
        public String keyStatus;

        public Data(String lockName, long keyId, String keyType, String keyStatus) {
            this.lockName = lockName;
            this.keyType = keyType;
            this.keyId = keyId;
            this.keyStatus = keyStatus;
        }
    }
}
