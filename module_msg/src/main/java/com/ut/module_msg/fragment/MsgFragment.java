package com.ut.module_msg.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
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
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.module_msg.R;
import com.ut.module_msg.databinding.FragmentMsgBinding;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

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
    private Badge tab1Badge, tab2Badge;

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

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            }
        };
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);

        for (int i =0; i < titles.length; i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if(tab != null) {
                View view = View.inflate(getContext(), R.layout.view_msg_tab, null);
                TextView textView = view.findViewById(R.id.textview);
                textView.setText(titles[i]);
                Badge badge = new QBadgeView(getContext());
                badge.bindTarget(textView).setBadgeBackgroundColor(Color.parseColor("#F55D54"))
                        .setGravityOffset(0,1, true)
                        .setBadgeTextColor(Color.WHITE)
                        .setShowShadow(false)
                        .setBadgeTextSize(8, true)
                        .setBadgeNumber(1);
                if(i == 0) {
                    tab1Badge = badge;
                } else {
                    tab2Badge = badge;
                }
                tab.setCustomView(view);
            }
        }
    }
}
