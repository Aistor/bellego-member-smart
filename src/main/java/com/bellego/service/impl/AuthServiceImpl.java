package com.bellego.service.impl;

import com.bellego.common.exception.BusinessException;
import com.bellego.domain.dto.auth.LoginDto;
import com.bellego.domain.entity.Admin;
import com.bellego.domain.vo.LoginVo;
import com.bellego.security.JwtService;
import com.bellego.service.AdminService;
import com.bellego.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
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
    public LoginVo login(LoginDto dto) {
        log.info("开始管理员登录，username={}", dto.getUsername());
        Admin admin = adminService.getByUsername(dto.getUsername());
        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            log.error("管理员登录失败，用户名或密码错误，username={}", dto.getUsername());
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            log.error("管理员登录失败，账号被禁用，adminId={}", admin.getId());
            throw new BusinessException("账号已被禁用");
        }
        log.info("管理员登录成功，adminId={}", admin.getId());
        return LoginVo.builder()
                .token(jwtService.generateToken(admin.getId(), admin.getUsername()))
                .adminId(admin.getId())
                .username(admin.getUsername())
                .realName(admin.getRealName())
                .permissions(adminService.findPermissionCodes(admin.getId()))
                .build();
    }

    @Override
    public void logout() {
        log.info("执行退出登录操作");
    }
}