package com.app.canteen.config;

import com.app.canteen.security.JwtFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(reg -> reg
            .requestMatchers("/api/auth/**").permitAll()
            // keep public GETs to menu & ratings; lock down mutations via JwtFilter + method checks
            .requestMatchers("/api/menu/**").permitAll()
            .requestMatchers("/api/ratings/**").permitAll()
            .anyRequest().permitAll()
        );
    return http.build();
  }

  /**
   * Register the JwtFilter without naming the bean "jwtFilter" to avoid
   * clashing with the component-scanned JwtFilter bean itself.
   * The filter will run for protected API patterns; adjust as you tighten access.
   */
  @Bean
  public FilterRegistrationBean<JwtFilter> jwtFilterRegistration(JwtFilter jwtFilter) {
    FilterRegistrationBean<JwtFilter> reg = new FilterRegistrationBean<>();
    reg.setFilter(jwtFilter);
    // Apply to endpoints that need authentication/role checks inside the filter
    reg.addUrlPatterns("/api/menu/*", "/api/ratings/*");
    // Order < default Spring Security chain is fine here; raise if you need it earlier
    reg.setOrder(1);
    return reg;
  }
}
