package com.ut.module_msg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * author : chenjiajun
 * time   : 2018/11/30
 * desc   :
 */
public class ApplyMessage implements Serializable {

    private String url;
    private String name;
    private String hint;
    private String time;
    private String applicant;
    private int keyType;

    private String message;

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String keyTypeString() {
        switch (keyType) {
            case 0:
                return "单次";
            case 1:
                return "限时";
            case 2:
                return "循环";
            case 3:
                return "永久";
        }
        return "";
    }
}
