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
    private static BaseFragment fragment_lock = null;
    private static BaseFragment fragment_msg = null;
    private static BaseFragment fragment_mall = null;
    private static BaseFragment fragment_mine = null;

    public static BaseFragment getLockFragment() {
        if (fragment_lock == null)
            fragment_lock = (BaseFragment) ARouter.getInstance().build(RouterUtil.LockModulePath.Fragment_Lock).navigation();
        return fragment_lock;
    }

    public static BaseFragment getMsgFragment() {
        if (fragment_msg == null)
            fragment_msg = (BaseFragment) ARouter.getInstance().build(RouterUtil.MsgModulePath.Fragment_MSG).navigation();

        return fragment_msg;
    }

    public static BaseFragment getMallFragment() {
        if (fragment_mall == null)
            fragment_mall = (BaseFragment) ARouter.getInstance().build(RouterUtil.MallModulePath.Fragment_Mall).navigation();
        return fragment_mall;
    }

    public static BaseFragment getMineFragment() {
        if (fragment_mine == null)
            fragment_mine = (BaseFragment) ARouter.getInstance().build(RouterUtil.MineModulePath.Fragment_Mine).navigation();
        return fragment_mine;
    }
}
