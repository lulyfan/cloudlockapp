package com.example.api;

/**
 * author : zhouyubin
 * time   : 2018/09/11
 * desc   :
 * version: 1.0
 */
public interface ApiUrl {
    String PREFIX = "/api";
    String SUFFIX = "?json";

    //登录
    String loginUrl = "/api/user/login?json";

    //注册
    String registerUrl = "/api/user/registerUser?json";

    //获取注册验证码
    String getRegisterVerifyCode = "/api/user/getRegisterVeriCode?json";

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

    //重置密码
    String resetPassword = "api/user/resetPwd?json";

    //查找锁日志
    String queryLogsByLock = "api/log/pageLockLog";

    //用户下锁日志列表
    String queryLogsByUser = "api/log/pageUserLog?json";

    //获取用户的管理员锁
    String pageAdminLock = "api/lock/pageAdminLock";

    //获取锁分组
    String getGroup = "api/lockGroup/getGroup";

    //添加锁组
    String addGroup = "api/lockGroup/newGroup";

    //获取锁组内信息
    String getLockInfoFromGroup = "api/lockGroup/getLockInfoFromGroup";

    //删除锁分组
    String delGroup = "api/lockGroup/delGroup";

    //修改锁名称
    String updateGroupName = "api/lockGroup/updateGroupName";

    //获取忘记密码验证码
    String getForgetPwdVerifyCode = "api/user/getForgetPwdVeriCode?json";

    //修改钥匙规则
    String editKey = "/api/key/modifyKeyRule?json";

    //转移锁权限
    String changeLockAdmin = "api/lock/changeLockAdmin";

    //发送短信验证码
    String sendMobileCode = "api/user/sendMobileCode";

    //获取转移管理员的验证码
    String getChangeAdminCode = "api/lock/getChangeAdminCode";

    //退出登录
    String logout = "api/user/logout";

    //获取锁用户
    String pageLockUser = "api/lock/pageLockUser";

    //根据手机号获取用户信息
    String getUserInfoByMobile = "api/user/getUserInfoByMobile";

    //获取发给
    String pageLockUserKey = "api/lock/pageLockUserKey";

    //绑定锁
    String bindLock = PREFIX + "/lock/addAdminLock" + SUFFIX;

    //查询锁信息（蓝牙搜索页面）
    String getLockInfo = PREFIX + "/lock/getLockInfo" + SUFFIX;

    //删除管理员钥匙
    String delAdminLock = PREFIX + "/lock/delAdminLock" + SUFFIX;

    //修改锁名称
    String updateLockName = PREFIX + "/lock/updateLockName" + SUFFIX;

    //获取用户锁列表
    String pageUserLock = PREFIX + "/lock/pageUserLock" + SUFFIX;

    //获取消息列表
    String getMessage = "api/lockMessage/getMessage?json";

    //校验钥匙申请的处理状态
    String checkKeyStatus = "api/key/checkStatus?json";
}
