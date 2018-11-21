package com.ut.cloudlock.utils;

import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.FrameLayout;

import com.ut.commoncomponent.BadgeView;

public class BottomNavigationViewUtil {
    //反射调用去掉底部导航缩小
//    @SuppressLint("RestrictedApi")
//    public static void disableShiftMode(BottomNavigationView view) {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//        try {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            for (int i = 0; i < menuView.getChildCount(); i++) {
//                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                item.setShifting(false);
//                item.setChecked(item.getItemData().isChecked());
//            }
//        } catch (NoSuchFieldException e) {
//            UTLog.e("BottomNavigationViewUtil", "Unable to get shift mode field");
//        } catch (IllegalAccessException e) {
//            UTLog.e("BottomNavigationViewUtil", "Unable to change value of shift mode");
//        }
//    }

    public static void addBadgeView(BottomNavigationView view, int index, int count) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        int itemCount = menuView.getChildCount();
        if (index < itemCount) {
            View itemView = menuView.getChildAt(index);
            BadgeView badgeView = new BadgeView(view.getContext());
            badgeView.setTargetView(itemView);
            badgeView.setBadgeCount(count);
        }
    }

    public static void removeBadgeView(BottomNavigationView view, int index) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        int itemCount = menuView.getChildCount();
        if (index < itemCount) {
            View itemView = menuView.getChildAt(index);
            if (itemView instanceof FrameLayout) {
                for (int i = 0; i < ((FrameLayout) itemView).getChildCount(); i++) {
                    View view1 = ((FrameLayout) itemView).getChildAt(i);
                    if (view1 instanceof BadgeView) {
                        ((FrameLayout) itemView).removeView(view1);
                    }
                }
            }
        }
    }
}