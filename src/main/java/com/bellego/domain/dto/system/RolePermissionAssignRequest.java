package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionAssignRequest {
    @NotEmpty(message = "权限不能为空")
    private List<String> permissionIds;
}

