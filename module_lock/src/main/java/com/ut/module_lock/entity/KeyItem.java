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

    private String caption; //钥匙名字
    private String desc; //描述
    private String startTime;
    private String endTime;
    private int type;//类型 0：单次， 1：限时， 2：循环，3：永久
    private int state;//状态 0：失效，1，生效，2：待接受
    private String sender;
    private String sendTime;
    private String acceptTime;
    private boolean isAuthorized; //是否授权
    private String authorizedType;
    private String startDate;//循环钥匙的启动日期
    private String endDate;//循环钥匙的停止日期

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
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
        //0：单次， 1：限时， 2：循环，3：永久
        switch (type) {
            case 0:
                return BaseApplication.getAppContext().getString(R.string.once_time);
            case 1:
                return BaseApplication.getAppContext().getString(R.string.limit_time);
            case 2:
                return BaseApplication.getAppContext().getString(R.string.loop);
            case 3:
                return BaseApplication.getAppContext().getString(R.string.permanent);
        }
        return "";
    }

    public String getStateString() {
//        0：失效，1，生效，2：待接受
        switch (state) {
            case 0:
                return "已失效";

            case 2:
                return "待接受";
        }
        return "";
    }

    public int stateColor() {
        return Color.parseColor(state == 0 ? "#999999" : "#F55D54");
    }

    public boolean isInvalid() {
        return state == 0;
    }

    public Drawable getTypeDrawable() {
        int[] rids = {R.mipmap.once, R.mipmap.limited_time, R.mipmap.loop, R.mipmap.permanent};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return BaseApplication.getAppContext().getDrawable(rids[type]);
        }
        return null;
    }
}
