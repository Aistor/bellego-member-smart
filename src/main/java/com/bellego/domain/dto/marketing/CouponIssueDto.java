package com.bellego.domain.dto.marketing;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CouponIssueDto {
    @NotNull(message = "发放类型不能为空")
    private Integer type;
    private List<String> memberIds;
    private List<String> levelIds;
}