package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.system.RolePermissionAssignDto;
import com.bellego.domain.dto.system.RoleQueryDto;
import com.bellego.domain.dto.system.RoleUpsertDto;
import com.bellego.domain.entity.Role;
import com.bellego.domain.entity.RolePermission;
import com.bellego.mapper.RoleMapper;
import com.bellego.mapper.RolePermissionMapper;
import com.bellego.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 角色服务实现
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleServiceImpl(RoleMapper roleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    @Override
    public IPage<Role> page(RoleQueryDto dto) {
        log.info("开始分页查询角色，关键字={}", dto.getKeyword());
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<Role>()
                .and(dto.getKeyword() != null && !dto.getKeyword().isBlank(), q -> q.like(Role::getName, dto.getKeyword()).or().like(Role::getCode, dto.getKeyword()))
                .orderByDesc(Role::getCreateTime);
        return roleMapper.selectPage(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);
    }

    @Override
    public void create(RoleUpsertDto dto) {
        log.info("开始新增角色，code={}", dto.getCode());
        Role role = new Role();
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setDescription(dto.getDescription());
        role.setCreateTime(new Date());
        roleMapper.insert(role);
    }

    @Override
    public void update(String id, RoleUpsertDto dto) {
        log.info("开始修改角色，id={}", id);
        Role role = roleMapper.selectById(id);
        role.setName(dto.getName());
        role.setCode(dto.getCode());
        role.setDescription(dto.getDescription());
        roleMapper.updateById(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("开始删除角色，id={}", id);
        roleMapper.deleteById(id);
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(String id, RolePermissionAssignDto dto) {
        log.info("开始分配角色权限，roleId={}, count={}", id, dto.getPermissionIds().size());
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id));
        dto.getPermissionIds().forEach(permissionId -> {
            RolePermission relation = new RolePermission();
            relation.setRoleId(id);
            relation.setPermissionId(permissionId);
            rolePermissionMapper.insert(relation);
        });
    }

    @Override
    public List<String> getPermissionIds(String id) {
        log.info("开始查询角色已分配权限，roleId={}", id);
        if (roleMapper.selectById(id) == null) {
            log.error("查询角色权限失败，角色不存在，roleId={}", id);
            throw new BusinessException("角色不存在");
        }
        return rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, id))
                .stream()
                .map(RolePermission::getPermissionId)
                .toList();
    }
}
