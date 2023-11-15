package com.audition.configuration;

import brave.Span;
import brave.Tracer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseHeaderInjector {

    private final Tracer tracer;

    @Autowired
    public ResponseHeaderInjector(final Tracer tracer) {
        this.tracer = tracer;
    }

    public void injectTraceAndSpanIds(final HttpServletResponse response) {
        final Span currentSpan = tracer.currentSpan();
        final Span span = currentSpan == null ? tracer.nextSpan() : currentSpan;

        response.addHeader("X-Trace-Id", span.context().traceIdString());
        response.addHeader("X-Span-Id", span.context().spanIdString());
    }
}
