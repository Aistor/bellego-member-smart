package com.bellego.domain.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginVo {
    private String token;
    private String adminId;
    private String username;
    private String realName;
    private List<String> permissions;
}

