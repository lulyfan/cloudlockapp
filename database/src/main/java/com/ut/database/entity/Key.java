package com.ut.database.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.io.Serializable;


/**
 * author : chenjiajun
 * time   : 2018/11/29
 * desc   :
 */

@Entity(tableName = "ut_key")
public class Key implements Serializable, Cloneable {
    @PrimaryKey
    private long keyId;//
    private String userName; //钥匙名字
    private String desc; //描述
    private String startTime;
    private String endTime;
    private int ruleType = 1;//类型 1永久 2限时 3单次 4循环
    private int status;//status 1,"发送中" 2,"冻结中" 3,"解除冻结中" 4,"删除中" 5,"授权中" 6,"取消授权中" 7,"修改中 8,"正常"  9,"已冻结" 10,"已删除" 11,"已失效" 12,"已过期"
    private String sendTime;
    private String receiveTime;
    private String mobile;
    private String keyName;
    private String weeks;
    private String sendUser;
    private String startTimeRange; //循环钥匙的启动日期
    private String endTimeRange; //循环钥匙的停止日期
    private String mac;
    private int userType;//userType;//类型(1:管理员,2:授权用户,3:普通用户)

    private String ruleTypeString;
    private int ruleTypeDrawableId;
    private String statusString;


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


    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
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

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
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

    public boolean isInvalid() {
        return status == 11;
    }

    public String userNameOrMobile() {
        if (TextUtils.isEmpty(sendUser)) {
            return mobile;
        }
        return sendUser;
    }

    public boolean isFrozened() {
        return status == 9;
    }


    public int statusColor() {
        return Color.parseColor(status >= 9 ? "#999999" : "#F55D54");
    }


    public String getRuleTypeString() {
        return ruleTypeString;
    }

    public void setRuleTypeString(String ruleTypeString) {
        this.ruleTypeString = ruleTypeString;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }

    public int getRuleTypeDrawableId() {
        return ruleTypeDrawableId;
    }

    public void setRuleTypeDrawableId(int ruleTypeDrawableId) {
        this.ruleTypeDrawableId = ruleTypeDrawableId;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
