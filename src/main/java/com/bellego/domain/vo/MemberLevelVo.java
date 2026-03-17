package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberLevelVo { private String id; private String name; private Integer level; private Integer minPoints; private BigDecimal minConsumption; private BigDecimal discountRate; private Integer pointRate; private Integer status; private Date createTime; }