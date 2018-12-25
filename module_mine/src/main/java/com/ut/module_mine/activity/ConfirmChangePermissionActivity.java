package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.ut.base.BaseActivity;
import com.ut.base.customView.CheckCodeView;
import com.ut.module_mine.GlobalData;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivityConfirmChangePermissionBinding;
import com.ut.module_mine.viewModel.ConfirmChangePermissionViewModel;

import java.util.concurrent.Executors;

public class ConfirmChangePermissionActivity extends BaseActivity {

    private ActivityConfirmChangePermissionBinding binding;
    private ConfirmChangePermissionViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_confirm_change_permission);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(ConfirmChangePermissionViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.tip.observe(this, s -> toastShort(s));
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.getUserInfoByMobile();
        sendVerifyCode();
    }

    private void initUI() {
        initLightToolbar();

        binding.checkCodeView.setInputListener(new CheckCodeView.InputListener() {
            @Override
            public void onInput(String checkCOde) {
                binding.confirm.setEnabled(false);
            }

            @Override
            public void onFinish(String checkCode) {
                binding.confirm.setEnabled(true);
            }
        });

        binding.confirm.setOnClickListener(v -> {
                String verifyCode = binding.checkCodeView.getInput();
                viewModel.changeLockAdmin(verifyCode);
        });

        binding.timer.setOnClickListener(v -> sendVerifyCode());
    }

    private void sendVerifyCode() {
        viewModel.getChangeAdminCode();
        viewModel.startCount(binding.timer);
    }

}
