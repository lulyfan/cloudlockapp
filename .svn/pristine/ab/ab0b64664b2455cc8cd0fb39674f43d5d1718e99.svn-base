package com.ut.module_mall.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.module_mall.R;
import com.ut.module_mall.databinding.*;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
@Route(path = RouterUtil.MallModulePath.Fragment_Mall)
public class MallFragment extends BaseFragment {
    View mView = null;
    FragmentMallBinding mMallBinding = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mMallBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_mall, container, false);
            mView = mMallBinding.getRoot();
        }
        return mView;
    }
}
