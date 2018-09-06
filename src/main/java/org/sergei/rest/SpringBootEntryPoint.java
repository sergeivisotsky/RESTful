package org.sergei.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:applicationContext.xml")
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class SpringBootEntryPoint extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(applicationClass);
    }

    private static Class<SpringBootEntryPoint> applicationClass = SpringBootEntryPoint.class;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootEntryPoint.class, args);
    }
}