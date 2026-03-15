package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志实体类
 */
@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id; // 主键 ID
    private String operatorId; // 操作人 ID，关联 admin 表
    private String operatorName; // 操作人姓名（冗余存储）
    private String module; // 操作模块（如会员管理、卡券管理）
    private String operation; // 操作内容（如新增会员、修改卡券）
    private String detail; // 操作详情（JSON 格式，存储前后数据）
    private String ip; // 操作 IP 地址
    private Date createTime; // 操作时间
}
