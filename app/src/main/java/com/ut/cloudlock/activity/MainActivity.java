package com.ut.cloudlock.activity;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.UIUtils.FragmentUtil;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UserRepository;
import com.ut.cloudlock.R;
import com.ut.cloudlock.adapter.MainPageAdapter;
import com.ut.cloudlock.databinding.ActivityMainBinding;
import com.ut.database.database.CloudLockDatabaseHolder;

import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;


@SuppressLint("CheckResult")
@Route(path = RouterUtil.MainModulePath.Main_Module)
public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        enableImmersive();
        initViewPager();
        initPageChangeListener();
        initNavigationItemSelectListener();
        mBinding.bottomNavigation.setItemIconTintList(null);
        resetBottomIcon();
        mBinding.bottomNavigation.getMenu().findItem(R.id.action_home).setIcon(R.mipmap.icon_home_pressed);

        mBinding.fab.setOnClickListener(v -> {
            Flowable.just(this).subscribeOn(Schedulers.io()).subscribe(context -> {
                CloudLockDatabaseHolder.get().getUUIDDao().deleteUUID();
                CloudLockDatabaseHolder.get().getUserDao().deleteAllUsers();
            });
        });

        UserRepository.getInstance().getUser().observe(this, user -> {
            BaseApplication.setUser(user);
            Log.d("observe", "user update ----> " + JSON.toJSONString(user));
        });

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
            case 2:
                id = R.id.action_mall;
                break;
            case 3:
                id = R.id.action_mime;
                break;
        }
        return id;
    }

    private void initViewPager() {
        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mainPageAdapter.addFragment(FragmentUtil.getLockFragment());
        mainPageAdapter.addFragment(FragmentUtil.getMsgFragment());
        mainPageAdapter.addFragment(FragmentUtil.getMallFragment());
        mainPageAdapter.addFragment(FragmentUtil.getMineFragment());
        mBinding.vpMain.setAdapter(mainPageAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        UserRepository.getInstance().refreshUser();
    }
}
