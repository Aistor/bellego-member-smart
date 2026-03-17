package com.bellego.controller;

import com.bellego.common.result.Result;
import com.bellego.common.result.ResultBuilder;
import com.bellego.domain.dto.auth.LoginRequest;
import com.bellego.domain.vo.LoginVo;
import com.bellego.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginRequest request) {
        return ResultBuilder.success(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return ResultBuilder.success();
    }
}

