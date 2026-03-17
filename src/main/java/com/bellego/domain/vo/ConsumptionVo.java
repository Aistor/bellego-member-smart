package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConsumptionVo { private String id; private String memberId; private String storeId; private BigDecimal amount; private Integer pointsEarned; private Date consumeTime; private Date createTime; }