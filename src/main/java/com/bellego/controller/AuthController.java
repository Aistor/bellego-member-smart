package com.bellego.controller;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.auth.LoginDto;
import com.bellego.domain.vo.LoginVo;
import com.bellego.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return ResultBuilder.success(authService.login(dto));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return ResultBuilder.success();
    }
}