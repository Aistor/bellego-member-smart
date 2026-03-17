package com.bellego.domain.dto.marketing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponUpsertRequest {
    @NotBlank(message = "优惠券名称不能为空")
    private String name;
    @NotNull(message = "类型不能为空")
    private Integer type;
    @NotNull(message = "优惠值不能为空")
    private BigDecimal couponValue;
    private BigDecimal useCondition;
    @NotNull(message = "库存不能为空")
    private Integer stock;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @NotNull(message = "状态不能为空")
    private Integer status;
}

