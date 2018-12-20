package com.ut.module_lock.entity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.ut.base.BaseApplication;
import com.ut.module_lock.R;

import java.io.Serializable;

/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */
public class KeyItem implements Serializable {

    private long keyId;//
    private String userName; //钥匙名字
    private String desc; //描述
    private String startTime;
    private String endTime;
    private int ruleType = 1;//类型 1永久 2限时 3单次 4循环
    private int status;//status 1,"发送中" 2,"冻结中" 3,"解除冻结中" 4,"删除中" 5,"授权中" 6,"取消授权中" 7,"修改中 8,"正常"  9,"已冻结" 10,"已删除" 11,"已失效" 12,"已过期"
    private String sender;
    private String sendTime;
    private String acceptTime;
    private String mobile;
    private String keyName;
    private String weeks;
    private String startTimeRange; //循环钥匙的启动日期
    private String endTimeRange; //循环钥匙的停止日期
    private String mac;
    private int userType;//userType;//类型(1:管理员,2:授权用户,3:普通用户)
    private int isAdmin;


    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRuleType() {
        return ruleType;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public String getStartTimeRange() {
        return startTimeRange;
    }

    public void setStartTimeRange(String startTimeRange) {
        this.startTimeRange = startTimeRange;
    }

    public String getEndTimeRange() {
        return endTimeRange;
    }

    public void setEndTimeRange(String endTimeRange) {
        this.endTimeRange = endTimeRange;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAuthorized() {
        return userType == 2;
    }

    public String getAuthorizedType() {
        if (userType == 1) {
            return "管理员";
        } else if (userType == 2) {
            return "授权用户";
        } else if (userType == 3) {
            return "普通用户";
        }
        return "";
    }

    public String typeString() {
//        1永久 2限时 3单次 4循环
        switch (ruleType) {
            case 1:
                return BaseApplication.getAppContext().getString(R.string.permanent);
            case 2:
                return BaseApplication.getAppContext().getString(R.string.limit_time);
            case 3:
                return BaseApplication.getAppContext().getString(R.string.once_time);
            case 4:
                return BaseApplication.getAppContext().getString(R.string.loop);
        }
        return "";
    }

    public String getStateString() {
//       //status 1,"发送中" 2,"冻结中" 3,"解除冻结中" 4,"删除中" 5,"授权中" 6,"取消授权中" 7,"修改中 8,"正常"  9,"已冻结" 10,"已删除" 11,"已失效" 12,"已过期"
        switch (status) {
            case 1:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_sending);
            case 2:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_frozening);
            case 3:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_cancel_frozen);
            case 4:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_delete);
            case 5:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_authorize);
            case 6:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_cancel_authorize);
            case 7:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_fix);
            case 8:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_normal);
            case 9:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_has_frozen);
            case 10:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_has_deleted);
            case 11:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_has_invailed);
            case 12:
                return BaseApplication.getAppContext().getString(R.string.lock_key_status_out_of_date);
        }
        return "";
    }

    public int stateColor() {
        return Color.parseColor(status >= 9 ? "#999999" : "#F55D54");
    }

    public boolean isInvalid() {
        return status == 11;
    }

    public Drawable getTypeDrawable() {
        if (ruleType == 0) ruleType = 1;
        int[] rids = {R.mipmap.permanent, R.mipmap.limited_time, R.mipmap.once, R.mipmap.loop};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return BaseApplication.getAppContext().getDrawable(rids[ruleType - 1]);
        }
        return null;
    }

    public boolean isForzened() {
        return status == 9;
    }
}
