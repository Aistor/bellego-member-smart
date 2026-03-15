package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 管理员实体类
 */
@Data
@TableName("admin")
public class Admin {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String username; // 登录用户名（唯一）
    private String password; // 密码
    private String realName; // 真实姓名
    private String phone; // 联系电话
    private Integer status; // 状态：0-禁用 1-启用
    private Date createTime; // 创建时间
}
