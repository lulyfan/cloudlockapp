package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/07/27
 * desc   :
 * version: 1.0
 */
public class VersionInfo {
    /**
     * fileSize : 63.16720008850098
     * description : 云锁Android App
     * fileUrl : 63.16720008850098
     * time : 2018-07-11 11:08:19
     * version : 0.10
     * extProps :
     */

    private double fileSize;//文件大小
    private String description;
    private String fileUrl;
    private String time;
    private String version;
    private String versionName;

    public double getFileSize() {
        return fileSize;
    }


    public String getDescription() {
        return description;
    }


    public String getFileUrl() {
        return fileUrl;
    }


    public String getTime() {
        return time;
    }


    public String getVersion() {
        return version;
    }


    public String getExtProps() {
        return versionName;
    }


    @Override
    public String toString() {
        return "VersionInfo{" +
                "fileSize=" + fileSize +
                ", description='" + description + '\'' +
                ", fileUrl=" + fileUrl +
                ", time='" + time + '\'' +
                ", version='" + version + '\'' +
                ", extProps='" + versionName + '\'' +
                '}';
    }
}
