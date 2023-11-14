package com.audition.configuration;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeaderInjector {

    private final Tracer tracer;

    public ResponseHeaderInjector(final Tracer tracer) {
        this.tracer = tracer;
    }

    public void injectTraceAndSpanIds(HttpServletResponse response) {
        Span currentSpan = tracer.currentSpan();
        response.addHeader("X-Trace-Id", currentSpan.context().traceIdString());
        response.addHeader("X-Span-Id", currentSpan.context().spanIdString());
    }
}
