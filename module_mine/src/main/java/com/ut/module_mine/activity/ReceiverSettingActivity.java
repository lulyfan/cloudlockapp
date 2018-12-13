package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityReceiverSettingBinding;
import com.ut.module_mine.viewModel.ReceiverSettingViewModel;

public class ReceiverSettingActivity extends BaseActivity {

    private ActivityReceiverSettingBinding binding;
    private ReceiverSettingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_receiver_setting);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ReceiverSettingViewModel.class);
        viewModel.isInputPhone.observe(this, b -> binding.nextStep.setEnabled(b));

        binding.setViewmodel(viewModel);
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.receiverSetting));

        binding.nextStep.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiverSettingActivity.this, ConfirmChangePermissionActivity.class);
            startActivity(intent);
        });
    }
}
