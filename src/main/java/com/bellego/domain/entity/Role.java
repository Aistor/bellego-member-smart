package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 角色实体类
 */
@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String name; // 角色名称（如超级管理员）
    private String code; // 角色编码（如 ADMIN、OPERATOR）
    private String description; // 角色描述
    private Date createTime; // 创建时间
}
