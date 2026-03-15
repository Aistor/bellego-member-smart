package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 管理员角色关联实体类
 */
@Data
@TableName("admin_role")
public class AdminRole {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String adminId; // 管理员 ID，关联 admin 表
    private String roleId; // 角色 ID，关联 role 表
}
