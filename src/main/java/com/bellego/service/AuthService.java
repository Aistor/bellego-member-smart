package com.bellego.service;

import com.bellego.domain.dto.auth.LoginRequest;
import com.bellego.domain.vo.LoginVo;

public interface AuthService {
    LoginVo login(LoginRequest request);
    void logout();
}

