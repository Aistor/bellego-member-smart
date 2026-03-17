package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("admin_role")
public class AdminRole {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String adminId;
    private String roleId;
}

