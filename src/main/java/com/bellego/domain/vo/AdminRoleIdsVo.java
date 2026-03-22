package com.bellego.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AdminRoleIdsVo {
    private List<String> roleIds;
}
