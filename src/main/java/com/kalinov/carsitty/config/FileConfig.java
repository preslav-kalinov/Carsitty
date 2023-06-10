package com.kalinov.carsitty.config;

import com.kalinov.carsitty.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileConfig {
    @Bean
    public FileService instantiateFileService(@Value("${carsitty.fileservice.filepath}") String filePath) {
        return new FileService(filePath);
    }
}