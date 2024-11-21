package com.gigasea.learning_management.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // Use DataSize class to specify the file and request size
        factory.setMaxFileSize(DataSize.ofMegabytes(10));  // Set maximum file size (10MB)
        factory.setMaxRequestSize(DataSize.ofMegabytes(10));  // Set maximum request size (10MB)
        return factory.createMultipartConfig();
    }
}
