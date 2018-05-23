package io.github.piaozaiguang.blockchain.support.utils;

import org.junit.Test;

public class HashUtilTest {

    @Test
    public void applySha256() {
        System.out.println(HashUtil.applySha256("123"));
    }
}
