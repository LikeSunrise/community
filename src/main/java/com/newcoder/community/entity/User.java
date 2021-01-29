package com.newcoder.community.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {

    private int id;
    private String username;
    private String password;
    private String salt;//用户注册时，系统用来和用户密码进行组合而生成的随机数值，称作salt值，通称为加盐值
    private String email;

    private int type;
    private int status;

    private String activationCode;//用户注册时用的激活码
    private String headerUrl;

    private Date createTime;

}
