package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermissionUpsertRequest {
    @NotBlank(message = "权限名称不能为空")
    private String name;
    @NotBlank(message = "权限编码不能为空")
    private String code;
    @NotNull(message = "权限类型不能为空")
    private Integer type;
    private String parentId;
}

