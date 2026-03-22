package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.PageConvertUtils;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.common.StatusUpdateDto;
import com.bellego.domain.dto.system.AdminQueryDto;
import com.bellego.domain.dto.system.AdminRoleAssignDto;
import com.bellego.domain.dto.system.AdminUpsertDto;
import com.bellego.domain.vo.AdminRoleIdsVo;
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
    public Result<IPage<AdminVo>> page(AdminQueryDto dto) {
        return ResultBuilder.success(PageConvertUtils.map(adminService.page(dto), voMapper::toAdminVo));
    }

    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('admin:view')")
    public Result<AdminRoleIdsVo> roleIds(@PathVariable String id) {
        return ResultBuilder.success(new AdminRoleIdsVo(adminService.getRoleIds(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin:add')")
    @LogOperation(module = "admin", action = "create admin")
    public Result<Void> create(@Valid @RequestBody AdminUpsertDto dto) {
        adminService.create(dto);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin:edit')")
    @LogOperation(module = "admin", action = "update admin")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody AdminUpsertDto dto) {
        adminService.update(id, dto);
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
    public Result<Void> updateStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateDto dto) {
        adminService.updateStatus(id, dto.getStatus());
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('admin:assign')")
    @LogOperation(module = "admin", action = "assign admin roles")
    public Result<Void> assignRoles(@PathVariable String id, @Valid @RequestBody AdminRoleAssignDto dto) {
        adminService.assignRoles(id, dto);
        return ResultBuilder.success();
    }
}
