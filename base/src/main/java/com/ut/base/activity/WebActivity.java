package com.ut.base.activity;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.R;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.databinding.ActivityWebBinding;

/**
 * author : chenjiajun
 * time   : 2019/1/11
 * desc   :
 */

@Route(path = RouterUtil.BaseModulePath.WEB)
public class WebActivity extends BaseActivity {
    private ActivityWebBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        initLightToolbar();
        String url = getIntent().getStringExtra("load_url");
        if (TextUtils.isEmpty(url)) {
            finish();
        }
        binding.webView.loadUrl(url);
        binding.refreshLayout.setOnRefreshListener(() -> {
            showErrorTips(false);
            binding.webView.reload();
            binding.refreshLayout.postDelayed(() -> {
                binding.webView.reload();
                binding.refreshLayout.setRefreshing(false);
            }, 1200L);
        });
        webSettings();
    }

    private void webSettings() {
        WebSettings settings = binding.webView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        WebViewClient client = new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                showErrorTips(false);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                showErrorTips(true);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };

        binding.webView.setWebViewClient(client);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.webView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (binding.webView.getScrollY() == 0) {
                    binding.refreshLayout.setEnabled(true);
                } else {
                    binding.refreshLayout.setEnabled(false);
                }
            });
        }
    }

    private void showErrorTips(boolean isShow) {
        binding.noNetworkLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }
}
