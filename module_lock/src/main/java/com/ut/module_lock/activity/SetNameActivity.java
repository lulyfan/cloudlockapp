package com.ut.module_lock.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.operation.CommonApi;
import com.ut.base.AppManager;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivitySetNameBinding;
import com.ut.module_lock.dialog.AddSuccessDialog;

import io.reactivex.disposables.Disposable;

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
                                AppManager.getAppManager().finishAllActivity();
                                ARouter.getInstance().build(RouterUtil.MainModulePath.Main_Module).navigation();
                            }).show();
                        } else {
                            toastShort(voidResult.msg);
                        }
                    }, throwable -> {
                        toastShort(getString(R.string.lock_tip_setname_failed));
                    });
        }
    }
}
