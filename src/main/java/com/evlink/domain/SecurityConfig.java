// src/main/java/kr/co/ictedu/teamcfinal/config/SecurityConfig.java
package com.evlink.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService; // @Service
  private final OAuth2SuccessHandler oAuth2SuccessHandler;       // @Component

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(c -> c.configurationSource(corsConfigurationSource()))
      .authorizeHttpRequests(auth -> auth
        // ★ 컨텍스트 경로(/TeamCproject_final)는 여기 쓰지 않음 (Spring이 내부 처리)
        .requestMatchers("/oauth2/**", "/login/oauth2/**", "/api/auth/**").permitAll()
        .anyRequest().permitAll()
      )
      .oauth2Login(o -> o
        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
        .successHandler(oAuth2SuccessHandler) //  성공 시 프론트 루트로
      );
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of(
      "http://localhost:3000",
      "http://localhost:3001",
      "http://192.168.0.90:3000",
      "http://192.168.0.90:3001"
    ));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
    cfg.setAllowCredentials(true); // ✅ JSESSIONID 쿠키 주고받기
    cfg.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
