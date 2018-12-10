package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/07/09
 * desc   :
 * version: 1.0
 */
public class LockInfo {

    /**
     * id : 15
     * name : 公寓门锁
     * type : 41025
     * mac : 54:6C:0E:7B:2A:3C
     * qrcode :
     * authType : 0
     */

    private int id;
    private String name;
    private String type;
    private String mac;
    private String qrcode;
    private int verified;
    private int alarmed;
    private String question;
    private String checkPhone;
    private String alarmPhone;
    private int authType;
    private String lockType;


    private boolean isAutoOpen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
    }

    public boolean isAutoOpen() {
        return isAutoOpen;
    }

    public void setAutoOpen(boolean autoOpen) {
        isAutoOpen = autoOpen;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCheckPhone() {
        return checkPhone;
    }

    public void setCheckPhone(String checkPhone) {
        this.checkPhone = checkPhone;
    }

    public int getAlarmed() {
        return alarmed;
    }

    public void setAlarmed(int alarmed) {
        this.alarmed = alarmed;
    }

    public String getAlarmPhone() {
        return alarmPhone;
    }

    public void setAlarmPhone(String alarmPhone) {
        this.alarmPhone = alarmPhone;
    }

    @Override
    public String toString() {
        return "LockInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", mac='" + mac + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", verified=" + verified +
                ", alarmed=" + alarmed +
                ", question='" + question + '\'' +
                ", checkPhone='" + checkPhone + '\'' +
                ", alarmPhone='" + alarmPhone + '\'' +
                ", authType=" + authType +
                ", isAutoOpen=" + isAutoOpen +
                '}';
    }
}
