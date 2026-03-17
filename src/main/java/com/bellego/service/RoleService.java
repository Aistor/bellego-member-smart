package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.RolePermissionAssignRequest;
import com.bellego.domain.dto.system.RoleQueryRequest;
import com.bellego.domain.dto.system.RoleUpsertRequest;
import com.bellego.domain.entity.Role;

public interface RoleService {
    PageResult<Role> page(RoleQueryRequest request);
    void create(RoleUpsertRequest request);
    void update(String id, RoleUpsertRequest request);
    void delete(String id);
    void assignPermissions(String id, RolePermissionAssignRequest request);
}

