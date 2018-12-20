package com.ut.module_lock.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_lock.R;

public class SearchLockActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lock);
        initTitle();
    }

    private void initTitle() {
        enableImmersive(R.color.white, true);
    }

    @Override
    public void onXmlClick(View view) {
        super.onXmlClick(view);
        int i = view.getId();
        if (i == R.id.tv_cancel) {
            onBackPressed();
        }
    }
}
