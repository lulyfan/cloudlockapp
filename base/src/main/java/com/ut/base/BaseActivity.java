package com.ut.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.ut.base.Utils.Util;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class BaseActivity extends AppCompatActivity {

    private OnCustomerClickListener moreListener = null;
    private OnCustomerClickListener addListener = null;
    private OnCustomerClickListener checkAllListener = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setLightStatusBar() {
        ImmersionBar.with(this)
                .reset()
                .statusBarDarkFont(true)
                .init();
    }

    public void setDarkStatusBar() {
        ImmersionBar.with(this)
                .reset()
                .statusBarDarkFont(false)
                .init();
    }

    /**
     * 设置沉浸式状态栏
     *
     * @param stateBarColor             状态栏颜色, 为0时状态栏颜色不改变
     * @param isEnableStatusBarDarkFont 是否启用深色字体，默认浅色
     */
    public void enableImmersive(int stateBarColor, boolean isEnableStatusBarDarkFont) {
        ImmersionBar immersionBar = ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarDarkFont(isEnableStatusBarDarkFont);

        if (stateBarColor != 0) {
            immersionBar.statusBarColor(stateBarColor);
        }

        immersionBar.init();
    }

    /**
     * 设置沉浸式状态栏，布局内容会上移到状态栏（设置状态栏为图片背景时用）
     */
    public void enableImmersive() {
        ImmersionBar.with(this).reset().init();
    }

    public void onXmlClick(View view) {
    }

    public void setTitle(int resId) {
        TextView textView = findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(resId);
        }
    }

    public void setTitle(CharSequence charSequence) {
        TextView textView = findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void initLightToolbar() {
        setLightStatusBar();
        initToolbar();
    }

    public void initDarkToolbar() {
        setDarkStatusBar();
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        ViewParent parent = toolbar.getParent();
        if (parent instanceof View) {
            ((View) parent).setPadding(0, Util.getStatusBarHeight(this), 0, 0);
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(null);
            actionBar.setHomeButtonEnabled(true); //设置返回键可用
            actionBar.setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        }
    }

    public interface OnCustomerClickListener {
        void onClick();
    }

    public void initMore(OnCustomerClickListener listener) {
        this.moreListener = listener;
    }

    public void initAdd(OnCustomerClickListener listener) {
        this.addListener = listener;
    }

    public void initCheckAll(OnCustomerClickListener listener) {
        this.checkAllListener = listener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_more_menu, menu);
        menu.findItem(R.id.more).setVisible(moreListener != null);
        menu.findItem(R.id.add).setVisible(addListener != null);
        menu.findItem(R.id.checkAll).setVisible(checkAllListener != null);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {// ToolBar左侧按键点击事件监听
            onBackPressed();
        } else if (i == R.id.more && moreListener != null) {//更多图示点击事件
            moreListener.onClick();
        } else if (i == R.id.add && addListener != null) {
            addListener.onClick();
        } else if (i == R.id.checkAll && checkAllListener != null) {
            checkAllListener.onClick();
        }
        return true;
    }
}
