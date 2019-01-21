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
    String uploadImg = "api/user/uploadImg?json";

    //修改用户头像
    String uploadHead = "api/user/uploadHead?json";

    //修改用户昵称
    String editUserNmae = "api/user/updateName?json";

    //修改配置启用状态
    String changeUserConfig = "api/user/changeUserConfig?json";

    //获取用户信息
    String getUserInfo = "api/user/getUserInfo?json";

    //重置密码
    String resetPassword = "api/user/resetPwd?json";

    //查找锁日志
    String queryLogsByLock = "api/log/pageLockLog?json";

    //用户下锁日志列表
    String queryLogsByUser = "api/log/pageUserLog?json";

    //获取用户的管理员锁
    String pageAdminLock = "api/lock/pageAdminLock?json";

    //获取锁分组
    String getGroup = "api/lockGroup/getGroup?json";

    //添加锁组
    String addGroup = "api/lockGroup/newGroup?json";

    //获取锁组内信息
    String getLockInfoFromGroup = "api/lockGroup/getLockInfoFromGroup?json";

    //删除锁分组
    String delGroup = "api/lockGroup/delGroup?json";

    //修改锁名称
    String updateGroupName = "api/lockGroup/updateGroupName?json";

    //获取忘记密码验证码
    String getForgetPwdVerifyCode = "api/user/getForgetPwdVeriCode?json";

    //修改钥匙规则
    String editKey = "/api/key/modifyKeyRule?json";

    //转移锁权限
    String changeLockAdmin = "api/lock/changeLockAdmin?json";

    //发送短信验证码
    String sendMobileCode = "api/user/sendMobileCode?json";

    //获取转移管理员的验证码
    String getChangeAdminCode = "api/lock/getChangeAdminCode?json";

    //退出登录
    String logout = "api/user/logout?json";

    //获取锁用户
    String pageLockUser = "api/lock/pageLockUser?json";

    //根据手机号获取用户信息
    String getUserInfoByMobile = "api/user/getUserInfoByMobile?json";

    //获取发给
    String pageLockUserKey = "api/lock/pageLockUserKey?json";

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

    //判断是否充许授权
    String isAuth = PREFIX + "/key/isAuth" + SUFFIX;

    //添加日志
    String addLog = PREFIX + "/log/addLog" + SUFFIX;

    //设置是否触摸开
    String setCanOpen = PREFIX + "/key/setCanOpen" + SUFFIX;

    //获取消息列表
    String getMessage = "api/lockMessage/getMessage?json";

    //校验钥匙申请的处理状态
    String checkKeyStatus = "api/key/checkStatus?json";

    //发送钥匙
    String sendKey = "api/lock/sendKey?json";

    //添加锁进分组
    String addLockIntoGroup = "api/lockGroup/addLockIntoGroup?json";

    //从分组中移除锁
    String delLockFromGroup = "api/lockGroup/delLockFromGroup?json";

    //验证用户
    String verifyUserPwd = "api/user/veriPassword?json";

    //删除管理员锁
    String deleteAdminLock = "api/lock/delAdminLock?json";

    //修改锁名称
    String editLockName = "api/lock/updateLockName?json";

    //将锁从一个分组移动到另一个分组
    String changeLockGroup = "api/lockGroup/changeGroup?json";

    //授权钥匙
    String toAuth = "api/key/toAuth?json";

    //取消授权钥匙
    String cancelAuth = "api/key/cancelAuth?json";

    //清空钥匙
    String clearAllKeys = "api/key/clearKey?json";

    //获取某把锁的所有消息（二级消息菜单）
    String getLockMessageInfos = "api/lockMessage/getMessageDetail?json";

    //读消息
    String readMessages = "api/lockMessage/readMessage?json";

    //版本更新
    String updateAppVersion = "/api/version/getNewVersion?json&typeId=1004";

    //修改钥匙名称
    String editKeyName = "api/key/updateKeyName";

    //验证短信验证码（除注册和忘记密码的发送验证码）
    String verifyPhoneCode = "api/user/veriMobileCode?json";

    //获取后台时间
    String checkTime = "api/innerLock/checkTime?json";

    //更新钥匙数据
    String updateKeyInfo = PREFIX + "/innerLock/updateKeyInfo" + SUFFIX;

    //获取门锁钥匙信息
    String getDeviceKeyListByType = PREFIX + "/innerLock/getListByType" + SUFFIX;

    //初始化门锁钥匙信息
    String initLockKey = PREFIX + "/innerLock/init" + SUFFIX;

    //新增门锁日志
    String insertInnerLockLog = PREFIX + "/innerLock/insertInnerLockLog" + SUFFIX;

    //验证码登录
    String loginByVerifyCodeWithDeviceCode = "api/user/loginByVerifyCodeWithDeviceCode?json";

    //删除钥匙
    String delKeyInfo = PREFIX + "/innerLock/delKeyInfo" + SUFFIX;
}
