package com.ut.cloudlock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.FragmentUtil;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.cloudlock.R;
import com.ut.cloudlock.adapter.MainPageAdapter;
import com.ut.cloudlock.databinding.ActivityMainBinding;

@Route(path = RouterUtil.MainModulePath.Main_Module)
public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initViewPager();
        initPageChangeListener();
        initNavigationItemSelectListener();

    }

    private void initNavigationItemSelectListener() {
        mBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        mBinding.vpMain.setCurrentItem(0);
                        break;
                    case R.id.action_msg:
                        mBinding.vpMain.setCurrentItem(1);
                        break;
                    case R.id.action_mall:
                        mBinding.vpMain.setCurrentItem(2);
                        break;
                    case R.id.action_mime:
                        mBinding.vpMain.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
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

    public class Present {

    }

}
