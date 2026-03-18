package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.system.PermissionUpsertDto;
import com.bellego.domain.vo.PermissionTreeNode;
import com.bellego.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<PermissionTreeNode>> tree() {
        return ResultBuilder.success(permissionService.tree());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    @LogOperation(module = "permission", action = "create permission")
    public Result<Void> create(@Valid @RequestBody PermissionUpsertDto dto) {
        permissionService.create(dto);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:edit')")
    @LogOperation(module = "permission", action = "update permission")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody PermissionUpsertDto dto) {
        permissionService.update(id, dto);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    @LogOperation(module = "permission", action = "delete permission")
    public Result<Void> delete(@PathVariable String id) {
        permissionService.delete(id);
        return ResultBuilder.success();
    }
}