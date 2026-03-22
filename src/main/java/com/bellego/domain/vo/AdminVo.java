package com.bellego.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class AdminVo {
    private String id;
    private String username;
    private String realName;
    private String phone;
    private Integer status;
    private Date createTime;
}