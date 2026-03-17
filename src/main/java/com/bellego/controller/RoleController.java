package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.system.RolePermissionAssignRequest;
import com.bellego.domain.dto.system.RoleQueryRequest;
import com.bellego.domain.dto.system.RoleUpsertRequest;
import com.bellego.domain.entity.Role;
import com.bellego.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public Result<PageResult<Role>> page(RoleQueryRequest request) {
        return ResultBuilder.success(roleService.page(request));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    @LogOperation(module = "角色管理", action = "新增角色")
    public Result<Void> create(@Valid @RequestBody RoleUpsertRequest request) {
        roleService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    @LogOperation(module = "角色管理", action = "修改角色")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody RoleUpsertRequest request) {
        roleService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @LogOperation(module = "角色管理", action = "删除角色")
    public Result<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    @LogOperation(module = "角色管理", action = "分配权限")
    public Result<Void> assignPermissions(@PathVariable String id, @Valid @RequestBody RolePermissionAssignRequest request) {
        roleService.assignPermissions(id, request);
        return ResultBuilder.success();
    }
}

