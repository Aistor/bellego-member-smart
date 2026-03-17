package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.result.PageResult;
import com.bellego.domain.dto.system.RolePermissionAssignRequest;
import com.bellego.domain.dto.system.RoleQueryRequest;
import com.bellego.domain.dto.system.RoleUpsertRequest;
import com.bellego.domain.entity.Role;
import com.bellego.domain.entity.RolePermission;
import com.bellego.mapper.RoleMapper;
import com.bellego.mapper.RolePermissionMapper;
import com.bellego.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public PageResult<Role> page(RoleQueryRequest request) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .and(request.getKeyword() != null && !request.getKeyword().isBlank(), q -> q.like(Role::getName, request.getKeyword()).or().like(Role::getCode, request.getKeyword()))
                .orderByDesc(Role::getCreateTime);
        Page<Role> page = roleMapper.selectPage(new Page<>(request.getPageNum(), request.getPageSize()), wrapper);
        return PageResult.of(page);
    }

    @Override
    public void create(RoleUpsertRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        role.setCreateTime(new Date());
        roleMapper.insert(role);
    }

    @Override
    public void update(String id, RoleUpsertRequest request) {
        Role role = roleMapper.selectById(id);
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setDescription(request.getDescription());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(String id, RolePermissionAssignRequest request) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        request.getPermissionIds().forEach(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(id);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        });
    }
}

