package com.bellego.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.aop.LogOperation;
import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.common.util.PageConvertUtils;
import com.bellego.common.util.VoMapper;
import com.bellego.domain.dto.system.RolePermissionAssignDto;
import com.bellego.domain.dto.system.RoleQueryDto;
import com.bellego.domain.dto.system.RoleUpsertDto;
import com.bellego.domain.vo.RoleVo;
import com.bellego.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;
    private final VoMapper voMapper;

    public RoleController(RoleService roleService, VoMapper voMapper) {
        this.roleService = roleService;
        this.voMapper = voMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('role:view')")
    public Result<IPage<RoleVo>> page(RoleQueryDto dto) {
        return ResultBuilder.success(PageConvertUtils.map(roleService.page(dto), voMapper::toRoleVo));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    @LogOperation(module = "role", action = "create role")
    public Result<Void> create(@Valid @RequestBody RoleUpsertDto dto) {
        roleService.create(dto);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    @LogOperation(module = "role", action = "update role")
    public Result<Void> update(@PathVariable String id, @Valid @RequestBody RoleUpsertDto dto) {
        roleService.update(id, dto);
        return ResultBuilder.success();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    @LogOperation(module = "role", action = "delete role")
    public Result<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ResultBuilder.success();
    }

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    @LogOperation(module = "role", action = "assign role permissions")
    public Result<Void> assignPermissions(@PathVariable String id, @Valid @RequestBody RolePermissionAssignDto dto) {
        roleService.assignPermissions(id, dto);
        return ResultBuilder.success();
    }
}