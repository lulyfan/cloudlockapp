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


    /**
     * 设置未登录错误监听
     *
     * @param iNoLoginListener
     */
    public static void setNoLoginListener(INoLoginListener iNoLoginListener) {
        MyRetrofit.get().setNoLoginListener(iNoLoginListener);
    }
}
