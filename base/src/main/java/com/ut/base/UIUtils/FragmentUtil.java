package com.ut.base.UIUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.ut.base.BaseFragment;

/**
 * author : zhouyubin
 * time   : 2018/11/20
 * desc   :
 * version: 1.0
 */
public class FragmentUtil {

    public static BaseFragment getLockFragment() {
        BaseFragment fragment = (BaseFragment) ARouter.getInstance().build(RouterUtil.LockModulePath.Fragment_Lock).navigation();
        return fragment;
    }

    public static BaseFragment getMsgFragment() {
        BaseFragment fragment = (BaseFragment) ARouter.getInstance().build(RouterUtil.MsgModulePath.Fragment_MSG).navigation();
        return fragment;
    }

    public static BaseFragment getMallFragment() {
        BaseFragment fragment = (BaseFragment) ARouter.getInstance().build(RouterUtil.MallModulePath.Fragment_Mall).navigation();
        return fragment;
    }

    public static BaseFragment getMineFragment() {
        BaseFragment fragment = (BaseFragment) ARouter.getInstance().build(RouterUtil.MineModulePath.Fragment_Mine).navigation();
        return fragment;
    }
}
