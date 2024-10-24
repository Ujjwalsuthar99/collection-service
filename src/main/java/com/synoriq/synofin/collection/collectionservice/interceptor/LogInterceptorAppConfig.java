package com.synoriq.synofin.collection.collectionservice.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration

public class LogInterceptorAppConfig implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    public LogInterceptorAppConfig(LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }
}
