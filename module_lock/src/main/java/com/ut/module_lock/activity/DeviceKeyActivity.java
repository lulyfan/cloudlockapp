package com.ut.module_lock.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.module_lock.R;
import com.ut.module_lock.fragment.DeviceKeyFragment;

import java.util.ArrayList;
import java.util.List;


@Route(path = RouterUtil.LockModulePath.LOCK_DEVICE_KEY)
public class DeviceKeyActivity extends BaseActivity {
    private DevicePagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_key);

        enableImmersive();
        initToolbar();
        initView();
    }

    private void initView() {
        mSectionsPagerAdapter = new DevicePagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.titleBar);
        TextView textView = findViewById(R.id.tv_title);
        textView.setText(R.string.func_key_device);
        ViewParent parent = toolbar.getParent();
        if (parent instanceof View) {
            ((View) parent).setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setHomeButtonEnabled(true); //设置返回键可用
            actionBar.setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        }
    }


    public class DevicePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = null;

        public DevicePagerAdapter(FragmentManager fm) {
            super(fm);
            initFragment();
        }

        private void initFragment() {
            mFragments = new ArrayList<>();
            mFragments.add(DeviceKeyFragment.newInstance(DeviceKeyFragment.KEY_TYPE_FINGER));
            mFragments.add(DeviceKeyFragment.newInstance(DeviceKeyFragment.KEY_TYPE_PWD));
            mFragments.add(DeviceKeyFragment.newInstance(DeviceKeyFragment.KEY_TYPE_IC));
            mFragments.add(DeviceKeyFragment.newInstance(DeviceKeyFragment.KEY_TYPE_ELEC_KEY));
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }


    }
}
