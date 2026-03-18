package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.domain.dto.system.PermissionUpsertDto;
import com.bellego.domain.entity.Permission;
import com.bellego.domain.vo.PermissionTreeNode;
import com.bellego.mapper.PermissionMapper;
import com.bellego.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现
 */
@Slf4j
@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionTreeNode> tree() {
        log.info("开始查询权限树");
        List<Permission> permissions = permissionMapper.selectList(new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getCreateTime));
        Map<String, List<Permission>> childrenMap = permissions.stream().collect(Collectors.groupingBy(permission -> permission.getParentId() == null ? "ROOT" : permission.getParentId()));
        return buildTree(childrenMap, "ROOT");
    }

    @Override
    public void create(PermissionUpsertDto dto) {
        log.info("开始新增权限，code={}", dto.getCode());
        Permission permission = new Permission();
        copy(dto, permission);
        permission.setCreateTime(new Date());
        permissionMapper.insert(permission);
    }

    @Override
    public void update(String id, PermissionUpsertDto dto) {
        log.info("开始修改权限，id={}", id);
        Permission permission = permissionMapper.selectById(id);
        copy(dto, permission);
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(String id) {
        log.info("开始删除权限，id={}", id);
        permissionMapper.deleteById(id);
    }

    private List<PermissionTreeNode> buildTree(Map<String, List<Permission>> childrenMap, String parentId) {
        List<Permission> children = childrenMap.getOrDefault(parentId, List.of());
        List<PermissionTreeNode> result = new ArrayList<>();
        for (Permission permission : children) {
            result.add(PermissionTreeNode.builder().id(permission.getId()).name(permission.getName()).code(permission.getCode()).type(permission.getType()).children(buildTree(childrenMap, permission.getId())).build());
        }
        return result;
    }

    private void copy(PermissionUpsertDto dto, Permission permission) {
        permission.setName(dto.getName());
        permission.setCode(dto.getCode());
        permission.setType(dto.getType());
        permission.setParentId(dto.getParentId());
    }
}