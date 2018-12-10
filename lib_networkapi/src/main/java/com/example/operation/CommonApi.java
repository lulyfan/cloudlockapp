package com.example.operation;

import com.example.entity.base.Result;
import com.example.entity.base.Results;
import com.example.entity.data.AuthData;
import com.example.entity.data.AuthLog;
import com.example.entity.data.LockInfo;
import com.example.entity.data.LoginEntity;
import com.example.entity.data.OperateLog;
import com.example.entity.data.VersionInfo;
import com.example.i.INoLoginListener;
import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;

/**
 * Created by ZYB on 2017-03-10.
 */

public class CommonApi {
    //通用接口
    public static Call<Result<JsonElement>> doPost(Map<String, Object> map, String url) {
        return MyRetrofit.get().getCommonApiService().doPost(map, MyRetrofit.mBaseUrl + url);
    }

    //登录
    public static Call<Result<LoginEntity>> login(String mobile, String password) {
        return MyRetrofit.get().getCommonApiService().login(mobile, password);
    }

    //获取短信验证码
    public static Call<Result<Object>> getVerify(String mobile) {
        return MyRetrofit.get().getCommonApiService().getVerify(mobile);
    }

    //注册
    public static Call<Result<Object>> regist(String mobile, String password, String verifyCode) {
        return MyRetrofit.get().getCommonApiService().regist(mobile, password, verifyCode);
    }

    //重置密码
    public static Call<Result<Object>> resetPassword(String mobile, String password, String verifyCode) {
        return MyRetrofit.get().getCommonApiService().resetPassword(mobile, password, verifyCode);
    }

    //获取锁列表
    public static Call<Results<LockInfo>> fetchLocks() {
        return MyRetrofit.get().getCommonApiService().fetchLocks();
    }

    //获取用户信息
    public static Call<Result<LoginEntity>> fetchUser(String mobile) {
        return MyRetrofit.get().getCommonApiService().fetchUser(mobile);
    }

    //使用旧密码重置密码
    public static Call<Result<Object>> resetPasswordByOld(String oldPassword, String newPassword) {
        return MyRetrofit.get().getCommonApiService().resetPasswordByOld(oldPassword, newPassword);
    }

    //用户重设置昵称
    public static Call<Result<Object>> userRename(String name) {
        return MyRetrofit.get().getCommonApiService().userRename(name);
    }

    //解绑锁
    public static Call<Result<Object>> unbindLock(String mac) {
        return MyRetrofit.get().getCommonApiService().unbindLock(mac);
    }

    //绑定锁
    public static Call<Result<Object>> bindLock(String mac, String name, String type, String qrcode) {
        return MyRetrofit.get().getCommonApiService().bindLock(mac, name, type, qrcode);
    }

    //修改锁备注
    public static Call<Result<Object>> lockRename(String mac, String name) {
        return MyRetrofit.get().getCommonApiService().lockRename(mac, name);
    }

    //获取锁操作记录
    public static Call<Results<OperateLog>> fetchLockOperationLog(String startTime, String endTime) {
        return MyRetrofit.get().getCommonApiService().fetchLockOperationLog(startTime, endTime);
    }

    //添加授权
    public static Call<Result<Object>> addAuth(String mobile, String mac, String startTime, String endTime) {
        return MyRetrofit.get().getCommonApiService().addAuth(mobile, mac, startTime, endTime);
    }

    //删除授权
    public static Call<Result<Object>> removeAuth(String mobile, String mac) {
        return MyRetrofit.get().getCommonApiService().removeAuth(mobile, mac);
    }

    //获取授权列表
    public static Call<Results<AuthData>> listAuthUser(String mac) {
        return MyRetrofit.get().getCommonApiService().listAuthUser(mac);
    }

    //获取授权记录列表
    public static Call<Results<AuthLog>> listAuthLog(String startTime, String endTime) {
        return MyRetrofit.get().getCommonApiService().listAuthLog(startTime, endTime);
    }

    //开关锁(操作锁)
    public static Call<Result<Object>> changeLockStatus(String mac, String status) {
        return MyRetrofit.get().getCommonApiService().changeLockStatus(mac, status);
    }

    //根据mac获取授权记录
    public static Call<Results<AuthLog>> listAuthLogByMac(String mac, String startTime, String endTime) {
        return MyRetrofit.get().getCommonApiService().listAuthLogByMac(mac, startTime, endTime);
    }

