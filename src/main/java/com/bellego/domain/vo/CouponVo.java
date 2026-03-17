package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponVo { private String id; private String name; private Integer type; private BigDecimal couponValue; private BigDecimal useCondition; private Integer stock; private Integer totalIssued; private Date startTime; private Date endTime; private Integer status; private Date createTime; }