package com.ut.module_mine.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.AutoOpenLockService;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.base.VersionUpdateHelper;
import com.ut.module_mine.R;
import com.ut.module_mine.databinding.ActivitySystemSettingBinding;
import com.ut.module_mine.viewModel.SystemSettingViewModel;

public class SystemSettingActivity extends BaseActivity {

    private ActivitySystemSettingBinding binding;
    private SystemSettingViewModel viewModel;
    private AutoOpenLockService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_system_setting);
        initUI();
        initViewModel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AutoOpenLockService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((AutoOpenLockService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SystemSettingViewModel.class);
        viewModel.tip.observe(this, s -> toastShort(s));
        viewModel.logoutSuccess.observe(this, aVoid -> {
            if (mService != null) {
                mService.stopAutoOpenLock();
            }
        });
    }

    private void initUI() {
        initLightToolbar();
        setTitle(getString(R.string.systemSetting));

        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.checkVersion.setOnClickListener(v -> {
            try {
                VersionUpdateHelper.updateVersion(this, getPackageManager().getPackageInfo(getPackageName(), 0).versionCode, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.userRule.setOnClickListener((v) -> {
            ARouter.getInstance().build(RouterUtil.BaseModulePath.WEB)
                    .withString("load_url", "https://smarthome.zhunilink.com/realtimeadmin/api/buss/executeBackString?scriptName=cloudlockinfo")
                    .navigation();
        });

        binding.aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(SystemSettingActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        binding.resetPW.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.LoginModulePath.FORGET_PWD)
                    .withAction(RouterUtil.LoginModuleAction.action_login_resetPW)
                    .withString("phone", BaseApplication.getUser().getAccount())
                    .navigation();
        });

        binding.logout.setOnClickListener(v -> {
            logout();
        });

        binding.privateRule.setOnClickListener(v -> {
            ARouter.getInstance().build(RouterUtil.BaseModulePath.WEB)
                    .withString("load_url", "https://smarthome.zhunilink.com/realtimeadmin/api/buss/executeBackString?scriptName=cloudlockprivate")
                    .navigation();
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
                .setContentBackgroundResource(R.drawable.mine_bg_dialog)
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
