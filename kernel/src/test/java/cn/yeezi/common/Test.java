package cn.yeezi.common;

import cn.hutool.crypto.SecureUtil;

public class Test {
    public static void main(String[] args) {
        //生成md5密码
        System.out.println(SecureUtil.md5("lixue@2025"));
    }
}
