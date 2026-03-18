package com.bellego.domain.dto.system;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class AdminRoleAssignDto {
    @NotEmpty(message = "角色不能为空")
    private List<String> roleIds;
}