    //根据mac获取锁的操作记录
    public static Call<Results<OperateLog>> listOperLogByMac(String mac, String startTime, String endTime) {
        return MyRetrofit.get().getCommonApiService().listOperLogByMac(mac, startTime, endTime);
    }

    //添加密保问题校验
    public static Call<Result<Object>> verifyByQuestion(String lockId, String oldPwd, String newPwd, String question, String answer, String alarmPwd, String alarmPhone) {
        return MyRetrofit.get().getCommonApiService().verifyByQuestion(lockId, oldPwd, newPwd, question, answer, alarmPwd, alarmPhone);
    }

    //添加手机校验
    public static Call<Result<Object>> verifyByCheckPhone(String lockId, String oldPwd, String newPwd, String checkPhone, String alarmPwd, String alarmPhone) {
        return MyRetrofit.get().getCommonApiService().verifyByCheckPhone(lockId, oldPwd, newPwd, checkPhone, alarmPwd, alarmPhone);
    }

    //通过密保问题修改密码
    public static Call<Result<Object>> updatePwdByQuestion(String lockId, String newPwd, String question, String answer, String alarmPwd, String alarmPhone) {
        return MyRetrofit.get().getCommonApiService().updatePwdByQuestion(lockId, newPwd, question, answer, alarmPwd, alarmPhone);
    }

    //通过信任手机修改密码
    public static Call<Result<Object>> updatePwdByCheckPhone(String lockId, String newPwd, String verifyCode, String alarmPwd, String alarmPhone) {
        return MyRetrofit.get().getCommonApiService().updatePwdByCheckPhone(lockId, newPwd, verifyCode, alarmPwd, alarmPhone);
    }

    //通过原密码修改密码
    public static Call<Result<Object>> updatePwdByOldPwd(String lockId, String newPwd, String oldPwd, String alarmPwd, String alarmPhone) {
        return MyRetrofit.get().getCommonApiService().updatePwdByOldPwd(lockId, newPwd, oldPwd, alarmPwd, alarmPhone);
    }

    //校验密码
    public static Call<Result<Object>> checkLockPwd(String lockId, String pwd) {
        return MyRetrofit.get().getCommonApiService().checkLockPwd(lockId, pwd);
    }

    //发送验证码
    public static Call<Result<Object>> sendVerifyCode(String mobile) {
        return MyRetrofit.get().getCommonApiService().sendVerifyCode(mobile);
    }

    //分页获取锁操作记录
    public static Call<Results<OperateLog>> fetchLockOperationLogInPage(String startTime, String endTime, String pageStart, String pageSize) {
        return MyRetrofit.get().getCommonApiService().pageOperLog(startTime, endTime, pageStart, pageSize);
    }

    //根据mac分页获取锁操作记录
    public static Call<Results<OperateLog>> fetchOperLogInPageByMac(String startTime, String endTime, String pageStart, String pageSize, String mac) {
        return MyRetrofit.get().getCommonApiService().pageOperLogByMac(startTime, endTime, pageStart, pageSize, mac);
    }

    //分页获取锁授权记录
    public static Call<Results<OperateLog>> fetchLockAuthLogInPage(String startTime, String endTime, String pageStart, String pageSize) {
        return MyRetrofit.get().getCommonApiService().pageAuthLog(startTime, endTime, pageStart, pageSize);
    }

    //根据mac分页获取锁授权记录
    public static Call<Results<OperateLog>> fetchAuthInPageByMac(String startTime, String endTime, String pageStart, String pageSize, String mac) {
        return MyRetrofit.get().getCommonApiService().pageAuthLogByMac(startTime, endTime, pageStart, pageSize, mac);
    }

    //获取版本号
    public static Call<Result<VersionInfo>> getVersionInfo(int typeId) {
        return MyRetrofit.get().getCommonApiService().getVersionInfo(typeId);
    }

    //检查是否在授权内
    public static Call<Result<Boolean>> isAuth(String mac) {
        return MyRetrofit.get().getCommonApiService().isAuth(mac);
    }

    //检查是否登录
    public static Call<Object> ping() {
        return MyRetrofit.get().getCommonApiService().ping();
    }

    /**
     * 设置未登录错误监听
     *
     * @param iNoLoginListener
     */
    public static void setNoLoginListener(INoLoginListener iNoLoginListener) {
        MyRetrofit.get().setNoLoginListener(iNoLoginListener);
    }
}
