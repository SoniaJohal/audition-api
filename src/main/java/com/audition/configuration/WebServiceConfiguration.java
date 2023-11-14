package com.audition.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.text.SimpleDateFormat;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    private final ResponseHeaderInterceptor responseHeaderInterceptor;

    @Autowired
    public WebServiceConfiguration(final ResponseHeaderInterceptor responseHeaderInterceptor) {
        this.responseHeaderInterceptor = responseHeaderInterceptor;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 1. Allows for date format as yyyy-MM-dd
        objectMapper.setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN));

        // 2. Does not fail on unknown properties
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 3. Maps to camelCase
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);

        // 4. Does not include null values or empty values
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 5. Does not write data as timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(createClientFactory()));

        // Use the configured ObjectMapper
        restTemplate.getMessageConverters()
                .stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(converter -> ((MappingJackson2HttpMessageConverter) converter)
                        .setObjectMapper(objectMapper()));

        // loggingInterceptor that logs request/response for rest template calls.
        restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor()));


        return restTemplate;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(responseHeaderInterceptor);
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        // Log request details
        final Logger LOG = LoggerFactory.getLogger(ClientHttpRequestInterceptor.class);
        return (request, body, execution) -> {
            LOG.info("Request URI: " + request.getURI());
            LOG.info("Request Method: " + request.getMethod());
            LOG.info("Request Headers: " + request.getHeaders());

            // Continue with the execution of the request
            ClientHttpResponse response = execution.execute(request, body);

            // Log response details
            LOG.info("Response Status Code: " + response.getStatusCode());
            LOG.info("Response Headers: " + response.getHeaders());

            return response;
        };
    }
}
