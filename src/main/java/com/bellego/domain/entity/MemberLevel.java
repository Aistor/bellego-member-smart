package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("member_level")
public class MemberLevel {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private Integer level;
    private Integer minPoints;
    private BigDecimal minConsumption;
    private BigDecimal discountRate;
    private Integer pointRate;
    private Integer status;
    private Date createTime;
}

