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
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityLockDetailBindingImpl;
import com.ut.module_lock.dialog.UnlockSuccessDialog;
import com.ut.module_lock.viewmodel.LockDetailVM;
import com.ut.unilink.UnilinkManager;

import android.arch.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

@Route(path = RouterUtil.LockModulePath.LOCK_DETAIL)
public class LockDetailActivity extends BaseActivity {
    private LockKey mLockKey = null;

    ActivityLockDetailBindingImpl mDetailBinding;

    private LockDetailVM mLockDetailVM;
    private static final int BLEREAUESTCODE = 101;
    private static final int BLEENABLECODE = 102;

    private AtomicBoolean mIsShowDialogAndTip = new AtomicBoolean(false);
    private AtomicBoolean isToRequestPermission = new AtomicBoolean(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_detail);
        mLockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
        mLockDetailVM = ViewModelProviders.of(this).get(LockDetailVM.class);
        initLockData();
        addPaddingTop();
        initView();
        mDetailBinding.setPresent(new Present());
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo 自动开锁
        autoOpen();
    }

    private void autoOpen() {
        if (!isNotManager() && (mLockKey.getCanOpen() == 1) && isToRequestPermission.get()) {
            toOpenLock();
        }
    }

    private void initView() {
        if (isNotManager()) {
            mDetailBinding.functionTvBleKey.setVisibility(View.GONE);
            mDetailBinding.functionTvDeviceKey.setVisibility(View.GONE);
            mDetailBinding.functionTvManagekey.setVisibility(View.GONE);
            mDetailBinding.functionTvSendkey.setVisibility(View.GONE);
            mDetailBinding.tvTouchOpen.setVisibility(View.GONE);
            return;
        }
        if (mLockKey.getType() == EnumCollection.LockType.SMARTLOCK.getType()) {
            mDetailBinding.functionTvManagekey.setVisibility(View.GONE);
            mDetailBinding.functionTvSendkey.setVisibility(View.GONE);
            mDetailBinding.functionTvBleKey.setVisibility(View.VISIBLE);
            mDetailBinding.functionTvDeviceKey.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNotManager() {
        return mLockKey.getUserType() != EnumCollection.UserType.ADMIN.ordinal() &&
                mLockKey.getUserType() != EnumCollection.UserType.AUTH.ordinal();
    }

    private void initLockData() {
        if (mLockKey == null) return;
        mLockKey.setStatusStr(this.getResources().getStringArray(R.array.key_status));
        mLockKey.setLockTypeStr(this.getResources().getStringArray(R.array.lock_type));
        mLockKey.setKeyTypeStr(this.getResources().getStringArray(R.array.key_type));
        mLockKey.setElectricityStr();
        mDetailBinding.setLockKey(mLockKey);
        mLockDetailVM.setLockKey(mLockKey);
    }

    private void initViewModel() {
        mLockDetailVM.getConnectStatus().observe(this, isConnected -> {
            if (isConnected) {
                mDetailBinding.ivLockDetailBle.setImageResource(R.mipmap.icon_bluetooth_green);
            } else {
                mDetailBinding.ivLockDetailBle.setImageResource(R.mipmap.icon_bluetooth_grey);
            }
        });
        mLockDetailVM.getShowTip().observe(this, tip -> {
            UTLog.i("open lock show tip:" + tip);
            CLToast.showAtBottom(LockDetailActivity.this, tip);
        });
        mLockDetailVM.getUnlockSuccessStatus().observe(this, success -> {
            mIsShowDialogAndTip.set(false);
            new UnlockSuccessDialog(this, false).show();
        });
        mLockDetailVM.getLockKey().observe(this, lockKey -> {
            mLockKey = lockKey;
            mLockDetailVM.setLockKey(mLockKey);
            initLockData();
        });
        //todo 自动开锁
        mLockDetailVM.getReAutoOpen().observe(this, mReOpenObserver);
        mLockDetailVM.getShowDialog().observe(this, isShowDialog -> {
            if (isShowDialog) {
                if (mIsShowDialogAndTip.compareAndSet(true, false)) {
                    startLoad();
                }
            } else {
                endLoad();
            }
        });
    }

    private Observer<Boolean> mReOpenObserver = (reOpen) -> {
        if (reOpen) {
            autoOpen();
        }
    };

    private void addPaddingTop() {
        View view = findViewById(R.id.parent);
        view.setPadding(view.getPaddingLeft(), Util.getStatusBarHeight(this), view.getPaddingRight(), view.getPaddingBottom());
    }

    public class Present {
        public void onBackClick(View view) {
            onBackPressed();
            endAutoOpenLock();
        }

        public void onOpenLockClick(View view) {
            mIsShowDialogAndTip.set(true);
            toOpenLock();
        }

        public void onSendKeyClick(View view) {
            if (mLockKey == null) {
                keyHasDeletedTips();
                return;
            }
            ARouter.getInstance().build(RouterUtil.BaseModulePath.SEND_KEY)
                    .withString(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_SENDKEY_MAC, mLockKey.getMac())
                    .withInt(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY_USERTYPE, mLockKey.getUserType())
                    .navigation();
            endAutoOpenLock();
        }

        public void onMangeKeyClick(View view) {
            if (mLockKey == null) {
                keyHasDeletedTips();
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.KEY_MANAGER)
                    .withParcelable(Constance.LOCK_KEY, mLockKey)
                    .navigation();
            endAutoOpenLock();
        }

        public void onOperateRecordClick(View view) {
            if (mLockKey == null) {
                keyHasDeletedTips();
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.OPERATION_RECORD)
                    .withString(Constance.RECORD_TYPE, Constance.BY_LOCK)
                    .withLong(Constance.LOCK_ID, Long.valueOf(mLockKey.getId()))
                    .navigation();
            endAutoOpenLock();
        }

        public void onLockManageClick(View view) {
            if (mLockKey == null) {
                keyHasDeletedTips();
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_SETTING)
                    .withParcelable(Constance.LOCK_KEY, mLockKey)
                    .navigation();
            endAutoOpenLock();
        }

        public void onDeviceKeyClick(View view) {
            if (mLockKey == null) {
                keyHasDeletedTips();
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DEVICE_KEY)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, mLockKey)
                    .navigation();
            endAutoOpenLock();
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
            case UnilinkManager.SCAN_SUCCESS:
                mLockDetailVM.getShowDialog().postValue(true);
                break;
            case -3:
                CLToast.showAtCenter(this, getString(R.string.lock_open_lock_invaild_tips));
                break;
            case -100:
                keyHasDeletedTips();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLEENABLECODE) {
            if (resultCode == RESULT_OK) {
                toOpenLock();
            } else {
                isToRequestPermission.set(false);//用户拒绝打开蓝牙后不再进行提示
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        if (requestCode == BLEREAUESTCODE) {
            toOpenLock();
        }
    }

    private void keyHasDeletedTips() {
        //TODO 中文
        CLToast.showAtCenter(this, "钥匙已被删除，无法开锁，请联系钥匙发送者");
    }

    private void endAutoOpenLock() {
        UnilinkManager.getInstance(this.getApplicationContext()).stopScan();
        mLockDetailVM.getReAutoOpen().removeObserver(mReOpenObserver);
        mIsShowDialogAndTip.set(false);
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


    @BindingAdapter("touchDrawableLeft")
    public static void touchDrawableLeft(TextView textView, int canOpen) {
        UTLog.i("canOpen:" + canOpen);
        Drawable leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_touch_unable);
        if (canOpen == 1) {
            leftDrawable = textView.getResources().getDrawable(R.mipmap.icon_touch_enable);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getIntrinsicWidth(), leftDrawable.getIntrinsicHeight());
        textView.setCompoundDrawables(leftDrawable, textView.getCompoundDrawables()[1],
                textView.getCompoundDrawables()[2], textView.getCompoundDrawables()[3]);
    }
}
