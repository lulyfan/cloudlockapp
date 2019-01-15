package com.ut.base.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ut.base.BaseFragment;
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
        switch (position) {
            case 0:
                return "永久";

            case 1:
                return "限时";

            case 2:
                return "单次";

            case 3:
                return "循环";

            default:
                return "";
        }
    }
}
