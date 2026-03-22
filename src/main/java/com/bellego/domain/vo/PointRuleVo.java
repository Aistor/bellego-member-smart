package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PointRuleVo {
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