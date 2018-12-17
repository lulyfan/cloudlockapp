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

    //获取钥匙申请列表
    String getKeyApplyList = "api/key/list?json";

    //申请钥匙
    String applyKey = "api/key/add?json";

    //忽略申请
    String ignoreApply = "api/key/ignore?json";

    //用户锁下的钥匙列表
    String pageKey = "api/key/pageKey?json";

    //上传用户头像
    String uploadImg = "api/user/uploadImg";

    //修改用户头像
    String uploadHead = "api/user/uploadHead";
}
