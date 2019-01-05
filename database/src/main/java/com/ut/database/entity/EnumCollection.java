package com.ut.database.entity;

/**
 * author : zhouyubin
 * time   : 2018/12/18
 * desc   :
 * version: 1.0
 */
public class EnumCollection {

    public enum KeyStatus {
        OTHER,//其他
        SENDING,//"发送中",
        FREEZING,//"冻结中"),
        RELEASE_FREEZE,//"解除冻结中"),
        DELETING,//"删除中"),
        AUTHORITYING,//"授权中"),
        CANCEL_AUTHORITY,//"取消授权中"),
        UPDATING,//"修改中"),
        NORMAL,//"正常"),
        HAS_FREEZE,//"已冻结"),
        HAS_DELETE,//"已删除"),
        HAS_INVALID,//"已失效"),
        HAS_OVERDUE;//"已过期");

        public static boolean isKeyValue(int keyStatus) {
            return keyStatus == SENDING.ordinal() || keyStatus == AUTHORITYING.ordinal()
                    || keyStatus == NORMAL.ordinal();
        }
    }

    public enum BindStatus {
        UNBIND,//未有人绑定
        HASBIND,//当前用户绑定
        OTHERBIND_HASKE,//其他用户绑定,当前用户有钥匙
        OTHERBIND_NOKEY//其他用户绑定，当前用户没有钥匙
    }


    //锁用户类型
    public enum UserType {
        OTHER,
        ADMIN,//管理员
        AUTH,//授权用户
        NORMAL//普通用户
    }

    //钥匙规则类型
    public enum KeyRuleType {
        OTHER,
        FOREVER,//永久
        TIMELIMIT,//限时
        ONCE,//当次
        CYCLE//循环
    }

    public enum LockType{
        SMARTLOCK(0xA010);
        int type;

        LockType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
