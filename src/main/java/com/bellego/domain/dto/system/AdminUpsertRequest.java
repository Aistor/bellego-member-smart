package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUpsertRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String realName;
    private String phone;
    @NotNull(message = "状态不能为空")
    private Integer status;
}

