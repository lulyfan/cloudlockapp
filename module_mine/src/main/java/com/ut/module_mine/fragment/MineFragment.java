package com.ut.module_mine.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.BaseActivity;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.module_mine.activity.ChangeLockPermissionActivity;
import com.ut.module_mine.activity.EditUserInfoActivity;
import com.ut.module_mine.activity.LockGroupActivity;
import com.ut.module_mine.activity.LockUserActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.activity.SystemSettingActivity;
import com.ut.module_mine.databinding.*;
import com.ut.module_mine.viewModel.MineViewModel;


/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
@Route(path = RouterUtil.MineModulePath.Fragment_Mine)
public class MineFragment extends BaseFragment {

    View mView = null;
    FragmentMineBinding mMineBinding = null;
    MineViewModel mineViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mMineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
            mView = mMineBinding.getRoot();
            mMineBinding.setViewModel(mineViewModel);
        }
        initUI();
        return mView;
    }

    private void initViewModel() {
        mineViewModel = ViewModelProviders.of(this).get(MineViewModel.class);

        mineViewModel.tip.observe(this, s -> {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.endLoad();
            activity.toastShort(s);
        });

        mineViewModel.isWebLoginEnable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ((BaseActivity)getActivity()).startLoad();
            }
        });

        mineViewModel.isOpenLockVolumeEnable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ((BaseActivity)getActivity()).startLoad();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initUI() {
        mMineBinding.lockGroup.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), LockGroupActivity.class);
            startActivity(intent);
        });

        mMineBinding.editUesrInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditUserInfoActivity.class);
            startActivity(intent);
        });

        mMineBinding.lockUserManage.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LockUserActivity.class);
            startActivity(intent);
        });

        mMineBinding.changeLockPermission.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ChangeLockPermissionActivity.class);
            startActivity(intent);
        });

        mMineBinding.systemSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SystemSettingActivity.class);
            startActivity(intent);
        });

        mMineBinding.customService.setOnClickListener(v -> showCustomServiceDialog());
    }

    @Override
    protected void onUserVisible() {
        super.onUserVisible();
        if(mineViewModel != null) {
            mineViewModel.getUserInfo();
        }
    }

    private void showCustomServiceDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_base, null);
        TextView textView = view.findViewById(R.id.content);
        textView.setText(getString(R.string.isCallToCustomService));

        DialogPlus dialog = DialogPlus.newDialog(getContext())
                .setContentHolder(new ViewHolder(view))
                .setGravity(Gravity.CENTER)
                .setContentWidth(Util.getWidthPxByDisplayPercent(getContext(), 0.8))
                .setContentBackgroundResource(R.drawable.mine_bg_dialog)
                .setOnClickListener((dialog1, view1) -> {
                    int i = view1.getId();
                    if (i == R.id.cancel) {
                        dialog1.dismiss();

                    } else if (i == R.id.confirm) {
                        dialog1.dismiss();
                        dialPhoneNumber("18125067770");
                    } else {
                    }
                })
                .create();

        dialog.show();
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
