package com.bellego.service;

import com.bellego.domain.dto.system.PermissionUpsertRequest;
import com.bellego.domain.vo.PermissionTreeNode;

import java.util.List;

public interface PermissionService {
    List<PermissionTreeNode> tree();
    void create(PermissionUpsertRequest request);
    void update(String id, PermissionUpsertRequest request);
    void delete(String id);
}

