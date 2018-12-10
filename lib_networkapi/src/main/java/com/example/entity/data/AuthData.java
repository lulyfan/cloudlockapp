package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/07/17
 * desc   :
 * version: 1.0
 */
public class AuthData {

    /**
     * mobile : 13564892621
     * createTime : 2018-06-15 12:30:16
     */

    private String mobile;
    private String createTime;
    private String startTime;
    private String endTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    @Override
    public String toString() {
        return "AuthData{" +
                "mobile='" + mobile + '\'' +
                ", createTime='" + createTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
