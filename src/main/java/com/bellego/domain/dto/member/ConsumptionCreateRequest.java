package com.bellego.domain.dto.member;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ConsumptionCreateRequest {
    @NotBlank(message = "会员ID不能为空")
    private String memberId;
    @NotBlank(message = "门店ID不能为空")
    private String storeId;
    @NotNull(message = "消费金额不能为空")
    @DecimalMin(value = "0.01", message = "消费金额必须大于0")
    private BigDecimal amount;
    private Date consumeTime;
}

