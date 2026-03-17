package com.bellego.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class PermissionTreeNode {
    private String id;
    private String name;
    private String code;
    private Integer type;
    @Builder.Default
    private List<PermissionTreeNode> children = new ArrayList<>();
}

