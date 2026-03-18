package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.system.AdminQueryDto;
import com.bellego.domain.dto.system.AdminRoleAssignDto;
import com.bellego.domain.dto.system.AdminUpsertDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 管理员服务实现
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    private final AdminMapper adminMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AdminMapper adminMapper, AdminRoleMapper adminRoleMapper, RolePermissionMapper rolePermissionMapper, PermissionMapper permissionMapper, PasswordEncoder passwordEncoder) {
        this.adminMapper = adminMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public IPage<Admin> page(AdminQueryDto dto) {
        log.info("开始分页查询管理员，关键字={}, 状态={}", dto.getKeyword(), dto.getStatus());
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<Admin>()
                .eq(dto.getStatus() != null, Admin::getStatus, dto.getStatus())
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), q -> q.like(Admin::getUsername, dto.getKeyword()).or().like(Admin::getRealName, dto.getKeyword()).or().like(Admin::getPhone, dto.getKeyword()))
                .orderByDesc(Admin::getCreateTime);
        Page<Admin> page = adminMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
        page.getRecords().forEach(admin -> admin.setPassword(null));
        return page;
    }

    @Override
    public Admin getById(String id) {
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            log.error("查询管理员失败，管理员不存在，id={}", id);
            throw new BusinessException("管理员不存在");
        }
        admin.setPassword(null);
        return admin;
    }

    @Override
    public void create(AdminUpsertDto dto) {
        log.info("开始新增管理员，用户名={}", dto.getUsername());
        if (adminMapper.selectOne(new LambdaQueryWrapper<Admin>().eq(Admin::getUsername, dto.getUsername())) != null) {
            log.error("新增管理员失败，用户名重复，username={}", dto.getUsername());
            throw new BusinessException("用户名已存在");
        }
        Admin admin = new Admin();
        admin.setUsername(dto.getUsername());
        admin.setPassword(passwordEncoder.encode(dto.getPassword() == null || dto.getPassword().isBlank() ? "123456" : dto.getPassword()));
        admin.setRealName(dto.getRealName());
        admin.setPhone(dto.getPhone());
        admin.setStatus(dto.getStatus());
        admin.setCreateTime(new Date());
        adminMapper.insert(admin);
    }

    @Override
    public void update(String id, AdminUpsertDto dto) {
        log.info("开始修改管理员，id={}", id);
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            log.error("修改管理员失败，管理员不存在，id={}", id);
            throw new BusinessException("管理员不存在");
        }
        admin.setUsername(dto.getUsername());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        admin.setRealName(dto.getRealName());
        admin.setPhone(dto.getPhone());
        admin.setStatus(dto.getStatus());
        adminMapper.updateById(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("开始删除管理员，id={}", id);
        adminMapper.deleteById(id);
        adminRoleMapper.delete(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, id));
    }

    @Override
    public void updateStatus(String id, Integer status) {
        log.info("开始修改管理员状态，id={}, status={}", id, status);
        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            log.error("修改管理员状态失败，管理员不存在，id={}", id);
            throw new BusinessException("管理员不存在");
        }
        admin.setStatus(status);
        adminMapper.updateById(admin);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(String id, AdminRoleAssignDto dto) {
        log.info("开始为管理员分配角色，id={}, roleCount={}", id, dto.getRoleIds().size());
        adminRoleMapper.delete(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, id));
        dto.getRoleIds().forEach(roleId -> {
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
            log.error("加载登录管理员失败，管理员不存在，adminId={}", adminId);
            throw new BusinessException("管理员不存在");
        }
        return new LoginAdmin(admin, findPermissionCodes(adminId));
    }

    @Override
    public List<String> findPermissionCodes(String adminId) {
        List<String> roleIds = adminRoleMapper.selectList(new LambdaQueryWrapper<AdminRole>().eq(AdminRole::getAdminId, adminId)).stream().map(AdminRole::getRoleId).toList();
        if (roleIds.isEmpty()) {
            return List.of();
        }
        List<String> permissionIds = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds)).stream().map(RolePermission::getPermissionId).distinct().toList();
        if (permissionIds.isEmpty()) {
            return List.of();
        }
        return permissionMapper.selectList(new LambdaQueryWrapper<Permission>().in(Permission::getId, permissionIds)).stream().map(Permission::getCode).distinct().toList();
    }
}