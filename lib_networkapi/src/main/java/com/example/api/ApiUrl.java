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

    //解除冻结钥匙
    String unfrozenKey = "/api/key/unFrozenKey?json";

    //冻结钥匙
    String frozenKey = "/api/key/frozenKey?json";

    //删除钥匙
    String deleteKey = "/api/key/delKey?json";

    //根据钥匙查找锁日志
    String queryLogsByKey = "api/log/pageKeyLog?json";

    //上传用户头像
    String uploadImg = "api/user/uploadImg";

    //修改用户头像
    String uploadHead = "api/user/uploadHead";

    //修改用户昵称
    String editUserNmae = "api/user/updateName";

    //修改配置启用状态
    String changeUserConfig = "api/user/changeUserConfig";

    //获取用户信息
    String getUserInfo = "api/user/getUserInfo?json";

}
