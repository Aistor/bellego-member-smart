package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private Integer type;
    private BigDecimal couponValue;
    private BigDecimal useCondition;
    private Integer stock;
    private Integer totalIssued;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;
}

