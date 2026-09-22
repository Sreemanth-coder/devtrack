package com.devtrack.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "devtrack.cors")
public record CorsProperties(List<String> allowedOrigins) {
}
