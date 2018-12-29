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
        public static final String KEY_INFO = "/lock/keyInfo";
        public static final String KEY_MANAGER = "/lock/keyManager";
        public static final String OPERATION_RECORD = "/lock/operationRecord";
        public static final String EDIT_NAME = "/lock/editKeyName";
        public static final String EDIT_LIMITED_TIME = "/lock/editLimitedTime";
        public static final String CHOOSE_LOCK_GROUP = "/lock/chooseLockGroup";
        public static final String EDIT_LOOP_TIME = "/lock/editLoopTime";
        public static final String LOCK_SETTING = "/lock/lockSetting";
        public static final String APPLY_KEY = "/lock/applyKey";
        //锁详情
        public static final String LOCK_DETAIL = "/lock/detail";


    }

    public static class BaseModulePath {
        public static final String GRANTPERMISSION = "/base/sendKey";
    }

    public interface LockModuleExtraKey {
        String Extra_lock_detail = "extra_lock_key";
        String EXTRA_LOCK_SENDKEY_MAC = "extra_lock_sendkey";
        String EXTRA_LOCK_SENDKEY_MOBILE = "extra_lock_sendKey_mobile";
        String EXTRA_LOCK_SENDKEY_RULER_TYPE = "extra_lock_sendKey_rulerType";
    }

    public interface LoginModuleAction {
        String action_login_resetPW = "action_login_resetPW";
    }

    public static class MsgModulePath {
        //消息模块首页
        public static final String Fragment_MSG = "/msg/main";

        public static final String NOTIFICATION_INFO = "/msg/notificationInfo";
        public final static String APPLY_INFO = "/msg/applyInfo";


        public interface IntentKey {
            String EXTRA_MESSAGE_INFO = "extra_message_info";
        }
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
