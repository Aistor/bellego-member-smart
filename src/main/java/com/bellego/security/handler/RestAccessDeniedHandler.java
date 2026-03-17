package com.bellego.security.handler;

import com.bellego.common.result.ResultBuilder;
import com.bellego.common.result.ResultEnum;
import com.bellego.common.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final JsonUtils jsonUtils;

    public RestAccessDeniedHandler(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().write(jsonUtils.toJson(ResultBuilder.error(ResultEnum.FORBIDDEN)));
    }
}

