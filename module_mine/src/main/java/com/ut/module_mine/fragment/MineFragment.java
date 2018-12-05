package com.ut.module_mine.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_mine.activity.ChangeLockPermissionActivity;
import com.ut.module_mine.activity.EditUserInfoActivity;
import com.ut.module_mine.activity.LockGroupActivity;
import com.ut.module_mine.activity.LockUserActivity;
import com.ut.module_mine.R;
import com.ut.module_mine.activity.SystemSettingActivity;
import com.ut.module_mine.databinding.*;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mMineBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
            mView = mMineBinding.getRoot();
        }
        initUI();
        return mView;
    }


    private void initUI() {
        mMineBinding.lockGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LockGroupActivity.class);
                startActivity(intent);
            }
        });

        mMineBinding.editUesrInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditUserInfoActivity.class);
                startActivity(intent);
            }
        });

        mMineBinding.lockUserManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LockUserActivity.class);
                startActivity(intent);
            }
        });

        mMineBinding.changeLockPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeLockPermissionActivity.class);
                startActivity(intent);
            }
        });

        mMineBinding.systemSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SystemSettingActivity.class);
                startActivity(intent);
            }
        });
    }


}
