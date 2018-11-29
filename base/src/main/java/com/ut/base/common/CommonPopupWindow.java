package com.ut.base.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.reflect.Field;

/**
 * author : zhouyubin
 * time   : 2018/11/29
 * desc   :
 * version: 1.0
 */
public abstract class CommonPopupWindow {
    private Context mContext;
    private View mContentView;
    private PopupWindow mPopupWindow;

    public CommonPopupWindow(Context context, int layoutResId, int w, int h) {
        mContext = context;
        mContentView = LayoutInflater.from(context).inflate(layoutResId, null, false);
        mPopupWindow = new PopupWindow(mContentView, w, h, true);
        initWindow();
        initView();
    }

    protected abstract void initView();

    protected void initWindow() {
        fitPopupWindowOverStatusBar(true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setTouchable(true);
    }

    public void showAtLocationWithAnim(View parent, int gravity, int x, int y, int animStyleId) {
        mPopupWindow.setAnimationStyle(animStyleId);
        mPopupWindow.showAtLocation(parent, gravity, x, y);

    }

    public PopupWindow getPopupWindow() {
        return mPopupWindow;
    }

    public <T extends View> T getView(int viewId) {
        View view = mContentView.findViewById(viewId);
        return (T) view;
    }

    public void fitPopupWindowOverStatusBar(boolean needFullScreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Field mLayoutInScreen = PopupWindow.class.getDeclaredField("mLayoutInScreen");
                mLayoutInScreen.setAccessible(true);
                mLayoutInScreen.set(mPopupWindow, needFullScreen);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
