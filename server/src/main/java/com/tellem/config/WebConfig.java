package com.tellem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:8081")  // Allow client domain
                .allowedMethods("GET", "POST", "PUT", "DELETE")
         .allowedOrigins("https://your-app.ngrok.io")
                .allowedMethods("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ensure this path points to the correct location of your 'uploads' folder
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./server/uploads/");  // This assumes the 'uploads' folder is in the root directory
    }
}
