package com.bellego.domain.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StoreVo { private String id; private String name; private String code; private String address; private String phone; private Integer status; private Date createTime; }