package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("operation_log")
public class OperationLog {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String operatorId;
    private String operatorName;
    private String module;
    private String operation;
    private String detail;
    private String ip;
    private Date createTime;
}

