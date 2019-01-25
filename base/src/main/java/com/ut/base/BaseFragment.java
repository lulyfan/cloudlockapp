package com.ut.base;


import android.content.Context;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mobstat.StatService;
import com.example.operation.MyRetrofit;
import com.example.operation.WebSocketHelper;
import com.gyf.barlibrary.ImmersionBar;
import com.ut.base.Utils.UTLog;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
public class BaseFragment extends Fragment {

    public void setLightStatusBarFont() {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.setLightStatusBar();
        }
    }

    public void setDarkStatusBarFont() {
        if (getActivity() instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) getActivity();
            activity.setDarkStatusBar();
        }
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        UTLog.i(this.getClass().getSimpleName() + ":onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UTLog.i(this.getClass().getSimpleName() + ":onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        UTLog.i(this.getClass().getSimpleName() + ":onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        UTLog.i(this.getClass().getSimpleName() + ":onActivityCreated");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        UTLog.i(this.getClass().getSimpleName() + ":onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        UTLog.i(this.getClass().getSimpleName() + ":onResume");
        onUserVisible();
        StatService.onPageStart(getActivity(), this.getClass().getSimpleName());

        MyRetrofit.get().setWebSocketStateListener(() -> onWebSocketOpened());
    }

    protected void onWebSocketOpened() {

    }

    @Override
    public void onPause() {
        super.onPause();
        UTLog.i(this.getClass().getSimpleName() + ":onPause");
        onUserInvisible();
        StatService.onPageEnd(getActivity(), this.getClass().getSimpleName());

        MyRetrofit.get().setWebSocketStateListener(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        UTLog.i(this.getClass().getSimpleName() + ":onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        UTLog.i(this.getClass().getSimpleName() + ":onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UTLog.i(this.getClass().getSimpleName() + ":onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UTLog.i(this.getClass().getSimpleName() + ":onDetach");
    }

    /**
     * 用于 viewpager + fragment， 切换时生效
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onUserVisible();
        } else {
            onUserInvisible();
        }
    }

    /**
     * 用户fragment hide() or show()
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (hidden) {
            onUserInvisible();
        } else {
            onUserVisible();
        }
    }


    protected void onUserVisible() {

    }

    protected void onUserInvisible() {

    }

    private ViewDataBinding binding = null;

    public ViewDataBinding getBinding() {
        return binding;
    }
}
