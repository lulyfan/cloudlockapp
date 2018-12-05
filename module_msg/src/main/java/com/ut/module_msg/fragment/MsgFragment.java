package com.ut.module_msg.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.module_msg.R;
import com.ut.module_msg.databinding.FragmentMsgBinding;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
@Route(path = RouterUtil.MsgModulePath.Fragment_MSG)
public class MsgFragment extends BaseFragment {
    View mView = null;
    FragmentMsgBinding mMsgBinding = null;

    TabLayout mTabLayout;

    ViewPager mViewPager;

    FragmentPagerAdapter mAdapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mMsgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_msg, container, false);
            mView = mMsgBinding.getRoot();
            initView();
        }
        return mView;
    }

    private void initView() {
        mTabLayout = mMsgBinding.tabsBar;
        mViewPager = mMsgBinding.viewpager;

        String[] titles = new String[]{getString(R.string.msg_notification), getString(R.string.msg_apply)};
        mTabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        final Fragment[] fragments = {new NotificationFragment(), new ApplyFragment()};
        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragments[i];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }
        };

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
    }
}
