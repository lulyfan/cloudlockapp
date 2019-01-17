package com.ut.unilink.util;

import java.util.HashMap;
import java.util.Map;

public class Test {

    @org.junit.Test
    public void test() {
        Map<String, Integer> map = new HashMap<>();
        int i = map.get("asd");
        System.out.println("i:" + i);
    }
}
