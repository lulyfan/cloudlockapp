package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.fragment.DeviceKeyListFragment;
import com.ut.module_lock.viewmodel.DeviceKeyVM;

import java.util.ArrayList;
import java.util.List;


@Route(path = RouterUtil.LockModulePath.LOCK_DEVICE_KEY)
public class DeviceKeyActivity extends BaseActivity {
    private DevicePagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private LockKey mLockKey = null;
    private DeviceKeyVM mDeviceKeyVM = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_key);
        enableImmersive();
        initToolbar();
        initData();
        initVM();
        initView();
    }

    private void initVM() {
        mDeviceKeyVM = ViewModelProviders.of(this).get(DeviceKeyVM.class);
        mDeviceKeyVM.setLockKey(mLockKey);
        mDeviceKeyVM.getDevieKey();
        mDeviceKeyVM.getShowTip().observe(this, tip -> {

        });
    }

    private void initData() {
        mLockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
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

    @Override
    public void onXmlClick(View view) {
        int i = view.getId();
        if (i == R.id.tv_connectLock) {
            mDeviceKeyVM.connectAndGetData(mLockKey.getType(), this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mDeviceKeyVM.mBleOperateManager.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mDeviceKeyVM.mBleOperateManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    public class DevicePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = null;

        public DevicePagerAdapter(FragmentManager fm) {
            super(fm);
            initFragment();
        }

        private void initFragment() {
            mFragments = new ArrayList<>();
            mFragments.add(DeviceKeyListFragment.newInstance(EnumCollection.DeviceKeyType.FINGERPRINT.ordinal()));
            mFragments.add(DeviceKeyListFragment.newInstance(EnumCollection.DeviceKeyType.PASSWORD.ordinal()));
            mFragments.add(DeviceKeyListFragment.newInstance(EnumCollection.DeviceKeyType.ICCARD.ordinal()));
            mFragments.add(DeviceKeyListFragment.newInstance(EnumCollection.DeviceKeyType.ELECTRONICKEY.ordinal()));
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
