package com.devtrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
@EnableCaching
public class DevTrackApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevTrackApplication.class, args);
    }
}
