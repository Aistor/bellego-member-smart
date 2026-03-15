package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色权限关联实体类
 */
@Data
@TableName("role_permission")
public class RolePermission {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String roleId; // 角色 ID，关联 role 表
    private String permissionId; // 权限 ID，关联 permission 表
}
