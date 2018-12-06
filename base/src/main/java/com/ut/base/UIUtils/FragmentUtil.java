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
        BaseFragment  fragment_lock = (BaseFragment) ARouter.getInstance().build(RouterUtil.LockModulePath.Fragment_Lock).navigation();
        return fragment_lock;
    }

    public static BaseFragment getMsgFragment() {
        BaseFragment   fragment_msg = (BaseFragment) ARouter.getInstance().build(RouterUtil.MsgModulePath.Fragment_MSG).navigation();

        return fragment_msg;
    }

    public static BaseFragment getMallFragment() {
        BaseFragment    fragment_mall = (BaseFragment) ARouter.getInstance().build(RouterUtil.MallModulePath.Fragment_Mall).navigation();
        return fragment_mall;
    }

    public static BaseFragment getMineFragment() {
        BaseFragment    fragment_mine = (BaseFragment) ARouter.getInstance().build(RouterUtil.MineModulePath.Fragment_Mine).navigation();
        return fragment_mine;
    }
}
