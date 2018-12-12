package com.example.api;

/**
 * author : zhouyubin
 * time   : 2018/09/11
 * desc   :
 * version: 1.0
 */
public interface ApiUrl {
    //登录
    String loginUrl = "/api/user/login?json&&appid=1";

    //注册
    String registerUrl = "/api/user/registerUser?json&&appid=1";

    //获取注册验证码

    String getRegisterVerifyCode = "/api/user/getRegisterVeriCode?json&&appid=1";

}
