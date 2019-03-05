package com.ut.base.common;

import android.view.View;
import android.widget.AdapterView;

/**
 * author : zhouyubin
 * time   : 2019/03/04
 * desc   :
 * version: 1.0
 */
public abstract class NonDoubleOnItemClickListener implements AdapterView.OnItemClickListener {
    private long lastClickTime = 0L;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime >= 800) {
            onNonDoubleClick(parent, view, position, id);
            lastClickTime = currentTime;
        }
    }

    abstract public void onNonDoubleClick(AdapterView<?> parent, View view, int position, long id);
}
