package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String code;
    private Integer type;
    private String parentId;
    private Date createTime;
}

