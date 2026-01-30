package com.gdy.inspien.global.config;

import com.gdy.inspien.global.util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdConfig {

    @Bean
    public IdGenerator orderIdGenerator() {
        return new IdGenerator();
    }

    @Bean
    public IdGenerator shipmentIdGenerator() {
        return new IdGenerator();
    }
}
