package com.ut.module_lock;

import android.os.Bundle;

import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.FragmentUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.fl_lcok, FragmentUtil.getLockFragment()).commit();
    }
}
