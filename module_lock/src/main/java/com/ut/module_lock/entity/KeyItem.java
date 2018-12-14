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
    private int status;//状态 0：失效，1，生效，2：待接受
    private String sender;
    private String sendTime;
    private String acceptTime;
    private boolean isAuthorized; //是否授权
    private String authorizedType;
    private String startDate;//循环钥匙的启动日期
    private String endDate;//循环钥匙的停止日期
    private String mobile;
    private String keyName;
    private boolean isAdmin;
    private int weeks;
    private String startTimeRange;
    private String endTimeRange;

    public long getKeyId() {
        return keyId;
    }

    public void setKeyId(long keyId) {
        this.keyId = keyId;
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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getWeeks() {
        return weeks;
    }

    public void setWeeks(int weeks) {
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

    public void setRuleType(int type) {
        this.ruleType = type;
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

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getAuthorizedType() {
        return authorizedType;
    }

    public void setAuthorizedType(String authorizedType) {
        this.authorizedType = authorizedType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
//        0：失效，1，生效，2：待接受
        switch (status) {
            case 0:
                return "已失效";

            case 2:
                return "待接受";
        }
        return "";
    }

    public int stateColor() {
        return Color.parseColor(status == 0 ? "#999999" : "#F55D54");
    }

    public boolean isInvalid() {
        return status == 0;
    }

    public Drawable getTypeDrawable() {
        if(ruleType == 0) ruleType = 1;
        int[] rids = { R.mipmap.permanent, R.mipmap.limited_time, R.mipmap.once, R.mipmap.loop};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return BaseApplication.getAppContext().getDrawable(rids[ruleType - 1]);
        }
        return null;
    }
}
