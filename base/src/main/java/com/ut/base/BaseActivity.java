package com.ut.base;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import com.ut.base.UIUtils.StatusBarUtil;

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
        StatusBarUtil.setStatusTextColor(true, this);
    }

    public void setDarkStatusBar() {
        StatusBarUtil.setStatusTextColor(false, this);
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
}
