package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.system.AdminQueryRequest;
import com.bellego.domain.dto.system.AdminRoleAssignRequest;
import com.bellego.domain.dto.system.AdminUpsertRequest;
import com.bellego.domain.entity.Admin;
import com.bellego.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<PageResult<Admin>> page(AdminQueryRequest request) {
        return ResultBuilder.success(adminService.page(request));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:add')")
    @LogOperation(module = "管理员", action = "新增管理员")
    public Result<Void> create(@Valid @RequestBody AdminUpsertRequest request) {
        adminService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:edit')")
    @LogOperation(module = "管理员", action = "修改管理员")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody AdminUpsertRequest request) {
        adminService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @LogOperation(module = "管理员", action = "删除管理员")
    public Result<Void> delete(@PathVariable String id) {
        adminService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('admin:edit')")
    @LogOperation(module = "管理员", action = "修改管理员状态")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        adminService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('admin:assign')")
    @LogOperation(module = "管理员", action = "分配角色")
    public Result<Void> assignRoles(@PathVariable String id, @Valid @RequestBody AdminRoleAssignRequest request) {
        adminService.assignRoles(id, request);
        return ResultBuilder.success();
    }
}

