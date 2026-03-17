package com.bellego.service;

import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.AdminQueryRequest;
import com.bellego.domain.dto.system.AdminRoleAssignRequest;
import com.bellego.domain.dto.system.AdminUpsertRequest;
import com.bellego.domain.entity.Admin;
import com.bellego.security.model.LoginAdmin;

import java.util.List;

public interface AdminService {
    PageResult<Admin> page(AdminQueryRequest request);
    Admin getById(String id);
    void create(AdminUpsertRequest request);
    void update(String id, AdminUpsertRequest request);
    void delete(String id);
    void updateStatus(String id, Integer status);
    void assignRoles(String id, AdminRoleAssignRequest request);
    Admin getByUsername(String username);
    LoginAdmin loadLoginAdmin(String adminId);
    List<String> findPermissionCodes(String adminId);
}

