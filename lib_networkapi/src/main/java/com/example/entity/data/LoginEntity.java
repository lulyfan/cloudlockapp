package com.example.entity.data;

/**
 * author : zhouyubin
 * time   : 2018/06/28
 * desc   :
 * version: 1.0
 */
public class LoginEntity {
    /**
     * id : 4
     * mobile : 13534673710
     * name : 13534673710
     */

    private int id;
    private String mobile;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LoginEntity{" +
                "id=" + id +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
