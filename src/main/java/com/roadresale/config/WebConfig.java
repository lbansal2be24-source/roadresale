package com.roadresale.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

// Tells the web server: "addresses starting with /uploads/ are files in the uploads folder".
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String folder = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        if (!folder.endsWith("/")) {
            folder = folder + "/";
        }
        registry.addResourceHandler("/uploads/**").addResourceLocations(folder);
    }
}
