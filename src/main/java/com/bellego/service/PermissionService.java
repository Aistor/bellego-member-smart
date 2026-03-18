package com.bellego.service;

import com.bellego.domain.dto.system.PermissionUpsertDto;
import com.bellego.domain.vo.PermissionTreeNode;

import java.util.List;

public interface PermissionService {
    List<PermissionTreeNode> tree();
    void create(PermissionUpsertDto dto);
    void update(String id, PermissionUpsertDto dto);
    void delete(String id);
}