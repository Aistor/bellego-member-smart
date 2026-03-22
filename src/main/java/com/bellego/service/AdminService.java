package com.bellego.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bellego.domain.dto.system.AdminQueryDto;
import com.bellego.domain.dto.system.AdminRoleAssignDto;
import com.bellego.domain.dto.system.AdminUpsertDto;
import com.bellego.domain.entity.Admin;
import com.bellego.security.model.LoginAdmin;

import java.util.List;

public interface AdminService {
    IPage<Admin> page(AdminQueryDto dto);
    Admin getById(String id);
    void create(AdminUpsertDto dto);
    void update(String id, AdminUpsertDto dto);
    void delete(String id);
    void updateStatus(String id, Integer status);
    void assignRoles(String id, AdminRoleAssignDto dto);
    List<String> getRoleIds(String id);
    Admin getByUsername(String username);
    LoginAdmin loadLoginAdmin(String adminId);
    List<String> findPermissionCodes(String adminId);
}
