package com.ut.module_msg.model;

import java.io.Serializable;

/**
 * author : chenjiajun
 * time   : 2018/11/26
 * desc   :
 */
public class NotificationMessage implements Serializable {

    private long id;
    private String title;
    private String content;
    private String icon;
    private String time;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
