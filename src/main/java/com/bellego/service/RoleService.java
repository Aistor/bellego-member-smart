package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.system.RolePermissionAssignDto;
import com.bellego.domain.dto.system.RoleQueryDto;
import com.bellego.domain.dto.system.RoleUpsertDto;
import com.bellego.domain.entity.Role;

import java.util.List;

public interface RoleService {
    IPage<Role> page(RoleQueryDto dto);
    void create(RoleUpsertDto dto);
    void update(String id, RoleUpsertDto dto);
    void delete(String id);
    void assignPermissions(String id, RolePermissionAssignDto dto);
    List<String> getPermissionIds(String id);
}
