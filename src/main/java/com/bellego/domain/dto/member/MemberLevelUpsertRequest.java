package com.bellego.domain.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberLevelUpsertRequest {
    @NotBlank(message = "等级名称不能为空")
    private String name;
    @NotNull(message = "等级值不能为空")
    private Integer level;
    @NotNull(message = "最低积分不能为空")
    private Integer minPoints;
    @NotNull(message = "最低消费不能为空")
    private BigDecimal minConsumption;
    @NotNull(message = "折扣率不能为空")
    private BigDecimal discountRate;
    @NotNull(message = "积分倍率不能为空")
    private Integer pointRate;
    @NotNull(message = "状态不能为空")
    private Integer status;
}

