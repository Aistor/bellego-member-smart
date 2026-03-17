package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleUpsertRequest {
    @NotBlank(message = "角色名称不能为空")
    private String name;
    @NotBlank(message = "角色编码不能为空")
    private String code;
    private String description;
}

