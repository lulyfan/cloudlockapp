package com.ut.base.Utils;

/**
 * author : zhouyubin
 * time   : 2018/12/19
 * desc   :
 * version: 1.0
 */
public class UTError extends RuntimeException {
    private int code = -1;

    public UTError(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public interface UTErroCode {
        int UNKNOW_ERROR = 1000;
        int BLE_CONNECT_ERROR = 1001;
    }
}
