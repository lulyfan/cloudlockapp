package com.ut.module_lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ut.base.BaseFragment;
import com.ut.module_lock.R;

/**
 * author : zhouyubin
 * time   : 2019/01/14
 * desc   :
 * version: 1.0
 */
public class DeviceKeyForeverFrag extends BaseFragment {
    private View rootView;

    public DeviceKeyForeverFrag() {
    }

    public static DeviceKeyForeverFrag newInstance() {
        DeviceKeyForeverFrag fragment = new DeviceKeyForeverFrag();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.include_dk_permission_forever, container, false);
        }
        return rootView;
    }
}
