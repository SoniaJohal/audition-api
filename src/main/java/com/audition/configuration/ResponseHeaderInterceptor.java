package com.audition.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseHeaderInterceptor implements HandlerInterceptor {

    private final ResponseHeaderInjector responseHeaderInjector;

    @Autowired
    public ResponseHeaderInterceptor(final ResponseHeaderInjector responseHeaderInjector) {
        this.responseHeaderInjector = responseHeaderInjector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        responseHeaderInjector.injectTraceAndSpanIds(response);
        return true;
    }
}
