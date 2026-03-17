package com.bellego.domain.dto.marketing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointRuleUpsertRequest {
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;
    @NotNull(message = "规则类型不能为空")
    private Integer ruleType;
    private String applicableLevelId;
    @NotNull(message = "积分倍率不能为空")
    private Integer pointsPerUnit;
    private BigDecimal minAmount;
    private Integer maxPoints;
    @NotNull(message = "状态不能为空")
    private Integer status;
}

