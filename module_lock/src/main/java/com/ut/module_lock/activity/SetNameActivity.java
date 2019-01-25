package com.ut.module_lock.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.CommonApi;
import com.ut.base.AppManager;
import com.ut.base.BaseActivity;
import com.ut.base.ErrorHandler;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.database.daoImpl.LockKeyDaoImpl;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivitySetNameBinding;
import com.ut.module_lock.dialog.AddSuccessDialog;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SetNameActivity extends BaseActivity {
    private ActivitySetNameBinding mSetNameBinding = null;

    public static final String BUNDLE_EXTRA_BLE = "bundle_extra_ble";

    private Disposable mDisposable = null;

    public static void start(Context context, Bundle data) {
        Intent intent = new Intent(context, SetNameActivity.class);
        if (data != null) {
            intent.putExtras(data);
        }
        context.startActivity(intent);
    }

    private String mMac = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSetNameBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_name);
        setTitle(R.string.lock_title_set_name);
        initLightToolbar();
        initView();
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mMac = bundle.getString(BUNDLE_EXTRA_BLE);
        }
    }

    private void initView() {
        mSetNameBinding.clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSetNameBinding.btnSetNameConfirm.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onXmlClick(View view) {
        super.onXmlClick(view);
        int i = view.getId();
        if (i == R.id.btn_setName_confirm) {
            String name = mSetNameBinding.clearEditText.getText().toString();
            mDisposable = CommonApi.updateLockName(mMac, name)
                    .subscribe(voidResult -> {
                        if (voidResult.code == 200) {
                            AddSuccessDialog dialog = new AddSuccessDialog(this, false, name);
                            dialog.setConfirmListener(v -> {
                                dialog.dismiss();
                                goToLockDetail();
                            }).show();
                        } else {
                            toastShort(voidResult.msg);
                        }
                    }, throwable -> {
                        toastShort(getString(R.string.lock_tip_setname_failed));
                    });
        }
    }

    @SuppressLint("CheckResult")
    private void goToLockDetail() {
        CommonApi.pageUserLock(1, -1)
                .subscribeOn(Schedulers.io())
                .map(results->{
                    LockKey lk = null;
                    if (results.isSuccess()) {
                        List<LockKey> lockKeyList = results.getData();
                        for (LockKey lockKey : lockKeyList) {
                            if (lockKey.getMac().equals(mMac)) {
                                lk = lockKey;
                                LockKeyDaoImpl.get().insert(lk);
                                break;
                            }
                        }
                    }
                    return lk;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lk -> {
                    if (lk != null) {
                        AppManager.getAppManager().finishActivity(SetNameActivity.class);
                        AppManager.getAppManager().finishActivity(NearLockActivity.class);
                        AppManager.getAppManager().finishActivity(AddGuideActivity.class);
                        ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DETAIL)
                                .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, lk)
                                .navigation();
                    }
                }, new ErrorHandler());
    }
}
