package com.ut.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.StatService;
import com.example.operation.MyRetrofit;
import com.example.operation.WebSocketHelper;
import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.Util;
import com.ut.base.dialog.DialogHelper;
import com.ut.unilink.UnilinkManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


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
    private static AlertDialog noLoginDialog = null;
    private DialogPlus loadDialog;
    private WebSocketHelper.WebSocketStateListener webSocketStateListener;
    private AutoOpenLockService autoOpenLockService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppManager.getAppManager().addActivity(this);
        initNoLoginListener();
        initLoadDialog();

        if (BuildConfig.DEBUG)
            UnilinkManager.getInstance(this).enableLog(true);

        webSocketStateListener = () -> onWebSocketOpened();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AutoOpenLockService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, this.getClass().getSimpleName());

        MyRetrofit.get().addWebSocketStateListener(webSocketStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
    }

    public AutoOpenLockService getAutoOpenLockService() {
        return autoOpenLockService;
    }

    protected void onWebSocketOpened() {

    }

    protected void initNoLoginListener() {
        MyRetrofit.get().setNoLoginListener(() -> AndroidSchedulers.mainThread().scheduleDirect(()-> handlerNotLogin(RouterUtil.LoginModulePath.Login)));
    }

    private synchronized void handlerNotLogin(String url) {
        //TODO 暂时在这个地方删除所有数据库内容，后期加个判断是否换账号
        Schedulers.io().scheduleDirect(BaseApplication::clearDataWhenLogout);
        //todo
        try {
            DialogHelper.getInstance(AppManager.getAppManager().currentActivity())
                    .setMessage(getString(R.string.base_auto_login_time_out))
                    .setPositiveButton(getString(R.string.fine), (dialog1, which) -> ARouter.getInstance().build(url).navigation())
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (autoOpenLockService != null) {
            autoOpenLockService.stopAutoOpenLock();
        }
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

    public void hideNavigationIcon() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(null);
    }

    public void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);
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

    protected Toolbar getToolBar() {
        return findViewById(R.id.toolbar);
    }

    public void toastShort(String msg) {
        if (msg == null || "".equals(msg) || "未登录".equals(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void toastLong(String msg) {
        if (msg == null || "".equals(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (noLoginDialog != null && noLoginDialog.isShowing()) {
            noLoginDialog.dismiss();
        }
        StatService.onPageEnd(this, this.getClass().getSimpleName());

        MyRetrofit.get().removeWebSocketStateListener(webSocketStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (noLoginDialog != null) {
            noLoginDialog.dismiss();
        }
        AppManager.getAppManager().finishActivity(this);
    }

    public void startLoad(String msg) {
        if (loadDialog.isShowing()) {
            loadDialog.dismiss();
        }

        TextView textView = (TextView) loadDialog.findViewById(R.id.tip);
        textView.setText(msg);

        if (msg == null || "".equals(msg)) {
            textView.setVisibility(View.GONE);
        }

        loadDialog.show();
    }

    public void changeLoadText(String text) {
        TextView textView = (TextView) loadDialog.findViewById(R.id.tip);
        textView.setText("正在加载.. " + text + "%");

        if (text == null || "".equals(text)) {
            textView.setVisibility(View.GONE);
        }
    }

    public void setLoadText(String text) {
        TextView textView = (TextView) loadDialog.findViewById(R.id.tip);
        textView.setText(text);
    }

    public void startLoad() {
        //TODO 中文
        startLoad("请稍候...");
    }

    public void startProcess() {
        startLoad("正在加载.. 5%");
    }

    public void endLoad() {
        runOnUiThread(() -> {
            if (loadDialog.isShowing()) {
                loadDialog.dismiss();
            }
        });

    }

    public void initLoadDialog() {
        int padding = Util.dip2px(this, 16);
        int padding_24 = Util.dip2px(this, 24);

        View view = LayoutInflater.from(this).inflate(R.layout.load, null);
        loadDialog = DialogPlus.newDialog(this)
                .setCancelable(true)
                .setContentHolder(new ViewHolder(view))
                .setPadding(padding, padding_24, 0, padding_24)
                .setMargin(padding, 0, padding, 0)
                .setContentWidth(Util.getWidthPxByDisplayPercent(this, 0.8))
                .setGravity(Gravity.CENTER)
                .create();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            autoOpenLockService = ((AutoOpenLockService.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            autoOpenLockService = null;
        }
    };

}
