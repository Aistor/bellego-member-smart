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
        operationLog.setIp(request.getRemoteAddr());
        operationLog.setCreateTime(new Date());
        operationLogService.saveAsync(operationLog);
    }
}

