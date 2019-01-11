package com.ut.base.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWebBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        initLightToolbar();
        String url = getIntent().getStringExtra("load_url");
        if (TextUtils.isEmpty(url)) {
            finish();
        }
        binding.webView.loadUrl(url);
    }
}
