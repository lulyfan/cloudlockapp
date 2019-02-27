package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.AudioPlayUtil;
import com.ut.base.Utils.RomUtils;
import com.ut.base.Utils.UTLog;
import com.ut.base.Utils.Util;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.LockKey;
import com.ut.module_lock.R;
import com.ut.module_lock.common.Constance;
import com.ut.module_lock.databinding.ActivityLockDetailBindingImpl;
import com.ut.module_lock.dialog.UnlockSuccessDialog;
import com.ut.module_lock.viewmodel.LockDetailVM;
import com.ut.unilink.UnilinkManager;
import com.ut.unilink.cloudLock.protocol.data.LockState;

import android.arch.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

@Route(path = RouterUtil.LockModulePath.LOCK_DETAIL)
public class LockDetailActivity extends BaseActivity {
    private LockKey mLockKey = null;

    private boolean mIsOnPause = false;

    ActivityLockDetailBindingImpl mDetailBinding;

    private LockDetailVM mLockDetailVM;
    private static final int BLEREAUESTCODE = 10001;
    private static final int BLEENABLECODE = 10002;

    private AtomicBoolean mIsShowDialogAndTip = new AtomicBoolean(false);
    private AtomicBoolean isAllowAutoOpen = new AtomicBoolean(true);
    private AtomicBoolean isNotCreateReusme = new AtomicBoolean(false);

    private long lastAutoOpenTime = 0L;

