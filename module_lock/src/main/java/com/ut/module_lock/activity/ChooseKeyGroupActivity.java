package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :选择钥匙分组界面
 */

@Route(path = RouterUtil.LockModulePath.CHOOSE_KEY_GROUP)
public class ChooseKeyGroupActivity extends BaseActivity {
    private int currCheckedId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_choose_group);
        initDarkToolbar();
        setTitle(getString(R.string.choose_group));
        initAdd(() -> {

        });

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(currCheckedId == checkedId) {
                RadioButton rBtn =  group.findViewById(checkedId);
                rBtn.setChecked(false);
            }
        });
    }
}
