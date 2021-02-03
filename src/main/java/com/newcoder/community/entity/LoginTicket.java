package com.newcoder.community.entity;

import lombok.Data;

import java.util.Date;

/**
 * 登陆凭证
 */
@Data
public class LoginTicket {

    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
