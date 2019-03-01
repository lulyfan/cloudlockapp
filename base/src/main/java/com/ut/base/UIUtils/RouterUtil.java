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

        public static final String LOCK_DEVICE_KEY = "/lock/lockKey";

        //设备钥匙详情
        public static final String LOCK_DEVICE_KEY_DETAIL = "/lock/devicekeyDetail";

        public static final String LOCK_DEVICE_KEY_PERMISSION = "/lock/devicekeyPermission";
        public static final String TIME_ADJUST = "/lock/timeAdjust";
    }

    public static class BaseModulePath {
        public static final String SEND_KEY = "/base/sendKey";
        public static final String SAFEVERIFY = "/base/safeVerify";
        public static final String WEB = "/base/web";
    }

    public interface LockModuleExtraKey {
        String EXTRA_LOCK_KEY = "extra_lock_key";
        String EXTRA_LOCK_SENDKEY_MAC = "extra_lock_sendkey";
        String EXTRA_LOCK_KEY_USERTYPE = "extra_lock_sendkey_usertype";
        String EXTRA_LOCK_SENDKEY_MOBILE = "extra_lock_sendKey_mobile";
        String EXTRA_LOCK_SENDKEY_RULER_TYPE = "extra_lock_sendKey_rulerType";
        String EXTRA_LOCK_DEVICE_KEY = "extra_lock_device_key";

        String EDIT_NAME_TITLE = "edit_name_title";
        String IS_LOCK = "is_lock";
        String NAME_TYPE = "name_type";//编辑名称页的名称类型
        String MAC = "mac";
        String KEY_ID = "key_id";
        String NAME = "name";

        String EXTRA_LOCK_CURRENT_GROUPID = "extra_lock_current_groupid";
        String EXTRA_CANT_EDIT_PHONE =  "extra_cant_edit_phone";
    }

    public interface LockModuleConstParams {
        int NAMETYPE_KEY = 0;
        int NAMETYPE_LOCK = 1;
        int NAMETYPE_DEVICE_KEY = 3;
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


    public static class BrocastReceiverAction {
        public static final String ACTION_RELOAD_WEB_DEVICEKEY = "action_reload_web_devicekey";

        public static final String ACTION_FINISH_MAINACTIVITY = "action_finish_mainactivity";
    }


}
