package com.ut.module_msg;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.FragmentUtil;
import com.ut.module_msg.fragment.MsgFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.fly_content,FragmentUtil.getMsgFragment(),"").commit();
    }
}
