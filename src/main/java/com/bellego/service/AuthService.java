package com.bellego.service;

import com.bellego.domain.dto.auth.LoginDto;
import com.bellego.domain.vo.LoginVo;

public interface AuthService {
    LoginVo login(LoginDto dto);
    void logout();
}