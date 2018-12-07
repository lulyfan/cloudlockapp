package com.ut.module_lock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ut.base.BaseActivity;
import com.ut.module_lock.R;

public class AddGuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_guide);
        setTitle(R.string.lock_title_add_guide);
        initLightToolbar();
    }

    @Override
    public void onXmlClick(View view) {
        super.onXmlClick(view);
        int i = view.getId();
        if (i == R.id.btn_next) {
            startActivity(new Intent(this, NearLockActivity.class));

        }
    }
}
