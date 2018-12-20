package com.ut.module_lock.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.example.operation.MyRetrofit;
import com.ut.base.BaseActivity;
import com.ut.base.BaseApplication;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.commoncomponent.CLToast;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityApplyKeyBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : chenjiajun
 * time   : 2018/12/4
 * desc   :
 */

@Route(path = RouterUtil.LockModulePath.APPLY_KEY)
public class ApplyKeyActivity extends BaseActivity {

    private ActivityApplyKeyBinding mBinding;
    private String mac;
    private int ruleType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_key);
        initLightToolbar();
        setTitle(R.string.lock_apply_key);
        initView();
        //todo
        mac = "33-33-22-A1-B0-34";
    }

    private void initView() {
        mBinding.rbtnGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbtn_forever) {
                ruleType = 1;
            } else if (checkedId == R.id.rbtn_limited) {
                ruleType = 2;
            } else if (checkedId == R.id.rbtn_once) {
                ruleType = 3;
            } else if (checkedId == R.id.rbtn_loop) {
                ruleType = 4;
            }
        });

        mBinding.btnApplyKey.setOnClickListener(v -> {
            if (BaseApplication.getUser() == null) return;
            MyRetrofit.get().getCommonApiService()
                    .applyKey(BaseApplication.getUser().id, mac, mBinding.edtAskFor.getText().toString(), ruleType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                        } else {
                        }
                        CLToast.showAtCenter(getBaseContext(), result.msg);
                        Log.d("applyKey", result.msg);
                    }, new ErrorHandler());
        });
    }
}
