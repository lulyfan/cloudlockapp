package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityApplyKeyBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :
 */

public class  ApplyKeyActivity extends BaseActivity {

    private ActivityApplyKeyBinding mBinding;
    private String mac;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_key);
        initLightToolbar();
        setTitle(R.string.lock_apply_key);
        initView();
    }

    private void initView() {
        mBinding.rbtnGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtn_forever) {
                type = 1;
            } else if (checkedId == R.id.rbtn_limited) {
                type = 2;
            } else if (checkedId == R.id.rbtn_once) {
                type = 3;
            } else if (checkedId == R.id.rbtn_loop) {
                type = 4;
            }
        });

        mBinding.btnApplyKey.setOnClickListener(v -> {
            if (BaseApplication.getUser() == null) return;
            MyRetrofit.get().getCommonApiService()
                    .applyKey(BaseApplication.getUser().id, mac, mBinding.edtAskFor.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {

                        }
                        Log.d("applyKey", result.msg);
                    });
        });
    }
}
