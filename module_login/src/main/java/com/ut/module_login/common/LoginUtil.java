package com.ut.module_login.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginUtil {

    public static boolean isPhone(String phone) {
        phone = phone.replace(" ", "");
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    public static boolean isPassword(String password) {
        if (password.length() < 6)
            return false;
        String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$";
        Pattern p = Pattern.compile(reg);
        return p.matcher(password).matches();
    }
}
