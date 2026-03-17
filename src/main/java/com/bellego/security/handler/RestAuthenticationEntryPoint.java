package com.bellego.security.handler;

import com.bellego.common.result.ResultBuilder;
import com.bellego.common.result.ResultEnum;
import com.bellego.common.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final JsonUtils jsonUtils;

    public RestAuthenticationEntryPoint(JsonUtils jsonUtils) {
        this.jsonUtils = jsonUtils;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonUtils.toJson(ResultBuilder.error(ResultEnum.UNAUTHORIZED)));
    }
}

