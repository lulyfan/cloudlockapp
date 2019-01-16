package com.ut.unilink.util;

import org.junit.Test;

public class BitUtilTest {

    @Test
    public void test1() {
        int data = 0xC0;
        data = BitUtil.bit(data, 5);
        System.out.println(String.format("%x", data));
    }

    @Test
    public void test2() {
        int data = 0x00;
        data = BitUtil.set1(data, 3);
        System.out.println(String.format("%x", data));
    }

    @Test
    public void test3() {
        int data = 0xFF;
        data = BitUtil.set0(data, 6);
        System.out.println(String.format("%2x", data));
    }

    @Test
    public void test4() {
        int data = 0xF0;
        data = BitUtil.set(data, 7, 0);
        System.out.println(String.format("%2x", data));
    }
}
