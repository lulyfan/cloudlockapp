package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityLockDetailBindingImpl;
import com.ut.module_lock.viewmodel.LockDetailVM;
import com.ut.unilink.UnilinkManager;

@Route(path = RouterUtil.LockModulePath.LOCK_DETAIL)
public class LockDetailActivity extends BaseActivity {
    private LockKey mLockKey = null;

    ActivityLockDetailBindingImpl mDetailBinding;

    private LockDetailVM mLockDetailVM;
    private static final int BLEREAUESTCODE = 101;
    private static final int BLEENABLECODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_detail);
        mLockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.Extra_lock_detail);
        mLockKey.setStatusStr(this.getResources().getStringArray(R.array.key_status));
        mLockKey.setLockTypeStr(this.getResources().getStringArray(R.array.lock_type));
        mLockKey.setKeyTypeStr(this.getResources().getStringArray(R.array.key_type));
        mLockKey.setElectricityStr();
        addPaddingTop();
        mDetailBinding.setLockKey(mLockKey);
        mDetailBinding.setPresent(new Present());

        initViewModel();
    }

    private void initViewModel() {
        mLockDetailVM = ViewModelProviders.of(this).get(LockDetailVM.class);
        mLockDetailVM.setLockKey(mLockKey);
        mLockDetailVM.getConnectStatus().observe(this, isConnected -> {
            if (isConnected) {
                mDetailBinding.ivLockDetailBle.setImageResource(R.mipmap.icon_bluetooth_green);
            } else {
                mDetailBinding.ivLockDetailBle.setImageResource(R.mipmap.icon_bluetooth_grey);
            }
        });
        mLockDetailVM.getShowTip().observe(this, tip -> {
            UTLog.i("open lock show tip:" + tip);
            toastShort(tip);
        });
    }


    private void addPaddingTop() {
        View view = findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(this), view.getPaddingRight(), view.getPaddingBottom());
    }

    public class Present {
        public void onBackClick(View view) {
            onBackPressed();
        }

        public void onOpenLockClick(View view) {
            toOpenLock();
        }

        public void onSendKeyClick(View view) {
//            startActivity(new Intent(LockDetailActivity.this, GrantPermissionActivity.class));
            ARouter.getInstance().build(RouterUtil.BaseModulePath.GRANTPERMISSION)
                    .withString(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MAC, mLockKey.getMac())
                    .navigation();
        }

        public void onMangeKeyClick(View view) {
            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_MANAGER).withParcelable(Constance.LOCK_KEY, mLockKey)
                    .withParcelable(Constance.LOCK_KEY, mLockKey)
                    .navigation();
        }

        public void onOperateRecordClick(View view) {
            ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD)
                    .withString(Constance.RECORD_TYPE, Constance.BY_LOCK)
                    .withLong(Constance.LOCK_ID, Long.valueOf(mLockKey.getId()))
                    .navigation();
        }

        public void onLockManageClick(View view) {
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_SETTING)
                    .withParcelable(Constance.LOCK_KEY, mLockKey)
                    .navigation();
        }
    }

    @BindingAdapter("electricityDrawable")
    public static void loadDrawable(TextView textView, int electricity) {
        UTLog.i("electricity:" + electricity);
        Drawable leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_green);
        if (electricity < 20) {
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_red);
        } else if (electricity < 40) {
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_electricity_orange);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        textView.setCompoundDrawables(leftDrawable, textView.getCompoundDrawables()[1],
                textView.getCompoundDrawables()[2], textView.getCompoundDrawables()[3]);
    }

    @BindingAdapter("bgSrc")
    public static void loadbubble(TextView textView, int userType) {
        int srcId = R.mipmap.icon_bubble_orange;
        if (userType == EnumCollection.UserType.AUTH.ordinal()) {
            srcId = R.mipmap.icon_bubble_purple;
        } else if (userType == EnumCollection.UserType.NORMAL.ordinal()) {
            srcId = R.mipmap.icon_bubble_gray;
        }
        textView.setBackgroundResource(srcId);
    }

    @BindingAdapter("userType")
    public static void loadUserImage(ImageView imageView, int userType) {
        if (userType == EnumCollection.UserType.ADMIN.ordinal()) {
            imageView.setImageResource(R.mipmap.icon_user_manager_detail);
        } else if (userType == EnumCollection.UserType.AUTH.ordinal()) {
            imageView.setImageResource(R.mipmap.icon_user_auth_detail);
        } else {
            imageView.setImageResource(R.mipmap.icon_user_normal_detail);
        }
    }

    @BindingAdapter("setVisiable")
    public static void setVisiable(TextView textView, int userType) {
        if (userType == EnumCollection.UserType.ADMIN.ordinal() || userType == EnumCollection.UserType.AUTH.ordinal()) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private void toOpenLock() {
        int scanResult = mLockDetailVM.openLock();
        switch (scanResult) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                toastShort(getString(R.string.lock_tip_ble_not_support));
                break;
            case UnilinkManager.NO_LOCATION_PERMISSION:
                UnilinkManager.getInstance(getApplicationContext()).requestPermission(this, BLEREAUESTCODE);
                break;
            case UnilinkManager.BLE_NOT_OPEN:
                UnilinkManager.getInstance(getApplicationContext()).enableBluetooth(this, BLEENABLECODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BLEREAUESTCODE || requestCode == BLEENABLECODE)
                toOpenLock();
        }
    }
}
