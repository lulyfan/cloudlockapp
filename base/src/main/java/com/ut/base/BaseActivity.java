package com.ut.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.ut.base.UIUtils.StatusBarUtil;
import com.ut.base.Utils.TxtUtils;
import com.ut.base.Utils.Util;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setLightStatusBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(true).init();
    }

    public void setDarkStatusBar() {
        ImmersionBar.with(this)
                .statusBarDarkFont(false).init();
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

    public void showTitleMore() {
        findViewById(R.id.iv_more).setVisibility(View.VISIBLE);
    }

    public void setMoreClickListener(View.OnClickListener onClickListener) {
        findViewById(R.id.iv_more).setOnClickListener(onClickListener);
    }

    public void showTitleAdd() {
        ImageView imageView = findViewById(R.id.iv_more);
        imageView.setImageResource(R.mipmap.icon_add);
    }

    public void onXmlClick(View view) {
        if (view.getId() == R.id.iv_back) {
            this.finish();
        }
    }

    public void setTitle(int resId) {
        TextView textView = findViewById(R.id.tv_title);
        if (textView != null) {
            textView.setText(resId);
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
        enableImmersive();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // ToolBar左侧按键点击事件监听
                finish();
                break;
        }
        return true;
    }
}
