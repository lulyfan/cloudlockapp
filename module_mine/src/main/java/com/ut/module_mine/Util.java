package com.ut.module_mine;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Util {

    public static int getWidthPxByDisplayPercent(Context context, double percent) {

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int width = dm.widthPixels;
        return (int) (width * percent);
    }

    public static int getHeightPxByDisplayPercent(Context context, double percent) {

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int height = dm.heightPixels;
        return (int) (height * percent);
    }
}
