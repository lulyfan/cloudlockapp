package com.ut.base.UIUtils;

/**
 * author : zhouyubin
 * time   : 2018/11/13
 * desc   :
 * version: 1.0
 */
public class RouterUtil {
    public static class MainModulePath {

        //主activity路径
        public static final String Main_Module = "/app/main";


    }

    public static class LoginModulePath {
        //登录activity的路径
        public static final String Login = "/login/login";

        public static final String REGISTER = "/login/register";

        public static final String FORGET_PWD = "/login/forgetPassword";

        public static final String SELECT_COUNTRY_AREA_CODE = "/login/selectCountryAreaCode";
    }

    public static class LockModulePath {
        //锁模块的首页
        public static final String Fragment_Lock = "/lock/main";
        public static final String KEY_INFO =  "/lock/keyInfo";
        public static final String KEY_MANAGER = "/lock/keyManager";
    }

    public static class MsgModulePath {
        //消息模块首页
        public static final String Fragment_MSG = "/msg/main";

        public static final String NOTIFICATION_INFO = "/msg/notificationInfo";
    }

    public static class MallModulePath {
        //商城模块首页
        public static final String Fragment_Mall = "/mall/main";
    }

    public static class MineModulePath {
        //我的模块首页
        public static final String Fragment_Mine = "/mine/main";
    }
}
