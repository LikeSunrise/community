package com.newcoder.community.utils;

/**
 * 常量接口
 */
public interface CommunityConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;


    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;


    /**
     * 默认状态下的，登录凭证过期时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * “记住我”状态下的，登录凭证过期时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;


    /**
     *试题类型：帖子
     */
    int ENTITY_TYPE_POST = 1;


    /**
     *实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
