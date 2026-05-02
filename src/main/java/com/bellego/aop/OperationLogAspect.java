package com.bellego.aop;

import com.bellego.common.util.JsonUtils;
import com.bellego.domain.entity.Admin;
import com.bellego.domain.entity.OperationLog;
import com.bellego.security.model.LoginAdmin;
import com.bellego.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final HttpServletRequest request;
    private final JsonUtils jsonUtils;

    public OperationLogAspect(OperationLogService operationLogService, HttpServletRequest request, JsonUtils jsonUtils) {
        this.operationLogService = operationLogService;
        this.request = request;
        this.jsonUtils = jsonUtils;
    }

    @AfterReturning(value = "@annotation(logOperation)")
    public void afterReturning(JoinPoint joinPoint, LogOperation logOperation) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginAdmin loginAdmin)) {
            return;
        }
        Admin admin = loginAdmin.getAdmin();
        Map<String, Object> detail = new HashMap<>();
        detail.put("args", joinPoint.getArgs());
        detail.put("path", request.getRequestURI());

        OperationLog operationLog = new OperationLog();
        operationLog.setOperatorId(admin.getId());
        operationLog.setOperatorName(admin.getRealName() == null ? admin.getUsername() : admin.getRealName());
        operationLog.setModule(logOperation.module());
        operationLog.setOperation(logOperation.action());
        operationLog.setDetail(jsonUtils.toJson(detail));
        operationLog.setIp(getIpv4Address(request));
        operationLog.setCreateTime(new Date());
        operationLogService.saveAsync(operationLog);
    }

    /**
     * 从请求中获取IPv4格式的IP地址
     */
    private String getIpv4Address(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For可能包含多个IP，取第一个
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getHeader("X-Real-IP");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        }
        // IPv6本地回环地址转换为IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        // IPv4映射的IPv6地址（如 ::ffff:192.168.1.1）提取IPv4部分
        if (ip != null && ip.startsWith("::ffff:")) {
            return ip.substring(7);
        }
        return ip;
    }
}

