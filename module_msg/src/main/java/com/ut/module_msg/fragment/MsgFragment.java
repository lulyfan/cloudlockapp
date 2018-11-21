package com.ut.module_msg.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;
import com.ut.base.UIUtils.RouterUtil;
import com.ut.base.Utils.UTLog;
import com.ut.module_msg.MainActivity;
import com.ut.module_msg.R;
import com.ut.module_msg.databinding.FragmentMsgBinding;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
@Route(path = RouterUtil.MsgModulePath.Fragment_MSG)
public class MsgFragment extends BaseFragment {
    View mView = null;
    FragmentMsgBinding mMsgBinding = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mMsgBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_msg, container, false);
            mView = mMsgBinding.getRoot();
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMsgBinding.setPresenter(new Present());
    }

    public class Present {
        public void onClick(View view) {
            UTLog.i("present onclick");
            ARouter.getInstance().build(RouterUtil.LoginModulePath.Login).navigation();
        }
    }
}
