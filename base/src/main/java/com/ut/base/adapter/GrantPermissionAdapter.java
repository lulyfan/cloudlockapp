package com.ut.base.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ut.base.BaseApplication;
import com.ut.base.BaseFragment;
import com.ut.base.R;
import com.ut.base.fragment.ForeverFragment;
import com.ut.base.fragment.LimitTimeFragment;
import com.ut.base.fragment.LoopFragment;
import com.ut.base.fragment.OnceFragment;

import java.util.ArrayList;
import java.util.List;

public class GrantPermissionAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragments = null;

    public GrantPermissionAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new ForeverFragment());
        fragments.add(new LimitTimeFragment());
        fragments.add(new OnceFragment());
        fragments.add(new LoopFragment());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {//todo 中文
            case 0:
                return BaseApplication.getAppContext().getString(R.string.mine_forever);

            case 1:
                return BaseApplication.getAppContext().getString(R.string.mine_limitTime);

            case 2:
                return BaseApplication.getAppContext().getString(R.string.mine_once);

            case 3:
                return BaseApplication.getAppContext().getString(R.string.mine_loop);

            default:
                return "";
        }
    }
}
