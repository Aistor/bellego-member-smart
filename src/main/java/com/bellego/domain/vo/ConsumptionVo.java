package com.bellego.domain.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConsumptionVo {
    private String id;
    private String memberId;
    private String memberName;
    private String storeId;
    private String storeName;
    private BigDecimal amount;
    private Integer pointsEarned;
    private Date consumeTime;
    private Date createTime;
}
