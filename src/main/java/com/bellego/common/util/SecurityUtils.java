package com.bellego.common.util;

import com.bellego.security.model.LoginAdmin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public String currentAdminId() {
        LoginAdmin loginAdmin = currentLoginAdmin();
        return loginAdmin == null ? null : loginAdmin.getAdmin().getId();
    }

    public String currentAdminName() {
        LoginAdmin loginAdmin = currentLoginAdmin();
        if (loginAdmin == null) {
            return null;
        }
        return loginAdmin.getAdmin().getRealName() == null ? loginAdmin.getAdmin().getUsername() : loginAdmin.getAdmin().getRealName();
    }

    private LoginAdmin currentLoginAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginAdmin loginAdmin)) {
            return null;
        }
        return loginAdmin;
    }
}

