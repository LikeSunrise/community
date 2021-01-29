package com.newcoder.community.utils;

import com.sun.mail.smtp.DigestMD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtils {

    /**
     * 生成随机字符串
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5加密，注册的时候提交的密码是明文，必须加密否则有泄露的危险
     * MD5特点：
     * 1、只能加密不能解密
     * 2、同一个原始串，加密结果永远是同一个
     * 3、基于1,2，MD5很容易被破解
     * 解决办法：原始串加上一个串（随机串），再MD5加密
     */

    public static String md5(String origin){
        if(StringUtils.isBlank(origin)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(origin.getBytes());//springboot自带的md5算法的封装好了的方法
    }

}
