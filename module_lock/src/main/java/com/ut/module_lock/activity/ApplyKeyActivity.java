package com.ut.module_lock.activity;

import android.content.Context;
import android.content.Intent;
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
import com.ut.database.entity.NearScanLock;
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
    public static final String EXTRA_KEY_DATA = "extra_key_data";

    private ActivityApplyKeyBinding mBinding;
    private String mac;
    private int ruleType;
    private String name;

    public static void start(Context context, Bundle data) {
        Intent intent = new Intent(context, ApplyKeyActivity.class);
        if (data != null) {
            intent.putExtras(data);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_apply_key);
        initData();
        initLightToolbar();
        setTitle(R.string.lock_apply_key);
        initView();
//        mac = "33-33-22-A1-B0-34";
    }

    private void initData() {
        NearScanLock nearScanLock = getIntent().getParcelableExtra(EXTRA_KEY_DATA);
        mac = nearScanLock.getMac();
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

        mBinding.tvKeyName.setText(mac);

        mBinding.btnApplyKey.setOnClickListener(v -> {
            if (BaseApplication.getUser() == null) return;
            MyRetrofit.get().getCommonApiService()
                    .applyKey(BaseApplication.getUser().id, mac, mBinding.edtAskFor.getText().toString(), ruleType)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (result.isSuccess()) {
                            finish();
                        }
                        CLToast.showAtCenter(getBaseContext(), result.msg);
                        Log.d("applyKey", result.msg);
                    }, new ErrorHandler());
        });
    }
}
