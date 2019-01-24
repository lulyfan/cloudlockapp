package com.ut.module_lock.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.common.CommonAdapter;
import com.ut.base.common.CommonViewHolder;
import com.ut.commoncomponent.CLToast;
import com.ut.database.entity.DeviceKey;
import com.ut.database.entity.EnumCollection;
import com.ut.module_lock.R;
import com.ut.module_lock.databinding.FragmentDeviceKeyBinding;
import com.ut.module_lock.viewmodel.DeviceKeyVM;

import java.util.List;

public class DeviceKeyListFragment extends BaseFragment {
    private static final String EXTRA_KEY_TYPE = "extra_key_type";
    private DeviceKeyVM mDeviceKeyVM;
    private int mDeviceKeyType;
    private CommonAdapter<DeviceKey> mDeviceKeyCommonAdapter;
    private FragmentDeviceKeyBinding mFragmentDeviceKeyBinding = null;

    private View rootView;


    public DeviceKeyListFragment() {
    }

    public static DeviceKeyListFragment newInstance(int keyType) {
        DeviceKeyListFragment fragment = new DeviceKeyListFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_KEY_TYPE, keyType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mFragmentDeviceKeyBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_device_key, container, false);
            rootView = mFragmentDeviceKeyBinding.getRoot();
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        iniData();
        initVM();
        initListener();
    }

    private void initListener() {
        mFragmentDeviceKeyBinding.lvDeviceKey.setOnItemClickListener((parent, view, position, id) -> {
            DeviceKey deviceKey = (DeviceKey) parent.getAdapter().getItem(position);
            if (deviceKey.getKeyType() == EnumCollection.DeviceKeyType.PASSWORD.ordinal()
                    && deviceKey.getKeyID() == 0) {
                CLToast.showAtBottom(DeviceKeyListFragment.this.getContext(), getString(R.string.lock_device_key_admin_pwd_tip));
                return;
            }
            ARouter.getInstance().build(RouterUtil.LockModulePath.LOCK_DEVICE_KEY_DETAIL)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_DEVICE_KEY, deviceKey)
                    .withParcelable(RouterUtil.LockModuleExtraKey.EXTRA_LOCK_KEY, mDeviceKeyVM.getLockKey())
                    .navigation();
        });
    }

    private void iniData() {
        Bundle bundle = getArguments();
        mDeviceKeyType = bundle.getInt(EXTRA_KEY_TYPE);
    }

    private void initVM() {
        mDeviceKeyVM = ViewModelProviders.of(getActivity()).get(DeviceKeyVM.class);
        mDeviceKeyVM.getDeviceKeys(mDeviceKeyType).observe(this, deviceKeys -> {
            refreshData(deviceKeys);
        });
    }

    private void refreshData(List<DeviceKey> deviceKeys) {
        if (deviceKeys == null) return;
        if (mDeviceKeyCommonAdapter == null) {
            mDeviceKeyCommonAdapter = new CommonAdapter<DeviceKey>(getContext(), deviceKeys, R.layout.item_device_key) {
                @Override
                public void convert(CommonViewHolder commonViewHolder, int position, DeviceKey item) {
                    TextView keyAuthType = commonViewHolder.getView(R.id.tv_device_key_auth_type);
                    TextView keyName = commonViewHolder.getView(R.id.tv_device_key_name);
                    TextView keyStatus = commonViewHolder.getView(R.id.tv_device_key_status);
                    if (!EnumCollection.DeviceKeyStatus.isNormal(item.getKeyStatus())) {
                        keyAuthType.setEnabled(false);
                        keyName.setEnabled(false);
                        keyStatus.setVisibility(View.VISIBLE);
                    } else {
                        keyAuthType.setEnabled(true);
                        keyName.setEnabled(true);
                        keyStatus.setVisibility(View.GONE);
                    }
                    keyAuthType.setText(getResources().getStringArray(R.array.device_key_auth_type)[item.getKeyAuthType()]);
                    keyName.setText(item.getName());
                    keyStatus.setText(getResources().getStringArray(R.array.device_key_status)[item.getKeyStatus()]);
                }
            };
            View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.layout_empty_data, null);
//            emptyView.findViewById(R.id.tv_connectLock).setOnClickListener(v -> {
//                mDeviceKeyVM.connectAndGetData(mDeviceKeyVM.getLockKey().getType(), getActivity());
//            });
            ViewGroup parent = (ViewGroup) mFragmentDeviceKeyBinding.lvDeviceKey.getParent();
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            parent.addView(emptyView, parent.getChildCount(), layoutParams);
            mFragmentDeviceKeyBinding.lvDeviceKey.setEmptyView(emptyView);
            mFragmentDeviceKeyBinding.lvDeviceKey.setAdapter(mDeviceKeyCommonAdapter);
        } else {
            mDeviceKeyCommonAdapter.notifyData(deviceKeys);
        }
    }
}