package com.ut.unilink.util;

import org.junit.Test;

public class Base64Test {

    @Test
    public void test() {
        byte[] data = Log.getBytes("adef34534ad345235");
        System.out.println(Log.toUnsignedHex(data));
        String str = Base64.encode(data);
        System.out.println(Log.toUnsignedHex(Base64.decode(str)));
    }
}
