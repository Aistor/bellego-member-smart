package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("store")
public class Store {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String code;
    private String address;
    private String phone;
    private Integer status;
    private Date createTime;
}

