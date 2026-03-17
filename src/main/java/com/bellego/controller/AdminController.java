package com.bellego.controller;

import com.bellego.aop.LogOperation;
import com.bellego.common.result.PageResult;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateRequest;
import com.bellego.domain.dto.system.AdminQueryRequest;
import com.bellego.domain.dto.system.AdminRoleAssignRequest;
import com.bellego.domain.dto.system.AdminUpsertRequest;
import com.bellego.domain.vo.AdminVo;
import com.bellego.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {
    private final AdminService adminService;
    private final VoMapper voMapper;

    public AdminController(AdminService adminService, VoMapper voMapper) {
        this.adminService = adminService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<PageResult<AdminVo>> page(AdminQueryRequest request) {
        return ResultBuilder.success(adminService.page(request).map(voMapper::toAdminVo));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:add')")
    @LogOperation(module = "admin", action = "create admin")
    public Result<Void> create(@Valid @RequestBody AdminUpsertRequest request) {
        adminService.create(request);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:edit')")
    @LogOperation(module = "admin", action = "update admin")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody AdminUpsertRequest request) {
        adminService.update(id, request);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @LogOperation(module = "admin", action = "delete admin")
    public Result<Void> delete(@PathVariable String id) {
        adminService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('admin:edit')")
    @LogOperation(module = "admin", action = "update admin status")
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        adminService.updateStatus(id, request.getStatus());
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('admin:assign')")
    @LogOperation(module = "admin", action = "assign admin roles")
    public Result<Void> assignRoles(@PathVariable String id, @Valid @RequestBody AdminRoleAssignRequest request) {
        adminService.assignRoles(id, request);
        return ResultBuilder.success();
    }
}