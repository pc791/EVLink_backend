package com.evlink.global.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.*;

import com.evlink.CustomOAuth2UserService;

//import org.springframework.security.config.http.SessionCreationPolicy;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService; // @Service 로 등록되어 있어야 함
  private final OAuth2SuccessHandler oAuth2SuccessHandler;       // @Component 로 등록되어 있어야 함

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, SecurityContextRepository repo) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(c -> c.configurationSource(corsConfigurationSource()))
      .authorizeHttpRequests(auth -> auth
        // context-path(/EVLink_backend-main)는 자동으로 붙으므로 여기 쓰지 않습니다.
        .requestMatchers(
          "/",                 
          "/uploads/**",
          "/oauth2/**",
          "/login/oauth2/**",
          "/api/auth/**",      // 세션조회/로그아웃 등 공개
          "/notice/**",
          "/api/reservation/**",
          "/emotion/**"
        ).permitAll()
        .requestMatchers(HttpMethod.POST, "/api/PLogin/**").permitAll()
        // 나머지 API는 인증 필요
        .requestMatchers("/api/**").authenticated()
        // 기타는 필요에 따라 공개/보호 선택
        .anyRequest().permitAll()
      )
      .oauth2Login(o -> o
        .userInfoEndpoint(u -> u.userService(customOAuth2UserService))
        .successHandler(oAuth2SuccessHandler)
      )
      .logout(l -> l
        .logoutUrl("/api/auth/logout")   // 프론트가 POST하는 경로
        .clearAuthentication(true)
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID")
      )
      // API 접근 미인증 시, 로그인 페이지로 리다이렉트 대신 401 반환
      .exceptionHandling(ex -> ex
        .defaultAuthenticationEntryPointFor(
          (req, res, e) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED),
          new AntPathRequestMatcher("/api/**")
        )
      )
      // 저장소 명시
      .securityContext(c -> c.securityContextRepository(repo));

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of(
      "http://localhost:3000",
      "http://localhost:3001",
//      "http://192.168.0.90:3000",
//      "http://192.168.0.90:3001",
      "http://127.0.0.1:3000",
      "http://127.0.0.1:3001",
      "http://3.34.69.170:3000",
	  "http://3.34.69.170:3001"
    ));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
    cfg.setAllowCredentials(true);
    cfg.setMaxAge(3600L);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
  
  // 2025.09.16
  @Bean
  public SecurityContextRepository securityContextRepository() {
      return new HttpSessionSecurityContextRepository();
  }
}
