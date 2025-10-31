package com.app.canteen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173","http://localhost:8081","http://localhost:3000")
        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true);
  }
}