    private CustomerAlertDialog mPermissionDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableImmersive();
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_detail);
        mLockKey = getIntent().getParcelableExtra(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY);
        mLockDetailVM = ViewModelProviders.of(this).get(LockDetailVM.class);
        addPaddingTop();
        mDetailBinding.setPresent(new Present());
        initViewModel();
        isNotCreateReusme.set(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsOnPause = false;
        //todo 自动开锁
        UTLog.i(LockDetailVM.TAG, "onResume");
        if (isNotCreateReusme.get()) {
            autoOpen();
        }
        isNotCreateReusme.set(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UTLog.i(LockDetailVM.TAG, "onPause");
        mIsOnPause = true;
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }


    private void autoOpen() {
        if (!isNotManager() && (mLockKey.getCanOpen() == 1)
                && isAllowAutoOpen.get() && ifCanAutoOpen()) {
            //todo 自动开锁
            if (!mLockDetailVM.getReAutoOpen().hasObservers()) {
                mLockDetailVM.getReAutoOpen().observe(this, mReOpenObserver);
            }
            toOpenLock();
            UTLog.i(LockDetailVM.TAG, "toOpenLock 1");
        }
    }

    private synchronized boolean ifCanAutoOpen() {//防止同时调用自动开锁，魅族手机在禁用权限后请求权限会立即调用一次onpause和onresume
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAutoOpenTime < 500) {
            return false;
        }
        lastAutoOpenTime = currentTime;
        return true;
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
            if (mIsShowDialogAndTip.compareAndSet(true, false))
                CLToast.showAtBottom(LockDetailActivity.this, tip);
        });
        mLockDetailVM.getUnlockSuccessStatus().observe(this, type -> {
            if (type == LockDetailVM.SHOWTIPDIALOG_TYPE_UNLOCKSUCCESS) {
                mIsShowDialogAndTip.set(false);
                mLockDetailVM.setIsAutoOpen(true);
                AudioPlayUtil.get(this).play(0, true);

                //todo 弹出窗体前，做了当前activity是否可用处理，不然会报 error：View not attached to window manager，解决jra上的bug-669
                if(isUsable(this)) {
                    new UnlockSuccessDialog(this, false).show();
                }
            } else if (type == LockDetailVM.SHOWTIPDIALOG_TYPE_LOCKRESET) {
                new CustomerAlertDialog(LockDetailActivity.this, false)
                        .setMsg(getString(R.string.lock_detail_dialog_msg_reset))
                        .hideCancel()
                        .show();
            }

        });
        mLockDetailVM.getLockKey(mLockKey.getMac()).observeForever(mLockKeyObserver);

        mLockDetailVM.getShowDialog().observe(this, isShowDialog -> {
            if (isShowDialog) {
                if (mIsShowDialogAndTip.get()) {
                    startLoad();
                }
            } else {
                endLoad();
            }
        });
        mLockDetailVM.getElectricity().observe(this, electricity -> {
            mLockKey.setElectric(electricity);
            initLockData();
        });
    }

    private Observer<Boolean> mReOpenObserver = (reOpen) -> {
        UTLog.i(LockDetailVM.TAG, "reOpen mReOpenObserver");
        if (reOpen && isAllowAutoOpen.get()) {
            autoOpen();
        }
    };

    private Observer<LockKey> mLockKeyObserver = lockKey -> {
        if (lockKey != null) {
            mLockKey = lockKey;
        } else {
            mLockKey.setKeyStatus(EnumCollection.KeyStatus.HAS_DELETE.ordinal());
        }
        //初始化页面显示
        initLockData();
        initView();
        //判断是否开启自动开锁
        if (!EnumCollection.KeyStatus.isKeyValid(mLockKey.getKeyStatus())) {
            isAllowAutoOpen.set(false);
            endAutoOpenLock();
        } else {
            isAllowAutoOpen.set(true);
            if (!mIsOnPause) {//当钥匙被禁用后重新启用时需去判断是否自动开锁
                UTLog.i(LockDetailVM.TAG, "toOpenLock 11");
                autoOpen();
            }
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
            mLockDetailVM.setIsAutoOpen(false);
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
        if (RomUtils.isFlymeRom()//当为魅族手机时需要提醒用户打开定位服务
                && !checkGpsAndOpenLock())
            return;
        int scanResult = mLockDetailVM.openLock();
        UTLog.i(LockDetailVM.TAG, "toOpenLock");
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
                UTLog.i(LockDetailVM.TAG, "toOpenLock 2");
            } else {
                isAllowAutoOpen.set(false);//用户拒绝打开蓝牙后不再进行提示
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        UTLog.i(LockDetailVM.TAG, "onRequestPermissionsResult 1");
        if (requestCode == BLEREAUESTCODE && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {//定位未允许的时候提示去详情页修改
                    mPermissionDialog = new CustomerAlertDialog(this, false)
                            .setMsg(getString(R.string.lock_location_need_tips))
                            .setCancelLister(v -> {
                                isAllowAutoOpen.set(false);//用户拒绝打开蓝牙权限后不再进行自动开锁
                            })
                            .setConfirmListener(v -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            });
                    mPermissionDialog.show();
                    isAllowAutoOpen.set(false);
                    UTLog.i(LockDetailVM.TAG, "onRequestPermissionsResult 2");
                    return;
                }
            }
            toOpenLock();
            UTLog.i(LockDetailVM.TAG, "toOpenLock 3");
        }
    }

    private boolean checkGpsAndOpenLock() {
        if (!SystemUtils.isGPSOpen(LockDetailActivity.this)) {
            new CustomerAlertDialog(this, false)
                    .setMsg(getString(R.string.lock_gps_open_tips))
                    .setCancelLister(v -> {
                        isAllowAutoOpen.set(false);//用户拒绝打开蓝牙后不再进行提示
                    })
                    .setConfirmListener(v -> {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    })
                    .show();
            return false;
        }
        return true;
    }

    private void keyHasDeletedTips() {
        //TODO 中文
        CLToast.showAtCenter(this, getString(R.string.lock_key_had_deleted_tips));
    }

    private void endAutoOpenLock() {
        mLockDetailVM.getReAutoOpen().removeObserver(mReOpenObserver);
        mIsShowDialogAndTip.set(false);
        UnilinkManager.getInstance(this.getApplicationContext()).stopScan();
        UnilinkManager.getInstance(this.getApplicationContext()).disconnect(mLockKey.getMac());
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

    @BindingAdapter("setBackgroundGray")
    public static void setBackgroundGray(TextView textView, int lockStatus) {
        UTLog.i("======name:" + textView.getText() + " lockStatus:" + lockStatus);
        if (EnumCollection.KeyStatus.isKeyValid(lockStatus)) {
            textView.getCompoundDrawables()[1].setAlpha(255);
            return;
        } else if (textView.getId() == R.id.textView3) {
            if (lockStatus != EnumCollection.KeyStatus.HAS_DELETE.ordinal()) {
                textView.getCompoundDrawables()[1].setAlpha(255);
                return;
            }
        }
        textView.getCompoundDrawables()[1].setAlpha(128);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endAutoOpenLock();
        mLockDetailVM.getLockKey(mLockKey.getMac()).removeObserver(mLockKeyObserver);
    }
}
