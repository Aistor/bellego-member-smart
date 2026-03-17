package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.AdminQueryRequest;
import com.bellego.domain.dto.system.AdminRoleAssignRequest;
import com.bellego.domain.dto.system.AdminUpsertRequest;
import com.bellego.domain.entity.Admin;
import com.bellego.domain.entity.AdminRole;
import com.bellego.domain.entity.Permission;
import com.bellego.domain.entity.RolePermission;
import com.bellego.mapper.AdminMapper;
import com.bellego.mapper.AdminRoleMapper;
import com.bellego.mapper.PermissionMapper;
import com.bellego.mapper.RolePermissionMapper;
import com.bellego.security.model.LoginAdmin;
import com.bellego.service.AdminService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminMapper adminMapper,
                            AdminRoleMapper adminRoleMapper,
                            RolePermissionMapper rolePermissionMapper,
                            PermissionMapper permissionMapper,
                            PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageResult<Admin> page(AdminQueryRequest request) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<Admin>()
                .eq(request.getStatus() != null, Admin::getStatus, request.getStatus())
                .and(request.getKeyword() != null && !request.getKeyword().isBlank(), q -> q.like(Admin::getUsername, request.getKeyword())
                        .or().like(Admin::getRealName, request.getKeyword())
                        .or().like(Admin::getPhone, request.getKeyword()))
                .orderByDesc(Admin::getCreateTime);
        Page<Admin> page = adminMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        page.getRecords().forEach(admin -> admin.setPassword(null));
        return PageResult.of(page);
    }

    @Override
    public Admin getById(String id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        admin.setPassword(null);
        return admin;
    }

    @Override
    public void create(AdminUpsertRequest request) {
        if (adminMapper.selectOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, request.getUsername())) != null) {
            throw new BusinessException("用户名已存在");
        }
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword() == null || request.getPassword().isBlank() ? "123456" : request.getPassword()));
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        admin.setStatus(request.getStatus());
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);
    }

    @Override
    public void update(String id, AdminUpsertRequest request) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        admin.setUsername(request.getUsername());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        admin.setRealName(request.getRealName());
        admin.setPhone(request.getPhone());
        admin.setStatus(request.getStatus());
        adminMapper.updateById(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        adminMapper.deleteById(id);
        adminRoleMapper.delete(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, id));
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        admin.setStatus(status);
        adminMapper.updateById(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(String id, AdminRoleAssignRequest request) {
        adminRoleMapper.delete(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, id));
        request.getRoleIds().forEach(roleId -> {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(id);
            adminRole.setRoleId(roleId);
            adminRoleMapper.insert(adminRole);
        });
    }

    @Override
    public Admin getByUsername(String username) {
        return adminMapper.selectOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, username));
    }

    @Override
    public LoginAdmin loadLoginAdmin(String adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return new LoginAdmin(admin, findPermissionCodes(adminId));
    }

    @Override
    public List<String> findPermissionCodes(String adminId) {
        List<String> roleIds = adminRoleMapper.selectList(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, adminId))
                .stream().map(AdminRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<String> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds))
                .stream().map(RolePermission::getPermissionId).distinct().toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>().in(Permission::getId, permissionIds))
                .stream().map(Permission::getCode).distinct().toList();
    }
}

