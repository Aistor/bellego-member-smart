package com.bellego.service.impl;

import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.auth.LoginRequest;
import com.bellego.domain.entity.Admin;
import com.bellego.domain.vo.LoginVo;
import com.bellego.security.JwtService;
import com.bellego.service.AdminService;
import com.bellego.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    private final AdminService adminService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AdminService adminService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginVo login(LoginRequest request) {
        log.info("Admin login attempt, username={}", request.getUsername());
        Admin admin = adminService.getByUsername(request.getUsername());
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPassword()))
            throw new BusinessException("Invalid username or password");
        if (admin.getStatus() == null || admin.getStatus() != 1) throw new BusinessException("Account disabled");
        log.info("Admin login success, adminId={}", admin.getId());
        return LoginVo.builder().token(jwtService.generateToken(admin.getId(), admin.getUsername())).adminId(admin.getId()).username(admin.getUsername()).realName(admin.getRealName()).permissions(adminService.findPermissionCodes(admin.getId())).build();
    }

    @Override
    public void logout() {
        log.info("Admin logout invoked");
    }
}