package com.ut.module_lock.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.DialogUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityEditLimitedTimeBinding;
import com.ut.module_lock.entity.Key;

import java.util.Locale;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
@Route(path = RouterUtil.LockModulePath.EDIT_LIMITED_TIME)
public class EditLimitedTimeActivity extends BaseActivity {

    private ActivityEditLimitedTimeBinding mBinding = null;
    private Key keyInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_limited_time);
        initDarkToolbar();
        setTitle(R.string.lock_loop_key);
        keyInfo = (Key) getIntent().getSerializableExtra(Constance.KEY_INFO);
        mBinding.setKeyItem(keyInfo);
        mBinding.chooseStartTime.setOnClickListener(v -> dateChoose(v, "生效时间"));
        mBinding.chooseEndTime.setOnClickListener(v -> dateChoose(v, "失效时间"));
        mBinding.btnSave.setOnClickListener(v -> save());
    }

    private void dateChoose(View v, String title) {
        DialogUtil.chooseDateTime(v.getContext(), title, (year, month, day, hour, minute) -> {
            String dateTime = String.valueOf(year + "/"
                    + String.format(Locale.getDefault(), "%02d", month) + "/"
                    + String.format(Locale.getDefault(), "%02d", day) + " "
                    + String.format(Locale.getDefault(), "%02d", hour) + ":"
                    + String.format(Locale.getDefault(), "%02d", minute));
            if ("生效时间".equals(title)) {
                keyInfo.setStartTime(dateTime);
            } else {
                keyInfo.setEndTime(dateTime);
            }
            mBinding.setKeyItem(keyInfo);
        });
    }

    private void save() {
        Intent intent = new Intent();
        intent.putExtra(Constance.KEY_INFO, keyInfo);
        setResult(RESULT_OK, intent);
        finish();
    }
}
