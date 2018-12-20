package com.ut.module_msg.model;

import com.ut.database.entity.NotificationMessage;

import java.io.Serializable;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
public class NotifyCarrier implements Serializable {

    private String name;
    private List<NotificationMessage> notificationMessages;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NotificationMessage> getNotificationMessages() {
        return notificationMessages;
    }

    public void setNotificationMessages(List<NotificationMessage> notificationMessages) {
        this.notificationMessages = notificationMessages;
    }

    public String getFirstDesc() {
        if (notificationMessages != null && !notificationMessages.isEmpty()) {
            return notificationMessages.get(0).getDescription();
        }
        return "";
    }

    public String getFirstCreateTime() {
        if (notificationMessages != null && !notificationMessages.isEmpty()) {
            return notificationMessages.get(0).getCreateTime();
        }
        return "";
    }

    public int countUnRead() {
        int count = 0;
        if (notificationMessages != null && !notificationMessages.isEmpty()) {
            for (NotificationMessage nm : notificationMessages) {
                if (nm.getStatus() == 0) {
                    count++;
                }
            }
        }
        return count;
    }
}
