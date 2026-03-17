package com.bellego.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("point_rule")
public class PointRule {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String ruleName;
    private Integer ruleType;
    private String applicableLevelId;
    private Integer pointsPerUnit;
    private BigDecimal minAmount;
    private Integer maxPoints;
    private Integer status;
    private Date createTime;
}

