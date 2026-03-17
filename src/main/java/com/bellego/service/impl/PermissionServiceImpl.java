package com.bellego.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bellego.domain.dto.system.PermissionUpsertRequest;
import com.bellego.domain.entity.Permission;
import com.bellego.mapper.PermissionMapper;
import com.bellego.service.PermissionService;
import com.bellego.domain.vo.PermissionTreeNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionTreeNode> tree() {
        List<Permission> permissions = permissionMapper.selectList(new LambdaQueryWrapper<Permission>().orderByAsc(Permission::getCreateTime));
        Map<String, List<Permission>> childrenMap = permissions.stream().collect(Collectors.groupingBy(permission -> permission.getParentId() == null ? "ROOT" : permission.getParentId()));
        return buildTree(childrenMap, "ROOT");
    }

    @Override
    public void create(PermissionUpsertRequest request) {
        Permission permission = new Permission();
        copy(request, permission);
        permission.setCreateTime(new Date());
        permissionMapper.insert(permission);
    }

    @Override
    public void update(String id, PermissionUpsertRequest request) {
        Permission permission = permissionMapper.selectById(id);
        copy(request, permission);
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(String id) {
        permissionMapper.deleteById(id);
    }

    private List<PermissionTreeNode> buildTree(Map<String, List<Permission>> childrenMap, String parentId) {
        List<Permission> children = childrenMap.getOrDefault(parentId, List.of());
        List<PermissionTreeNode> result = new ArrayList<>();
        for (Permission permission : children) {
            result.add(PermissionTreeNode.builder()
                    .id(permission.getId())
                    .name(permission.getName())
                    .code(permission.getCode())
                    .type(permission.getType())
                    .children(buildTree(childrenMap, permission.getId()))
                    .build());
        }
        return result;
    }

    private void copy(PermissionUpsertRequest request, Permission permission) {
        permission.setName(request.getName());
        permission.setCode(request.getCode());
        permission.setType(request.getType());
        permission.setParentId(request.getParentId());
    }
}

