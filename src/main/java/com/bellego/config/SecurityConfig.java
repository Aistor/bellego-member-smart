package com.bellego.config;

import com.bellego.security.JwtAuthenticationFilter;
import com.bellego.security.handler.RestAccessDeniedHandler;
import com.bellego.security.handler.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint authenticationEntryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // 禁用 CSRF（JWT 不需要）
                .cors(Customizer.withDefaults()) // 启用 CORS
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/api/v1/auth/login", "/error").permitAll()  // 放行登录接口
                        .anyRequest().authenticated()) // 其他请求需要认证
                .exceptionHandling(configurer -> configurer
                        .authenticationEntryPoint(authenticationEntryPoint) // 认证失败处理
                        .accessDeniedHandler(accessDeniedHandler)) // 权限不足处理
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 添加 JWT 过滤器
        return httpSecurity.build();
    }
}

