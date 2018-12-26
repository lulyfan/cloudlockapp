package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivitySystemSettingBinding;
import com.ut.module_mine.viewModel.SystemSettingViewModel;

public class SystemSettingActivity extends BaseActivity {

    private ActivitySystemSettingBinding binding;
    private SystemSettingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_system_setting);
        initUI();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SystemSettingViewModel.class);
        viewModel.tip.observe(this, s -> toastShort(s));
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.systemSetting));

        binding.aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(SystemSettingActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        binding.resetPW.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LoginModulePath.FORGET_PWD)
                    .withAction(RouterUtil.LoginModuleAction.action_login_resetPW)
                    .navigation();
        });

        binding.logout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_base, null);
        TextView textView = view.findViewById(R.id.content);
        textView.setText(getString(R.string.isLogout));

        DialogPlus dialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setContentBackgroundResource(R.drawable.bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {
                        dialog1.dismiss();
                        viewModel.logout();
                    } else {
                    }
                })
                .create();

        dialog.show();
    }
}
