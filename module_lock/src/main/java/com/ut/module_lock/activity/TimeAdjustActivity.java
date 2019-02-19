package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityTimeAdjustBinding;
import com.ut.module_lock.viewmodel.TimeAdjustVM;
import com.ut.unilink.UnilinkManager;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

@Route(path = RouterUtil.LockModulePath.TIME_ADJUST)
public class TimeAdjustActivity extends BaseActivity {

    private TimeAdjustVM viewModel;
    private ActivityTimeAdjustBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initViewModel();
    }

    private void initUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_adjust);

        initLightToolbar();
        setTitle(getString(R.string.lockTime));

        binding.time.setVisibility(View.GONE);
        binding.date.setVisibility(View.GONE);
        binding.tip.setVisibility(View.GONE);
        binding.button.setOnClickListener(v -> {
            viewModel.adjustTime();
        });
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(TimeAdjustVM.class);
        LockKey lockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
        viewModel.setLockKey(lockKey);
        viewModel.tip.observe(this, s -> toastShort(s));
        viewModel.getShowLockResetDialog().observe(this, isShow -> {
            if (isShow) {
                new CustomerAlertDialog(TimeAdjustActivity.this, false)
                        .setMsg(getString(R.string.lock_detail_dialog_msg_reset))
                        .hideCancel()
                        .show();
            }
        });

        viewModel.lockTime.observe(this, aLong -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            String dateTime = dateFormat.format(new Date(aLong));
            String[] temp = dateTime.split(" ");
            String date = temp[0];
            String time = temp[1];
            binding.date.setText(date);
            binding.time.setText(time);
            binding.time.setTextSize(50);

            binding.tip.setText(getString(R.string.timeAdjustTip));
            binding.button.setText(getString(R.string.timeAdjust));

            binding.time.setVisibility(View.VISIBLE);
            binding.date.setVisibility(View.VISIBLE);
            binding.tip.setVisibility(View.VISIBLE);
        });

        viewModel.state.observe(this, integer -> {
            switch (integer) {
                case TimeAdjustVM.STATE_DEFAULT:
                    endLoad();
                    break;

                case TimeAdjustVM.STATE_FAILED:
                    endLoad();
                    binding.time.setText(getString(R.string.getLockTimeFailed));
                    binding.time.setTextSize(25);
                    binding.tip.setText(getString(R.string.checkConnect));
                    binding.button.setText(getString(R.string.retry));

                    binding.time.setVisibility(View.VISIBLE);
                    binding.tip.setVisibility(View.VISIBLE);
                    binding.date.setVisibility(View.GONE);
                    break;

                case TimeAdjustVM.STATE_SCAN:
                    startLoad(getString(R.string.scaning));
                    break;

                case TimeAdjustVM.STATE_CONNECT:
                    setLoadText(getString(R.string.connecting));
                    break;

                case TimeAdjustVM.STATE_READ_TIME:
                    setLoadText(getString(R.string.readingTime));
                    break;

                case TimeAdjustVM.STATE_WRITE_TIME:
                    setLoadText(getString(R.string.writingTime));
                    break;

                default:
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        UnilinkManager.getInstance(this).enableBluetooth(this, 0);
        UnilinkManager.getInstance(this).requestPermission(this, 1);
        viewModel.readLockTime();
    }
}
