package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 权限实体类
 */
@Data
@TableName("permission")
public class Permission {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String name; // 权限名称（如新增会员、查看卡券）
    private String code; // 权限编码（如 member:add、coupon:view）
    private Integer type; // 权限类型：1-菜单 2-按钮
    private Long parentId; // 父权限 ID（用于构建权限树）
    private Date createTime; // 创建时间
}
