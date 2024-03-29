package com.ut.cloudlock.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.ut.base.AppManager;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.FragmentUtil;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UserRepository;
import com.ut.base.Utils.UTLog;
import com.ut.cloudlock.R;
import com.ut.cloudlock.adapter.MainPageAdapter;
import com.ut.cloudlock.databinding.ActivityMainBinding;
import com.ut.commoncomponent.CLToast;


@SuppressLint("CheckResult")
@Route(path = RouterUtil.MainModulePath.Main_Module)
public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        enableImmersive();
        initViewPager();
        initPageChangeListener();
        initNavigationItemSelectListener();
        mBinding.bottomNavigation.setItemIconTintList(null);
        resetBottomIcon();
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_home).setIcon(R.mipmap.icon_home_pressed);

        UserRepository.getInstance().getUser().observe(this, user -> {
            BaseApplication.setUser(user);
            UTLog.d("observe", "user update ----> " + JSON.toJSONString(user));
        });
        UserRepository.getInstance().refreshUser();
        regBrocastReceiver();
    }

    private void initNavigationItemSelectListener() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                resetBottomIcon();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        menuItem.setIcon(R.mipmap.icon_home_pressed);
                        mBinding.vpMain.setCurrentItem(0);
                        enableImmersive();
                        break;
                    case R.id.action_msg:
                        menuItem.setIcon(R.mipmap.icon_msg_pressed);
                        mBinding.vpMain.setCurrentItem(1);
                        enableImmersive(R.color.white, true);
                        break;
                    case R.id.action_mall:
                        menuItem.setIcon(R.mipmap.icon_mall_pressed);
                        mBinding.vpMain.setCurrentItem(2);
                        enableImmersive(R.color.transparent, true);
                        break;
                    case R.id.action_mime:
                        menuItem.setIcon(R.mipmap.icon_mime_pressed);
                        mBinding.vpMain.setCurrentItem(3);
                        enableImmersive(R.color.white, true);
                        break;
                }
                return true;
            }
        });
    }

    private void resetBottomIcon() {
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_home).setIcon(R.mipmap.icon_home);
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_msg).setIcon(R.mipmap.icon_msg);
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_mall).setIcon(R.mipmap.icon_mall);
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_mime).setIcon(R.mipmap.icon_mime);
    }

    private void initPageChangeListener() {
        mBinding.vpMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mBinding.bottomNavigation.setSelectedItemId(getBottomNavigateIDByIndex(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private int getBottomNavigateIDByIndex(int index) {
        int id = R.id.action_home;

        switch (index) {
            case 1:
                id = R.id.action_msg;
                break;
            //TODO 隐藏商城页面
            case 2:
//                id = R.id.action_mall;
//                break;
//            case 3:
                id = R.id.action_mime;
                break;
        }
        return id;
    }

    private void initViewPager() {
        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mainPageAdapter.addFragment(FragmentUtil.getLockFragment());
        mainPageAdapter.addFragment(FragmentUtil.getMsgFragment());
        //TODO 隐藏商城页面
//        mainPageAdapter.addFragment(FragmentUtil.getMallFragment());
        mainPageAdapter.addFragment(FragmentUtil.getMineFragment());
        mBinding.vpMain.setAdapter(mainPageAdapter);
    }


    private long lastTime = 0;

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime < 2000) {
            super.onBackPressed();
        } else {
            lastTime = currentTime;
            CLToast.showAtBottom(this, getString(R.string.double_click_to_back));
//            UTLog.i("isLocation enable:"+SystemUtils.isLocationEnable(this));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegBrocastReceiver();
    }

    private void regBrocastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RouterUtil.BrocastReceiverAction.ACTION_FINISH_MAINACTIVITY);
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void unRegBrocastReceiver() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (RouterUtil.BrocastReceiverAction.ACTION_FINISH_MAINACTIVITY.equals(intent.getAction())
                    && !isFinishing()) {
                finish();
            }
        }
    };
}
