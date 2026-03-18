package com.bellego.domain.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateDto {
    @NotNull(message = "状态不能为空")
    private Integer status;
}