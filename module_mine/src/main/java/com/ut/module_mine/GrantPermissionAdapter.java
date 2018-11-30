package com.ut.module_mine;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ut.module_mine.fragment.ForeverFragment;
import com.ut.module_mine.fragment.LimitTimeFragment;
import com.ut.module_mine.fragment.LoopFragment;
import com.ut.module_mine.fragment.OnceFragment;

public class GrantPermissionAdapter extends FragmentPagerAdapter {

    public GrantPermissionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        switch (i) {
            case 0:
                fragment = new ForeverFragment();
                break;

            case 1:
                fragment = new LimitTimeFragment();
                break;

            case 2:
                fragment = new OnceFragment();
                break;

            case 3:
                fragment = new LoopFragment();
                break;
        }
        return fragment;
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
