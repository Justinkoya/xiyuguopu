package com.xiyuguopu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC 配置 — 注册 JWT 拦截器（管理后台 + 小程序双通道）
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    private final UserJwtInterceptor userJwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理后台：/api/admin/** 需要登录，排除登录接口本身
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login");

        // 小程序端：/api/user/**、/api/orders/**、/api/wx/pay 需要登录
        // 排除微信登录和支付回调（公开接口）
        registry.addInterceptor(userJwtInterceptor)
                .addPathPatterns("/api/user/**", "/api/orders/**", "/api/wx/pay")
                .excludePathPatterns("/api/wx/login", "/api/wx/pay-callback");
    }
}
