package com.ut.module_lock.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ut.base.BaseActivity;
import com.ut.base.UIUtils.SystemUtils;
import com.ut.base.Utils.RomUtils;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonViewHolder;
import com.ut.base.dialog.CustomerAlertDialog;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.EnumCollection;
import com.ut.database.entity.NearScanLock;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.ActivityNearLockBinding;
import com.ut.module_lock.viewmodel.NearLockVM;
import com.ut.unilink.UnilinkManager;

import java.util.List;

public class NearLockActivity extends BaseActivity {
    private ActivityNearLockBinding mNearLockBinding;
    private CommonAdapter<NearScanLock> mAdapter;
    private NearLockVM mNearLockVM = null;
    private NearScanLock mSelectedLock = null;
    private static final int BLEREQUESTCODE = 101;
    private static final int BLEENABLECODE = 102;
    private TextView mEmptyTip = null;
    private TextView mEmptyTip2 = null;
    private View emptyView = null;
    private CustomerAlertDialog mPermissionDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNearLockBinding = DataBindingUtil.setContentView(this, R.layout.activity_near_lock);
        initToolbar();
        initEmptyView();
        initListListener();
        initViewModel();
        toScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }

    private void toScan() {
        mNearLockBinding.swfBleDevice.postDelayed(() -> {
            mNearLockBinding.swfBleDevice.setRefreshing(false);
        }, 1500);
        if (RomUtils.isFlymeRom()//当为魅族手机时需要提醒用户打开定位服务
                && !checkGpsAndOpenLock()) {
            return;
        }
        int scanResult = mNearLockVM.startScan();
        switch (scanResult) {
            case UnilinkManager.BLE_NOT_SUPPORT:
                toastShort(getString(R.string.lock_tip_ble_not_support));
                break;
            case UnilinkManager.NO_LOCATION_PERMISSION:
                UnilinkManager.getInstance(getApplicationContext()).requestPermission(this, BLEREQUESTCODE);
                break;
            case UnilinkManager.BLE_NOT_OPEN:
                UnilinkManager.getInstance(getApplicationContext()).enableBluetooth(this, BLEENABLECODE);
                break;
        }
    }

    private void initViewModel() {
        mNearLockVM = ViewModelProviders.of(this).get(NearLockVM.class);
        mNearLockVM.getNearScanLocks().observe(this, this::refreshListData);
        mNearLockVM.isOperating().observe(this, isScanning -> {
            if (isScanning) {
                mNearLockBinding.progressBarGreen.setVisibility(View.VISIBLE);
                mEmptyTip.setText(R.string.scaning);
                mEmptyTip2.setVisibility(View.GONE);
            } else {
                mEmptyTip.setText(R.string.lock_near_lock_scan_tip_error);
                mNearLockBinding.progressBarGreen.setVisibility(View.INVISIBLE);
                mEmptyTip2.setVisibility(View.VISIBLE);
            }
        });
        mNearLockVM.getErrorCode().observe(this, errorMsg -> {
            endLoad();
            toastShort(errorMsg);
        });
        mNearLockVM.getBindLock().observe(this, cloudLock -> {
            Bundle bundle = new Bundle();
            bundle.putString(SetNameActivity.BUNDLE_EXTRA_BLE, cloudLock.getAddress());
            SetNameActivity.start(NearLockActivity.this, bundle);

            //绑定成功后，更新绑定锁的状态，刷新界面
            if (mSelectedLock != null) {
                List<NearScanLock> data = mAdapter.getData();
                if (data.indexOf(mSelectedLock) > -1) {
                    data.get(data.indexOf(mSelectedLock)).setBindStatus(EnumCollection.BindStatus.HASBIND.ordinal());
                    mAdapter.notifyData(data);
                }
            }
        });
    }

    private void initListListener() {
        mNearLockBinding.lvBleDevice.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
                    NearScanLock data = (NearScanLock) parent.getAdapter().getItem(position);
                    if (data.getBindStatus() == EnumCollection.BindStatus.HASBIND.ordinal()) {
                        String tip = getString(R.string.lock_tip_already_bind);
                        showTipDialog(tip);
                    } else if (data.getBindStatus() == EnumCollection.BindStatus.OTHERBIND_NOKEY.ordinal()) {
                        showApplyDialog(data);
                    } else if (data.getBindStatus() == EnumCollection.BindStatus.OTHERBIND_HASKE.ordinal()) {
                        if (EnumCollection.KeyStatus.isKeyValid(data.getKeyStatus())) {
                            String tip = getString(R.string.lock_tip_have_key);
                            showTipDialog(tip);
                        } else {
                            showApplyDialog(data);
                        }
                    }
                }
        );
        mNearLockBinding.swfBleDevice.setColorSchemeResources(R.color.color_tv_blue);
        mNearLockBinding.swfBleDevice.setOnRefreshListener(() -> {
            toScan();
        });
    }

    private void showApplyDialog(NearScanLock data) {
        new CustomerAlertDialog(this, false)
                .setMsg(getString(R.string.lock_tip_apply_key))
                .setConfirmText(getString(R.string.lock_apply_key))
                .setConfirmListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ApplyKeyActivity.EXTRA_KEY_DATA, data);
                    ApplyKeyActivity.start(NearLockActivity.this, bundle);
                }).show();
    }

    private void showTipDialog(String tip) {
        new CustomerAlertDialog(this, false)
                .setMsg(tip)
                .hideCancel()
                .show();
    }

    private void initToolbar() {
        initLightToolbar();
        setTitle(R.string.lock_title_near_lock);
    }


    private void refreshListData(List<NearScanLock> datas) {
        if (mAdapter == null) {
            if (datas == null) {
                return;
            }

            mAdapter = new CommonAdapter<NearScanLock>(this, datas, R.layout.item_ble_list) {
                @Override
                public void convert(CommonViewHolder commonViewHolder, int position, NearScanLock item) {
                    TextView textView = commonViewHolder.getView(R.id.tv_ble_name);
                    textView.setText(item.getName());
                    ImageButton imageButton = commonViewHolder.getView(R.id.iBtn_ble_add);
                    imageButton.setVisibility(item.getBindStatus() == EnumCollection.BindStatus.UNBIND.ordinal() ? View.VISIBLE : View.GONE);
                    imageButton.setOnClickListener(v -> {
                        startLoad();
                        mNearLockVM.bindLock(item);
                        mSelectedLock = item;
                    });
                }
            };
            mNearLockBinding.lvBleDevice.setEmptyView(emptyView);
            mNearLockBinding.lvBleDevice.setAdapter(mAdapter);
        } else {
            mAdapter.notifyData(datas);
        }
    }

    @NonNull
    private void initEmptyView() {
        emptyView = findViewById(R.id.empty_view_list);
        mEmptyTip = emptyView.findViewById(R.id.textView4);
        mEmptyTip2 = emptyView.findViewById(R.id.textView5);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BLEENABLECODE)
                toScan();
        } else {
            mNearLockBinding.progressBarGreen.setVisibility(View.INVISIBLE);
            if (requestCode == BLEENABLECODE) {
                CLToast.showAtBottom(this, getString(R.string.lock_tip_open_ble));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == BLEREQUESTCODE && grantResults.length > 0) {
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {//定位权限被禁止时去详情页设置
                    mPermissionDialog = new CustomerAlertDialog(this, false)
                            .setMsg(getString(R.string.lock_location_need_tips))
                            .setCancelLister(v -> {
                            })
                            .setConfirmListener(v -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            });
                    mPermissionDialog.show();
                    return;
                }
            }
            toScan();
        }
    }

    private boolean checkGpsAndOpenLock() {
        if (!SystemUtils.isGPSOpen(this)) {
            new CustomerAlertDialog(this, false)
                    .setMsg(getString(R.string.lock_gps_open_tips))
                    .setCancelLister(v -> {
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

}
