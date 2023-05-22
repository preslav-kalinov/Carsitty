package com.kalinov.carsitty.config;

import com.kalinov.carsitty.util.ModelMapperUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperUtilConfig {
    @Bean
    public ModelMapperUtil modelMapperUtil() {
        return new ModelMapperUtil();
    }
}