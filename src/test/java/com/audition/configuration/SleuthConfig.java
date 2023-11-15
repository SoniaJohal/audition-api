package com.audition.configuration;

import brave.Tracer;
import brave.Tracing;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class SleuthConfig {

    @Bean
    @Primary
    public Tracer tracer() {
        return Tracing.newBuilder()
            .build()
            .tracer();
    }
}

